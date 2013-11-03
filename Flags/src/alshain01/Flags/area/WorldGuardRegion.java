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
	private ProtectedRegion region = null;
	private String worldName = null;
	
	// ******************************
	// Constructors
	// ******************************
	public WorldGuardRegion(Location location) {
		this.worldName = location.getWorld().getName();
		ApplicableRegionSet regionSet = WGBukkit.getRegionManager(this.getWorld()).getApplicableRegions(location);
		if(regionSet == null) { this.region = null; }
		else {
			int currentPriority = -2147483648;
			
			for(ProtectedRegion region : regionSet) {
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
		if (!isArea()) { return null; }
		return region.getId();
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
		return region != null && worldName != null;
	}

	// ******************************
	// Comparable Interface
	// ******************************
	/**
	 * 0 if the the worlds are the same, 3 if they are not.
	 * @return The value of the comparison.
	 */
	@Override
	public int compareTo(Area a) {
		return (a instanceof WorldGuardRegion && a.getSystemID().equals(this.getSystemID())) ? 0 : 3;
	}
	
	// ******************************
	// Removable Interface
	// ******************************
	@Override
	public void remove() {
 	   Flags.getDataStore().write(getDataPath(), (String)null);
	}
}
