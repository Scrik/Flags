package alshain01.Flags;

import java.util.HashSet;
import java.util.Set;

import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.events.ClaimDeletedEvent;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import uk.co.jacekk.bukkit.infiniteplots.InfinitePlots;
import uk.co.jacekk.bukkit.infiniteplots.plot.PlotLocation;
import alshain01.Flags.area.Area;
import alshain01.Flags.area.FactionsTerritory;
import alshain01.Flags.area.GriefPreventionClaim;
import alshain01.Flags.area.GriefPreventionClaim78;
import alshain01.Flags.area.InfinitePlotsPlot;
import alshain01.Flags.area.PlotMePlot;
import alshain01.Flags.area.ResidenceClaimedResidence;
import alshain01.Flags.area.World;
import alshain01.Flags.area.WorldGuardRegion;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.event.ResidenceDeleteEvent;
import com.massivecraft.factions.entity.BoardColls;
import com.massivecraft.factions.event.FactionsEventDisband;
import com.massivecraft.mcore.ps.PS;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.worldcretornica.plotme.PlotManager;

/**
 * Class for retrieving area system specific information.
 * 
 * @author Alshain01
 */
public final class Director {
	private static class FactionsCleaner implements Listener {
		@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
		private void onFactionDisband(FactionsEventDisband e) {
			for (final org.bukkit.World world : Bukkit.getWorlds()) {
				new FactionsTerritory(world, e.getFaction().getId()).remove();
			}
		}
	}

	private static class GriefPreventionCleaner implements Listener {
		@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
		private void onClaimDeleted(ClaimDeletedEvent e) {
			// Cleanup the database, keep the file from growing too large.
			new GriefPreventionClaim78(e.getClaim().getID()).remove();
		}
	}

	public enum LandSystem {
		NONE(null, null),
		GRIEF_PREVENTION("GriefPrevention",	"Grief Prevention"),
		WORLDGUARD("WorldGuard", "WorldGuard"),
		RESIDENCE("Residence", "Residence"),
		INFINITEPLOTS("InfinitePlots", "InfinitePlots"),
		FACTIONS("Factions", "Factions"),
		PLOTME("PlotMe", "PlotMe");

		/**
		 * Gets the enumeration that matches the case sensitive plugin.yml name.
		 * 
		 * @return The enumeration. LandSystem.NONE if no matches found.
		 */
		public static LandSystem getByName(String name) {
			for (final LandSystem p : LandSystem.values()) {
				if (name.equals(p.pluginName)) {
					return p;
				}
			}
			return LandSystem.NONE;
		}

		private String pluginName = null, displayName = null;

		private LandSystem(String name, String displayName) {
			pluginName = name;
			this.displayName = displayName;
		}

		/**
		 * Gets a user friendly string, including spaces, for the plug-in.
		 * 
		 * @return The user friendly name of the plugin
		 */
		public String getDisplayName() {
			return displayName;
		}

		/**
		 * Gets the plug-in name as indicated in it's plugin.yml
		 * 
		 * @return The case sensitive plugin.yml name for the enumerated value
		 */
		@Override
		public String toString() {
			return pluginName;
		}
	}

	private static class ResidenceCleaner implements Listener {
		@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
		private void onResidenceDelete(ResidenceDeleteEvent e) {
			// Cleanup the database, keep the file from growing too large.
			new ResidenceClaimedResidence(e.getResidence().getName()).remove();
		}
	}

	/*
	 * Database cleanup monitors
	 */
	protected static void enableMrClean(PluginManager pm) {
		switch (getSystem()) {
		case GRIEF_PREVENTION:
			if (Float.valueOf(pm.getPlugin(getSystem().toString())
					.getDescription().getVersion().substring(0, 3)) >= 7.8) {

				pm.registerEvents(new GriefPreventionCleaner(),	Flags.getInstance());
			}
			break;
		case RESIDENCE:
			pm.registerEvents(new ResidenceCleaner(), Flags.getInstance());
			break;
		case FACTIONS:
			pm.registerEvents(new FactionsCleaner(), Flags.getInstance());
			break;
		default:
			break;
		}
	}

	/*
	 * Gets the area at a specific location if one exists, otherwise null
	 */
	private static Area getArea(Location location) {
		switch (getSystem()) {
		case GRIEF_PREVENTION:
			final Plugin plugin = Flags.getInstance().getServer()
					.getPluginManager().getPlugin("GriefPrevention");
			if (Float.valueOf(plugin.getDescription().getVersion().substring(0, 3)) >= 7.8) {
				return new GriefPreventionClaim78(location);
			} else if (Float.valueOf(plugin.getDescription().getVersion().substring(0, 3)) == 7.7) {
				return new GriefPreventionClaim(location);
			}
			Flags.getInstance().getLogger().warning("Unsupported Grief Prevention version detected. "
					+ "Shutting down integrated support. Only world flags will be available.");
			Flags.currentSystem = LandSystem.NONE;
			return null;
		case WORLDGUARD:
			return new WorldGuardRegion(location);
		case RESIDENCE:
			return new ResidenceClaimedResidence(location);
		case INFINITEPLOTS:
			return new InfinitePlotsPlot(location);
		case FACTIONS:
			return new FactionsTerritory(location);
		case PLOTME:
			return new PlotMePlot(location);
		default:
			return null;
		}
	}

	/**
	 * Gets an area by system specific name. The name is formatted based on the
	 * system.
	 * 
	 * GriefPrevention = ID number
	 * WorldGuard = worldname.regionname
	 * Residence = Residence name OR ResidenceName.SubzoneName
	 * InifitePlots = worldname.PlotLoc (X;Z)
	 * Factions = worldname.FactionID
	 * PlotMe = worldname.PlotID
	 * 
	 * @param name
	 *            The system specific name of the area or world name
	 * @return The Area requested, may be null in cases of invalid system
	 *         selection.
	 */
	public static Area getArea(String name) {
		String[] path;
		switch (getSystem()) {
		case GRIEF_PREVENTION:
			final Plugin plugin = Flags.getInstance().getServer()
					.getPluginManager().getPlugin("GriefPrevention");
			if (Float.valueOf(plugin.getDescription().getVersion()) >= 7.8) {
				final Long ID = Long.parseLong(name);
				return new GriefPreventionClaim78(ID);
			} else if (Float.valueOf(plugin.getDescription().getVersion()) == 7.7) {
				final Long ID = Long.parseLong(name);
				return new GriefPreventionClaim(ID);
			}
			Flags.getInstance().getLogger().warning("Unsupported Grief Prevention version detected. "
					+ "Shutting down integrated support. Only world flags will be available.");
			Flags.currentSystem = LandSystem.NONE;
			return null;
		case RESIDENCE:
			return new ResidenceClaimedResidence(name);
		case WORLDGUARD:
			path = name.split("\\.");
			return new WorldGuardRegion(Bukkit.getWorld(path[0]), path[1]);
		case INFINITEPLOTS:
			path = name.split("\\.");
			final String[] coords = path[1].split(";");
			return new InfinitePlotsPlot(Bukkit.getWorld(path[0]),
					Integer.valueOf(coords[0]), Integer.valueOf(coords[1]));
		case FACTIONS:
			path = name.split("\\.");
			return new FactionsTerritory(Bukkit.getWorld(path[0]), path[1]);
		case PLOTME:
			path = name.split("\\.");
			return new PlotMePlot(Bukkit.getWorld(path[0]), path[1]);
		default:
			return null;
		}
	}

	/**
	 * Gets an area from the data store at a specific location.
	 * 
	 * @param location
	 *            The location to request an area.
	 * @return An Area from the configured system or the world if no area is
	 *         defined.
	 */
	public static Area getAreaAt(Location location) {
		// hasArea() and area.isArea() may not necessarily be the same for all
		// systems,
		// however hasArea() is faster than constructing an area object, and
		// calling both has minimal impact.
		// This is done purely for efficiency.
		if (!hasArea(location)) {
			return new World(location);
		}
		final Area area = getArea(location);
		return area.isArea() ? area : new World(location);
	}

	/**
	 * Gets a set of system specific area names stored in the database
	 * 
	 * @return A list containing all the area names.
	 */
	public static Set<String> getAreaNames() {
		Set<String> worlds, localAreas;
		final Set<String> allAreas = new HashSet<String>();
		switch (getSystem()) {
		case GRIEF_PREVENTION:
			return Flags.getDataStore().readKeys("GriefPreventionData");
		case RESIDENCE:
			return Flags.getDataStore().readKeys("ResidenceData");
		case WORLDGUARD:
			worlds = Flags.getDataStore().readKeys("WorldGuardData");
			for (final String world : worlds) {
				localAreas = Flags.getDataStore().readKeys("WorldGuardData." + world);
				for (final String area : localAreas) {
					allAreas.add(world + "." + area);
				}
			}
			return allAreas;
		case INFINITEPLOTS:
			worlds = Flags.getDataStore().readKeys("InfinitePlotsData");
			for (final String world : worlds) {
				localAreas = Flags.getDataStore().readKeys("InfinitePlotsData." + world);
				for (final String localArea : localAreas) {
					allAreas.add(world + "." + localArea);
				}
			}
			return allAreas;
		case FACTIONS:
			worlds = Flags.getDataStore().readKeys("FactionsData");
			for (final String world : worlds) {
				localAreas = Flags.getDataStore().readKeys("FactionsData." + world);
				for (final String localArea : localAreas) {
					if (!allAreas.contains(localArea)) {
						allAreas.add(world + "." + localArea);
					}
				}
			}
			return allAreas;
		case PLOTME:
			worlds = Flags.getDataStore().readKeys("PlotMeData");
			for (final String world : worlds) {
				localAreas = Flags.getDataStore().readKeys("PlotMeData." + world);
				for (final String localArea : localAreas) {
					allAreas.add(world + "." + localArea);
				}
			}
			return allAreas;
		default:
			return null;
		}
	}

	/**
	 * Gets the current land system in use by Flags.
	 * 
	 * @return The current Land Management System in use by Flags.
	 */
	public static LandSystem getSystem() {
		return Flags.currentSystem;
	}

	/**
	 * Gets a user friendly name of the area type of the configured system,
	 * capitalized. For use when the name is required even though an area does
	 * not exist (such as error messages). If you have an area instance, use
	 * Area.getAreaType() instead.
	 * 
	 * @return The user friendly name.
	 */
	public static String getSystemAreaType() {
		switch (getSystem()) {
		case GRIEF_PREVENTION:
			return Message.GriefPrevention.get();
		case WORLDGUARD:
			return Message.WorldGuard.get();
		case RESIDENCE:
			return Message.Residence.get();
		case INFINITEPLOTS:
			return Message.InfinitePlots.get();
		case FACTIONS:
			return Message.Factions.get();
		case PLOTME:
			return Message.PlotMe.get();
		default:
			return Message.World.get();
		}
	}

	/*
	 * Performs a fast check to see if an area is defined at a location
	 */
	private static boolean hasArea(Location location) {
		switch (getSystem()) {
		case GRIEF_PREVENTION:
			return GriefPrevention.instance.dataStore.getClaimAt(location,
					false) != null;
		case WORLDGUARD:
			return WGBukkit.getRegionManager(location.getWorld())
					.getApplicableRegions(location).size() != 0;
		case RESIDENCE:
			return Residence.getResidenceManager().getByLoc(location) != null;
		case INFINITEPLOTS:
			return InfinitePlots.getInstance().getPlotManager()
					.getPlotAt(PlotLocation.fromWorldLocation(location)) != null;
		case FACTIONS:
			return BoardColls.get().getFactionAt(PS.valueOf(location)) != null;
		case PLOTME:
			return PlotManager.getPlotById(location) != null;
		default:
			return false;
		}
	}

	/**
	 * Checks if a player is in Pvp combat that is being monitored by the system
	 * 
	 * @param player
	 *            The player to request information for
	 * @return True if the player is in pvp combat, false is not or if system is
	 *         unsupported.
	 */
	public static boolean inPvpCombat(Player player) {
		return getSystem() != LandSystem.GRIEF_PREVENTION ? false
				: GriefPrevention.instance.dataStore.getPlayerData(player.getName()).inPvpCombat();
	}

	private Director() {
	}
}