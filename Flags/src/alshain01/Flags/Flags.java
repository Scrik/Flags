package alshain01.Flags;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.command.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import alshain01.Flags.Director.LandSystem;
import alshain01.Flags.Updater.UpdateResult;
import alshain01.Flags.area.Area;
import alshain01.Flags.area.Default;
import alshain01.Flags.area.World;
import alshain01.Flags.commands.Command;
import alshain01.Flags.data.CustomYML;
import alshain01.Flags.data.DataStore;
import alshain01.Flags.data.YamlDataStore;
import alshain01.Flags.importer.GPFImport;
import alshain01.Flags.metrics.MetricsManager;

/**
 * Flags
 * 
 * @author Alshain01
 */
public class Flags extends JavaPlugin{

	protected static CustomYML messageStore;
	protected static LandSystem currentSystem = LandSystem.NONE;

	private static Flags instance;
	private static DataStore dataStore;
	private static Updater updater = null;
	private static Economy economy = null;
	private static Boolean debug = false;
	private static final Registrar flagRegistrar = new Registrar();
	
	// Cached areas
	private static ConcurrentHashMap<UUID, Area> worldAreas;
	private static ConcurrentHashMap<UUID, Area> defaultAreas;

	/**
	 * Called when this plug-in is enabled
	 */
	@Override
	public void onEnable(){
		instance = this;
		
		// Create the configuration file if it doesn't exist
		this.saveDefaultConfig();
		debug = this.getConfig().getBoolean("Flags.Debug");
		
		// Update script
		if(this.getConfig().getBoolean("Flags.Update.Check")) {
			String key = this.getConfig().getString("Flags.Update.ServerModsAPIKey");
			if(this.getConfig().getBoolean("Flags.Update.Download")) {
				updater = new Updater(this, 65024, this.getFile(), Updater.UpdateType.DEFAULT, key, true);
			} else {
				updater = new Updater(this, 65024, this.getFile(), Updater.UpdateType.NO_DOWNLOAD, key, false);
			}
			
			if(updater.getResult() == UpdateResult.UPDATE_AVAILABLE) {
				Bukkit.getServer().getConsoleSender().sendMessage("[Flags] " + ChatColor.DARK_PURPLE + 
						"The version of Flags that this server is running is out of date. "
						+ "Please consider updating to the latest version at dev.bukkit.org/bukkit-plugins/flags/.");
			} else if(updater.getResult() == UpdateResult.SUCCESS) {
				Bukkit.getServer().reload();
			}
		}
		this.getServer().getPluginManager().registerEvents(new FlagsListener(), instance);

		// Create the specific implementation of DataStore
		// TODO: Add sub-interface for SQL
		dataStore = new YamlDataStore(this);
		messageStore = new CustomYML(this, "message.yml");
		messageStore.saveDefaultConfig();
		
		if (!dataStore.exists(this)) {
			// New installation
			if (!dataStore.create(this)) {
				this.getLogger().warning("Failed to create database schema. Shutting down Flags.");
				this.getServer().getPluginManager().disablePlugin(this);
				return;
			}
		}
		
		// Update the data to current as needed.
		dataStore.update(this);
		
		// Cache the initial world & default areas (loaded before event listeners take effect)
		for(org.bukkit.World w : Bukkit.getServer().getWorlds()) {
			if(!defaultAreas.contains(w.getUID())) {
				worldAreas.put(w.getUID(), new World(w));
				defaultAreas.put(w.getUID(), new Default(w));
			}
		}
		
		// Find the first available land management system
		currentSystem = findSystem(getServer().getPluginManager());
		if (currentSystem == LandSystem.NONE) {
			getLogger().info("No system detected. Only world flags will be available.");
		} else {
			getLogger().info(currentSystem.getDisplayName() + " detected. Enabling integrated support.");
		}
		
		// Check for older database and import as necessary.
		if(currentSystem == LandSystem.GRIEF_PREVENTION && !getServer().getPluginManager().isPluginEnabled("GriefPreventionFlags")) {
			GPFImport.importGPF();
		}
		
		// Enable Vault support
		setupEconomy();
		
		// Load Mr. Clean
		Director.enableMrClean(this.getServer().getPluginManager());
		
		// Load Border Patrol
		if (this.getConfig().getBoolean("Flags.BorderPatrol.Enable")) {
			this.getServer().getPluginManager().registerEvents(new BorderPatrol(), instance);
		}
		
		// Schedule tasks to perform after server is running
		new onEnabledTask().runTask(this);
		
		this.getLogger().info("Flags Has Been Enabled.");
	}
	
	/**
	 * Called when this plug-in is disabled 
	 */
	@Override
	public void onDisable(){
		//if(dataStore instanceof SQLDataStore) { ((SQLDataStore)dataStore).close(); }
		getLogger().info("Flags Has Been Disabled.");
	}
	
	/**
	 * Gets the static instance of Flags.
	 * 
	 * @return The vault economy.
	 */
	public static Flags getInstance() {
		return instance;
	}
	
	/**
	 * Gets the DataStore used by Flags.
	 * In most cases, plugins should not attempt to access this directly.
	 * 
	 * @return The vault economy.
	 */
	public static DataStore getDataStore() {
		return dataStore;
	}
	
	/**
	 * Gets the registrar for this instance of Flags.
	 * 
	 * @return The flag registrar.
	 */
	public static Registrar getRegistrar() {
		return flagRegistrar;
	}
    
	/**
	 * Gets the vault economy for this instance of Flags.
	 * 
	 * @return The vault economy.
	 */
	public static Economy getEconomy() {
		return economy;
	}
	
	/**
	 * Gets a cached default area.
	 * 
	 * @param world The world to get the default area for.
	 * @return The default area
	 */
	public static Area getCachedDefaultArea(org.bukkit.World world) {
		return defaultAreas.get(world.getUID());
	}
	
	/**
	 * Gets a cached world area.
	 * 
	 * @param world The world to get the default area for.
	 * @return The default area
	 */
	public static Area getCachedWorldArea(org.bukkit.World world) {
		return worldAreas.get(world.getUID());
	}
		
	/**
	 * Executes the given command, returning its success 
	 * 
	 * @param sender Source of the command
	 * @param cmd    Command which was executed
	 * @param label  Alias of the command which was used
	 * @param args   Passed command arguments 
	 * @return		 true if a valid command, otherwise false
	 * 
	 */
	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args){
		if(cmd.getName().equalsIgnoreCase("flag")) {
			return Command.onFlagCommand(sender, args);
		}
		
		if(cmd.getName().equalsIgnoreCase("bundle")) {
			return Command.onBundleCommand(sender, args);
		}
		return false;
	}
	
	/**
	 * Returns true if the provided string represents a
	 * version number that is equal to or lower than the
	 * current Bukkit API version.
	 * 
	 * String should be formatted with 3 numbers: x.y.z
	 * 
	 * @return True if the version provided is compatible
	 */	
	public static boolean checkAPI(String version) {
		float APIVersion = Float.valueOf(Bukkit.getServer().getBukkitVersion().substring(0, 3));
		float CompareVersion = Float.valueOf(version.substring(0, 3));
		int APIBuild = Integer.valueOf(Bukkit.getServer().getBukkitVersion().substring(4, 5));
		int CompareBuild = Integer.valueOf(version.substring(4, 5));
		
		if (APIVersion > CompareVersion || (APIVersion == CompareVersion && APIBuild >= CompareBuild)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Sends a debug message through the Flags logger if the plug-in is a development build.
	 * @param message The debug message
	 */
	public static final void Debug(String message) {
		if (debug) {
			Flags.instance.getLogger().info("DEBUG: " + message);
		}
	}
	
	/*
	 * Register with the Vault economy plugin.
	 * 
	 * @return True if the economy was successfully configured. 
	 */
    private static boolean setupEconomy()
    {
    	if (!Flags.instance.getServer().getPluginManager().isPluginEnabled("Vault")) { return false; }
        RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager()
        		.getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
        	economy = economyProvider.getProvider();
        }

        return (economy != null);
    }
	
	
	/*
	 * Acquires the land management plugin.
	 */
	private static LandSystem findSystem(PluginManager pm) {
		List<?> pluginList = Flags.instance.getConfig().getList("Flags.AreaPlugins");

		for(Object o : pluginList) {
			if (pm.isPluginEnabled((String)o)) {
				return LandSystem.getByName((String)o);
			}
		}
		return LandSystem.NONE;				
	}
	
	/*
	 * Contains event listeners required for plugin maintenance.
	 */
	private static class FlagsListener implements Listener {
		// Update listener
		@EventHandler(ignoreCancelled = true)
		private void onPlayerJoin(PlayerJoinEvent e) {
			if(e.getPlayer().hasPermission("flags.admin.notifyupdate") && updater.getResult() == UpdateResult.UPDATE_AVAILABLE) {
					e.getPlayer().sendMessage( ChatColor.DARK_PURPLE + 
							"The version of Flags that this server is running is out of date. "
							+ "Please consider updating to the latest version at dev.bukkit.org/bukkit-plugins/flags/.");
			}
		}
		
		// Cache any worlds added or removed by plugins.
		@EventHandler
		private void onWorldLoad(WorldLoadEvent e) {
			if(!defaultAreas.contains(e.getWorld().getUID())) {
				worldAreas.put(e.getWorld().getUID(), new World(e.getWorld()));
				defaultAreas.put(e.getWorld().getUID(), new World(e.getWorld()));
			}
		}
		
		@EventHandler
		private void onWorldUnload(WorldUnloadEvent e) {
			worldAreas.remove(e.getWorld().getUID());
			defaultAreas.remove(e.getWorld().getUID());
		}
	}
	
	/*
	 * Tasks the must be run only after the entire sever has loaded.
	 * Runs on first server tick.
	 */
	private class onEnabledTask extends BukkitRunnable {
		public void run() {
			for(String b : Bundle.getBundleNames()) {
				Debug("Registering Bundle Permission:" + b);
				Permission perm = new Permission("flags.bundle." + b, "Grants ability to use the bundle " + b, PermissionDefault.FALSE);
				perm.addParent("flags.bundle", true);
				Bukkit.getServer().getPluginManager().addPermission(perm);
			}
			
			if(!debug && checkAPI("1.3.2")) {
				MetricsManager.StartMetrics();
			}
	    }
	}
}
