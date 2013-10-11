package alshain01.Flags.area;

import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import alshain01.Flags.Flags;
import alshain01.Flags.Message;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class WorldGuardRegion extends Area implements Subdivision, Removable {
	private final static String dataHeader = "WorldGuardData.";
	private ProtectedRegion region;
	private org.bukkit.World world;
	
	private String getInheritPath() {
		return dataHeader + world.getName() + "." + getSystemID() + "." + getSystemSubID() + "." + "InheritParent";
	}

	// ******************************
	// Constructors
	// ******************************	
	public WorldGuardRegion(Location location) {
		this.world = location.getWorld();
		ApplicableRegionSet regionSet = WGBukkit.getRegionManager(world).getApplicableRegions(location);
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
	
	public WorldGuardRegion(org.bukkit.World world, String name) {
		this.world = world;
		region = WGBukkit.getRegionManager(world).getRegionExact(name);
	}

	// ******************************
	// Area Interface
	// ******************************
	protected String getDataPath() {
		if(isSubdivision() && !isInherited()) {
			return dataHeader + world.getName() + "." + getSystemID() + "." + getSystemSubID();
		}
		return dataHeader + world.getName() + "." + getSystemID();
	}
	
	@Override
	public String getSystemID() {
		if(isSubdivision()) {
			return region.getParent().getId();
		} else if (isArea()) {
			return region.getId();
		} else {
			return null;
		}
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
		return world;
	}
	
	@Override
	public boolean isArea() {
		return(region != null);
	}

	@Override
	public boolean hasPermission(Player player) {
		if (region.getOwners().contains(player.getName())) {
			if (player.hasPermission("flags.flag.set")) {
				return true;
			}
			return false;
		}
		
		if (player.hasPermission("flags.flag.set.others")) { return true; }
		return false;
	}

	@Override
	public boolean hasBundlePermission(Player player) {
		if (region.getOwners().contains(player.getName())) {
			if (player.hasPermission("flags.bundle.set")) {
				return true;
			}
			return false;
		}
		
		if (player.hasPermission("flags.bundle.set.others")) { return true; }
		return false;
	}
	
	// ******************************
	// Comparable Interface
	// ******************************
	@Override
	public int compareTo(Area a) {
		if(a instanceof WorldGuardRegion && a.getSystemID().equals(this.getSystemID())) {
			return 0;
		}
		
		// Return a -1 if this is a subdivision of the provided area
		if(a instanceof Subdivision	&& isSubdivision() 
				&& String.valueOf(region.getParent().getId()).equalsIgnoreCase(a.getSystemID())) {
			return -1;
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
 	   Flags.instance.dataStore.write(getDataPath() + valueFooter, (String)null);
	}
	
	// ******************************
	// Subdivision Interface
	// ******************************
	@Override
	public boolean isSubdivision() {
		return (isArea() && region.getParent() != null);
	}
	
	@Override
	public String getSystemSubID() {
		if(isSubdivision()) {
			return String.valueOf(region.getId());
		}
		return null;
	}
	
	@Override
	public boolean isInherited() {
		if(!isSubdivision()) { return false; }
		
    	String value = Flags.instance.dataStore.read(getInheritPath());
    	if (value == null) { return true; }
    	return Boolean.valueOf(value);
	}
	
	@Override
	public boolean setInherited(Boolean value) {
		if(!isSubdivision()) { return false; }
		String storedValue = Flags.instance.dataStore.read(getInheritPath());
		
		if(value == null) {
			if (storedValue != null) {
				value = !Boolean.valueOf(storedValue);
			} else {
		    	value = false;
			}
		}
		
		Flags.instance.dataStore.write(getInheritPath(), String.valueOf(value));
		return true;
	}
}
