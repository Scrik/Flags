package alshain01.Flags.area;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import alshain01.Flags.Flags;
import alshain01.Flags.Message;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;

public class ResidenceClaimedResidence extends Area implements Removable, Subdivision {
	private final static String dataHeader = "ResidenceData.";
	private ClaimedResidence residence;
	
	private String getInheritPath() {
		return dataHeader + getSystemSubID() + "." + "InheritParent";
	}	
	
	// ******************************
	// Constructors
	// ******************************	
	public ResidenceClaimedResidence(Location location) {
		residence = Residence.getResidenceManager().getByLoc(location);
	}
	
	public ResidenceClaimedResidence(String name) {
		residence = Residence.getResidenceManager().getByName(name);
	}

	// ******************************
	// Area Interface
	// ******************************
	@Override
	protected String getDataPath() {
		if(isSubdivision() && !isInherited()) {
			return dataHeader + getSystemSubID();
		}
		return dataHeader + getSystemID();
	}
	
	@Override
	public String getSystemID() {
		if (isSubdivision()) {
			return residence.getParent().getName();

		}
		return residence.getName();
	}

	@Override
	public String getAreaType() {
		return Message.Residence.get();
	}
	
	@Override
	public Set<String> getOwners() {
		return new HashSet<String>(Arrays.asList(residence.getOwner()));
	}
	
	@Override
	public org.bukkit.World getWorld() {
		return Bukkit.getServer().getWorld(residence.getWorld());
	}
	
	@Override
	public boolean isArea() {
		return (residence != null);
	}
	
	// ******************************
	// Comparable Interface
	// ******************************
	@Override
	public int compareTo(Area a) {
		if(a instanceof ResidenceClaimedResidence && a.getSystemID().equals(this.getSystemID())) {
			return 0;
		}
		
		// Return a -1 if this is a subdivision of the provided area
		if(a instanceof Subdivision && isSubdivision()
				&& String.valueOf(residence.getParent().getName()).equalsIgnoreCase(a.getSystemID())) {
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
		Flags.getDataStore().write(getDataPath(), (String)null);
	}

	// ******************************
	// Subdivision Interface
	// ******************************
	@Override
	public boolean isSubdivision() {
		return (isArea() && residence.getParent() != null);
	}
	
	@Override
	public String getSystemSubID() {
		if(isSubdivision()) {
			return residence.getName();
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
}
