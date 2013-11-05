package alshain01.Flags.area;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;

import alshain01.Flags.Flags;
import alshain01.Flags.Message;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;

public class GriefPreventionClaim extends Area implements Removable, Siege, Administrator {
	protected final static String dataHeader = "GriefPreventionData.";
	protected Claim claim;
	
	// ******************************
	// Constructors
	// ******************************
	/**
	 * Creates an instance of GriefPreventionClaim based on a Bukkit Location
	 * @param location The Bukkit location
	 */
	public GriefPreventionClaim(Location location) {
		this.claim = GriefPrevention.instance.dataStore.getClaimAt(location, false, null);
	}

	/**
	 * Creates an instance of GriefPreventionClaim based on a claim ID
	 * @param ID The claim ID
	 */
	public GriefPreventionClaim(long ID) {
		this.claim = GriefPrevention.instance.dataStore.getClaim(ID);
	}
	
	public Claim getClaim() {
		return claim;
	}
	
	// ******************************
	// Area Interface
	// ******************************
	@Override
	protected String getDataPath() {
		return dataHeader + getSystemID();
	}
	
	@Override
	public String getSystemID() {
		if(isArea() && getClaim().parent != null) {
			return String.valueOf(getClaim().parent.getID());
		} else if(isArea()) {
			return String.valueOf(getClaim().getID());
		} else {
			return null;
		}
	}
	
	@Override
	public String getAreaType() {
		return Message.GriefPrevention.get();
	}
	
	@Override
	public Set<String> getOwners() {
		return new HashSet<String>(Arrays.asList(getClaim().getOwnerName()));
	}
	
	@Override
	public org.bukkit.World getWorld() {
		return getClaim().getGreaterBoundaryCorner().getWorld();
	}
	
	@Override
	public boolean isArea() {
		return getClaim() != null;
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
		return (a instanceof GriefPreventionClaim && a.getSystemID().equals(this.getSystemID())) ? 0 : 3;
	}
	
	// ******************************
	// Removable Interface
	// ******************************
	@Override
	public void remove() {
 	   Flags.getDataStore().write(getDataPath(), (String)null);
	}
	
	// ******************************
	// Siege Interface
	// ******************************
	@Override
	public boolean isUnderSiege() {
		return !(getClaim() == null || getClaim().siegeData == null);
	}

	// ******************************
	// Admin Interface
	// ******************************
	@Override
	public boolean isAdminArea() {
		return getClaim().isAdminClaim();
	}
}

