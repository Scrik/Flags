package alshain01.Flags.area;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import alshain01.Flags.Flags;
import alshain01.Flags.Message;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class WorldGuardRegion extends Area implements Removable {
	private final static String dataHeader = "WorldGuardData.";
	private ProtectedRegion region;
	private String worldName;
	
	// ******************************
	// Constructors
	// ******************************	
	public WorldGuardRegion(Location location) {
		this.worldName = location.getWorld().getName();
		ApplicableRegionSet regionSet = WGBukkit.getRegionManager(this.getWorld()).getApplicableRegions(location);
		if(regionSet == null) { this.region = null; }
		else {
			int currentPriority = -2147483648;
			for (ProtectedRegion region : regionSet) {
				if(region.getPriority() >= currentPriority) {
					this.region = region;
					currentPriority = region.getPriority();
				}
			}
		}
	}
	
	public WorldGuardRegion(String worldName, String name) {
		this.worldName = worldName;
		region = WGBukkit.getRegionManager(this.getWorld()).getRegionExact(name);
	}

	// ******************************
	// Area Interface
	// ******************************
	protected String getDataPath() {
		return dataHeader + worldName + "." + getSystemID();
	}
	
	@Override
	public String getSystemID() {
		if (isArea()) {
			return region.getId();
		}
		return null;
	}
	
	@Override
	public String getAreaType() {
		return Message.WorldGuard.get();
	}

	@Override
	public Set<String> getOwners() {
		return region.getOwners().getPlayers();
	}

	@Override
	public org.bukkit.World getWorld() {
		return Bukkit.getServer().getWorld(worldName);
	}
	
	@Override
	public boolean isArea() {
		return(region != null);
	}

	// ******************************
	// Comparable Interface
	// ******************************
	@Override
	public int compareTo(Area a) {
		if(a instanceof WorldGuardRegion && a.getSystemID().equals(this.getSystemID())) {
			return 0;
		}
		return 3;
	}
	
	// ******************************
	// Removable Interface
	// ******************************
	/**
	 * Permanently removes the area from the data store
	 * USE CAUTION!
	 */
	@Override
	public void remove() {
 	   Flags.dataStore.write(getDataPath(), (String)null);
	}
}
