package alshain01.Flags;

import java.util.HashSet;
import java.util.Set;

import me.ryanhamshire.GriefPrevention.events.ClaimDeletedEvent;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import com.bekvon.bukkit.residence.event.ResidenceDeleteEvent;

import alshain01.Flags.area.Area;
import alshain01.Flags.area.GriefPreventionClaim78;
import alshain01.Flags.area.GriefPreventionClaim77;
import alshain01.Flags.area.InfinitePlotsPlot;
import alshain01.Flags.area.ResidenceClaimedResidence;
import alshain01.Flags.area.World;
import alshain01.Flags.area.WorldGuardRegion;

/**
 * Class for retrieving area system specific information.
 * 
 * @author Alshain01
 */
public final class Director {
	public static enum LandSystem {
		NONE(null, null), 
		GRIEF_PREVENTION("GriefPrevention", "Grief Prevention"),
		WORLDGUARD("WorldGuard", "WorldGuard"),
		RESIDENCE("Residence", "Residence"),
		INFINITEPLOTS("InfinitePlots","InfinitePlots");
		
		private String pluginName = null;
		private String displayName = null;
		
		private LandSystem(String name, String displayName) {
			this.pluginName = name;
			this.displayName = displayName;
		}
		
		/**
		 * @return The case sensitive plug.yml name for the enumerated value
		 */
		@Override
		public String toString() {
			return this.pluginName;
		}
		
		/**
		 * @return The user friendly name of the plugin
		 */
		public String getDisplayName() {
			return this.displayName;
		}
		
		/**
		 * @return The enumeration associated with the provided plugin.yml name. (Case sensitive)
		 */
		public final static LandSystem getByName(String name) {
			for(LandSystem p : LandSystem.values()) {
				if(name.equals(p.pluginName)) { return p; }
			}
			return LandSystem.NONE;
		}
	}
	
	protected static void enableMrClean(PluginManager pm) {
		if(getSystem() == LandSystem.GRIEF_PREVENTION) {
			if(Float.valueOf(pm.getPlugin(getSystem().toString())
					.getDescription().getVersion().substring(0,3)) >= 7.8) {
				
				pm.registerEvents(new GriefPreventionCleaner(), Flags.instance);
			}
		} else if(getSystem() == LandSystem.RESIDENCE) {
			pm.registerEvents(new ResidenceCleaner(), Flags.instance);
		}
	}
	
	private static class GriefPreventionCleaner implements Listener {
		@EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
		private void onClaimDeleted(ClaimDeletedEvent e) {
			// Cleanup the database, keep the file from growing too large.
			new GriefPreventionClaim78(e.getClaim().getID()).remove();
		}
	}
	
	private static class ResidenceCleaner implements Listener {
		@EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
		private void onResidenceDelete(ResidenceDeleteEvent e) {
			// Cleanup the database, keep the file from growing too large.
			new ResidenceClaimedResidence(e.getResidence().getName()).remove();
		}
	}
	
	/**
	 * @return The current Land Management System in use by Flags.
	 */
	public final static LandSystem getSystem() {
		return Flags.instance.currentSystem;
	}

	/**
	 * Retrieves an area from the data store at a specific location.
	 *  
	 * @param location The location to request an area.
	 * @return An Area from the configured system or the world if no area is defined.
	 */
	public final static Area getAreaAt(Location location) {
		Area area = null;
		if(getSystem() == LandSystem.GRIEF_PREVENTION) {
			Plugin plugin = Flags.instance.getServer().getPluginManager().getPlugin("GriefPrevention");
			if(Float.valueOf(plugin.getDescription().getVersion().substring(0, 3)) >= 7.8) {
				area = new GriefPreventionClaim78(location);
			} else if(Float.valueOf(plugin.getDescription().getVersion()) == 7.7) {
				area = new GriefPreventionClaim77(location);
			} else {
				Flags.instance.getLogger().warning("Unsupported Grief Prevention version detected. Shutting down integrated support. Only world flags will be available.");
				Flags.instance.currentSystem = LandSystem.NONE;
			}
		}
		else if(getSystem() == LandSystem.WORLDGUARD) { area = new WorldGuardRegion(location); }
		else if(getSystem() == LandSystem.RESIDENCE) { area = new ResidenceClaimedResidence(location); }
		else if(getSystem() == LandSystem.INFINITEPLOTS) { area = new InfinitePlotsPlot(location); }
		
		if(area == null || !area.isArea()) {
			area = new World(location);
		}
		return area;
	}
	
	/**
	 * Retrieves an area by system specific name
	 * GriefPrevention = ID number
	 * WorldGuard = worldname.regionname
	 * Residence = Residence name OR ResidenceName.SubzoneName
	 * InifitePlots = OwnerName.PlotName
	 * 
	 * @param name The system specific name of the area or world name
	 * @return The Area requested, may be null in cases of invalid system selection.
	 */
	public final static Area getArea(String name) {
		if(getSystem() == LandSystem.GRIEF_PREVENTION) {
			Plugin plugin = Flags.instance.getServer().getPluginManager().getPlugin("GriefPrevention");
			if(Float.valueOf(plugin.getDescription().getVersion()) >= 7.8) {
				Long ID = Long.parseLong(name);
				return new GriefPreventionClaim78(ID);
			} else if(Float.valueOf(plugin.getDescription().getVersion()) == 7.7){
				Long ID = Long.parseLong(name);
				return new GriefPreventionClaim77(ID);
			} else {
				Flags.instance.getLogger().warning("Unsupported Grief Prevention version detected. Shutting down integrated support. Only world flags will be available.");
				Flags.instance.currentSystem = LandSystem.NONE;
			}
		} else if(getSystem() == LandSystem.WORLDGUARD) { 
			String[] path = name.split("\\.");
			name = name.replaceAll(path[0] + ".", "");
			return new WorldGuardRegion(Bukkit.getServer().getWorld(path[0]), name);
		} else if(getSystem() == LandSystem.RESIDENCE) { 
			return new ResidenceClaimedResidence(name);
		} else if(getSystem() == LandSystem.INFINITEPLOTS) { 
			String[] path = name.split("\\.");
			return new InfinitePlotsPlot(path[0], path[1]);
		}
		return null;
	}
	
	/**
	 * Returns a list of system specific area names
	 * 
	 * @return A list containing all the area names.
	 */
	public final static Set<String> getAreaNames() {
		if(getSystem() == LandSystem.GRIEF_PREVENTION) { return Flags.instance.dataStore.readKeys("GriefPreventionData"); }
		
		if(getSystem() == LandSystem.WORLDGUARD) {
			Set<String> worlds = Flags.instance.dataStore.readKeys("WorldGuardData");
			Set<String> areas = new HashSet<String>();
			for(String world : worlds) {
				Set<String>localAreas = Flags.instance.dataStore.readKeys("WorldGuardData." + world);
				for(String localArea : localAreas) {
					areas.add(world + "." + localArea);
				}
			}
			
			return areas;
		}
		
		if(getSystem() == LandSystem.RESIDENCE) { return Flags.instance.dataStore.readKeys("ResidenceData"); }
		
		if(getSystem() == LandSystem.INFINITEPLOTS) {
			Set<String> players = Flags.instance.dataStore.readKeys("InfinitePlotsData");
			Set<String> areas = new HashSet<String>();
			for(String player : players) {
				Set<String>localAreas = Flags.instance.dataStore.readKeys("InifitePlotsData." + player);
				for(String localArea : localAreas) {
					areas.add(player + "." + localArea);
				}
			}
			return areas; 
		}

		return null;
	}
	
	/**
	 * Retrieves a list of all flags set in the area.
	 * 
	 * @param area The system specific area name.
	 * @return a list of all flags for the provided area.
	 */
	public final static Set<String> getAreaFlags(String area) {
		return Flags.instance.dataStore.readKeys(area);
	}
	
	/**
	 * Returns a user friendly name of the area type of the configured system, capitalized.
	 * For use when the name is required even though an area does not exist (such as error messages).
	 * If you have an area instance, use Area.getAreaType() instead.
	 * 
	 * @return The user friendly name.
	 */
	public final static String getAreaType() {
		if(getSystem() == LandSystem.GRIEF_PREVENTION) { return Message.GriefPrevention.get(); }
		if(getSystem() == LandSystem.WORLDGUARD) { return Message.WorldGuard.get(); }
		if(getSystem() == LandSystem.RESIDENCE) { return Message.Residence.get(); }
		if(getSystem() == LandSystem.INFINITEPLOTS) { return Message.InfinitePlots.get(); }
		// Should never make it here.
		return Message.World.get();
	}
	
	/**
	 * Retrieves whether or not a player is in Pvp combat that
	 * is being monitored by the system
	 * 
	 * Only supports Grief Prevention.
	 * 
	 * @param player The player to request information for
	 * @return True if in pvp combat, false is not or if system is unsupported.
	 */
	
	public final static boolean inPvpCombat(Player player) {
		if(getSystem() == LandSystem.GRIEF_PREVENTION) {
			return me.ryanhamshire.GriefPrevention.GriefPrevention.instance.dataStore.getPlayerData(player.getName()).inPvpCombat();
		}
		return false;
	}
}