package alshain01.Flags;

import java.util.Iterator;
import java.util.List;

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

import alshain01.Flags.Director.LandSystem;
import alshain01.Flags.Updater.UpdateResult;
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
	
	/**
	 * Gets the static instance of Flags.
	 * 
	 * @return The vault economy.
	 */
	public static Flags getInstance() { return instance; }
	
	/**
	 * Gets the DataStore used by Flags.
	 * In most cases, plugins should not attempt to access this directly.
	 * 
	 * @return The vault economy.
	 */
	public static DataStore getDataStore() { return dataStore; }
	
	/**
	 * Gets the registrar for this instance of Flags.
	 * 
	 * @return The flag registrar.
	 */
	public static Registrar getRegistrar() { return flagRegistrar; }
    
	/**
	 * Gets the vault economy for this instance of Flags.
	 * 
	 * @return The vault economy.
	 */
	public static Economy getEconomy() { return economy; }
	
	/**
	 * Called when this plug-in is enabled
	 */
	@Override
	public void onEnable(){
		instance = this;
		
		// Create the configuration file if it doesn't exist
		saveDefaultConfig();
		debug = getConfig().getBoolean("Flags.Debug");
		
		// Update script
		updatePlugin();

		// Create the specific implementation of DataStore
		// TODO: Add sub-interface for SQL
		messageStore = new CustomYML(this, "message.yml");
		messageStore.saveDefaultConfig();
		
		dataStore = new YamlDataStore(this);
		if (!dataStore.exists(this)) {
			// New installation
			if (!dataStore.create(this)) {
				getLogger().warning("Failed to create database schema. Shutting down Flags.");
				getServer().getPluginManager().disablePlugin(this);
				return;
			}
		}
		dataStore.update(this);
		
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
		Director.enableMrClean(getServer().getPluginManager());
		
		// Load Border Patrol
		if (getConfig().getBoolean("Flags.BorderPatrol.Enable")) {
			getServer().getPluginManager().registerEvents(new BorderPatrol(), this);
		}
		
		// Schedule tasks to perform after server is running
		new onServerEnabledTask().runTask(this);
		
		getLogger().info("Flags Has Been Enabled.");
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
	 * Checks if the provided string represents a version number that is equal
	 * to or lower than the current Bukkit API version.
	 * 
	 * String should be formatted with 3 numbers: x.y.z
	 * 
	 * @return true if the version provided is compatible
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
			instance.getLogger().info("DEBUG: " + message);
		}
	}
	
	/*
	 * Register with the Vault economy plugin.
	 * 
	 * @return True if the economy was successfully configured. 
	 */
    private boolean setupEconomy()
    {
    	if (!getServer().getPluginManager().isPluginEnabled("Vault")) { return false; }
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
	private LandSystem findSystem(PluginManager pm) {
		List<?> pluginList = getConfig().getList("Flags.AreaPlugins");
		
		Iterator<?> iter = pluginList.iterator();
		while(iter.hasNext()) {
			Object o = iter.next();
			if (pm.isPluginEnabled((String)o)) {
				return LandSystem.getByName((String)o);
			}
		}
		return LandSystem.NONE;				
	}
	
	/*
	 * Checks for updates and downloads them depending on server configuration.
	 */
	private void updatePlugin() {
		// Update script
		if(getConfig().getBoolean("Flags.Update.Check")) {
			String key = getConfig().getString("Flags.Update.ServerModsAPIKey");
			if(getConfig().getBoolean("Flags.Update.Download")) {
				updater = new Updater(this, 65024, getFile(), Updater.UpdateType.DEFAULT, key, true);
			} else {
				updater = new Updater(this, 65024, getFile(), Updater.UpdateType.NO_DOWNLOAD, key, false);
			}
			
			if(updater.getResult() == UpdateResult.UPDATE_AVAILABLE) {
				Bukkit.getServer().getConsoleSender().sendMessage("[Flags] " + ChatColor.DARK_PURPLE + 
						"The version of Flags that this server is running is out of date. "
						+ "Please consider updating to the latest version at dev.bukkit.org/bukkit-plugins/flags/.");
			} else if(updater.getResult() == UpdateResult.SUCCESS) {
				Bukkit.getServer().reload();
			}
		}
		getServer().getPluginManager().registerEvents(new FlagsListener(), this);
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
	}
	
	/*
	 * Tasks that must be run only after the entire sever has loaded.
	 * Runs on first server tick.
	 */
	private class onServerEnabledTask extends BukkitRunnable {
		public void run() {
			Iterator<String> iter = Bundle.getBundleNames().iterator();
			while(iter.hasNext()) {
				String b = iter.next();
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
