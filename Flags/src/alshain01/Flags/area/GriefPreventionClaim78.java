package alshain01.Flags.area;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import alshain01.Flags.Flags;
import alshain01.Flags.Message;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;

public class GriefPreventionClaim78 extends Area implements Subdivision, Removable, Siege, Administrator {
	protected final static String dataHeader = "GriefPreventionData.";
	protected Claim claim;
	
	private String getInheritPath() {
		return dataHeader + getSystemID() + "." + getSystemSubID() + "." + "InheritParent";
	}
	
	// ******************************
	// Constructors
	// ******************************
	public GriefPreventionClaim78(Location location) {
		this.claim = GriefPrevention.instance.dataStore.getClaimAt(location, false, null);
	}
	
	public GriefPreventionClaim78(long ID) {
		this.claim = GriefPrevention.instance.dataStore.getClaim(ID);
	}
	
	public GriefPreventionClaim78(long ID, long subID) {
		this.claim = GriefPrevention.instance.dataStore.getClaim(ID);
		if (claim != null) {
			this.claim = claim.getSubClaim(subID);
		}
	}
	
	// ******************************
	// Area Interface
	// ******************************
	@Override
	protected String getDataPath() {
		if(isSubdivision() && !isInherited()) {
			return dataHeader + getSystemID() + "." + getSystemSubID();
		}
		return dataHeader + getSystemID();
	}
	
	@Override
	public String getSystemID() {
		if(isSubdivision()) {
			return String.valueOf(claim.parent.getID());
		}else if(isArea()) {
			return String.valueOf(claim.getID());
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
		return new HashSet<String>(Arrays.asList(claim.getOwnerName()));
	}
	
	@Override
	public org.bukkit.World getWorld() {
		return Bukkit.getServer().getWorld(claim.getClaimWorldName());
	}
	
	@Override
	public boolean isArea() {
		return (claim != null);
	}
	
	// ******************************
	// Comparable Interface
	// ******************************
	/**
	 * 0 if the the claims are the same
	 * -1 if the claim is a subdivision of the provided claim.
	 * 1 if the claim is a parent of the provided claim.
	 * 2 if they are "sister" subdivisions.
	 * 3 if they are completely unrelated.
	 * 
	 * @return The value of the comparison.
	 */
	@Override
	public int compareTo(Area a) {
		if(!(a instanceof GriefPreventionClaim78)) { return 3; }
		
		if (a.getSystemID().equals(this.getSystemID())) {
			// They are related somehow.  We need to figure out how.
			// (We can safely assume instance of subdivision because of the first line)
			
			if(((Subdivision)a).getSystemSubID() == null && this.getSystemSubID() != null) {
				//a is the parent
				return -1;
			}
			
			if(((Subdivision)a).getSystemSubID() != null && this.getSystemSubID() == null) {
				//this is the parent
				return 1;
			}
			
			if(((Subdivision)a).getSystemSubID() != null && this.getSystemSubID() != null) {
				//neither are the parent, but the parent ID is the same
				return 2;
			}
			
			if((((Subdivision)a).getSystemSubID() == null && this.getSystemSubID() == null) || ((Subdivision)a).getSystemSubID().equals(getSystemSubID())) {
				// They are the same claim
				return 0;
			}
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
		Flags.getDataStore().write(getDataPath(), (String)null);
	}
	
	// ******************************
	// Subdivision Interface
	// ******************************
	@Override
	public boolean isSubdivision() {
		return (isArea() && claim.parent != null);
	}
	
	@Override
	public String getSystemSubID() {
		if(isSubdivision()) {
			return String.valueOf(claim.getSubClaimID());
		}
		return null;
	}
	
	@Override
	public boolean isInherited() {
		if(!isSubdivision()) { return false; }

		String value = Flags.getDataStore().read(getInheritPath());
		if (value == null) { return true; }
    	return Boolean.valueOf(value);
	}
	
	@Override
	public boolean setInherited(Boolean value) {
		if(!isSubdivision()) { return false; }
		String storedValue = Flags.getDataStore().read(getInheritPath());
		
		if(value == null) {
			if (storedValue != null) {
				value = !Boolean.valueOf(storedValue);
			} else {
				value = false;
			}
		}
		
		Flags.getDataStore().write(getInheritPath(), String.valueOf(value));
		return true;
	}
	
	// ******************************
	// Siege Interface
	// ******************************
	@Override
	public boolean isUnderSiege() {
		if (claim == null || claim.siegeData == null) { return false; }
		return true;
	}

	// ******************************
	// Admin Interface
	// ******************************
	@Override
	public boolean isAdminArea() {
		return claim.isAdminClaim();
	}
}
