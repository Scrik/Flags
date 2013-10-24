package alshain01.Flags.area;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import alshain01.Flags.Flag;
import alshain01.Flags.Flags;
import alshain01.Flags.Message;

public class Default extends Area {
	private final static String dataHeader = "Default.";
	private org.bukkit.World world;
	
	// ******************************
	// Constructors
	// ******************************
	public Default(org.bukkit.World world) {
		this.world = world;
	}
	
	public Default(Location location) {
		this.world = location.getWorld();
	}
	
	public void reconstruct(org.bukkit.World world) {
		if (this.world != world) {
			this.world = world;
		}
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
		return world.getName();
	}
	
	@Override
	public String getAreaType() {
		return Message.Default.get();
	}
	
	@Override
	public Set<String> getOwners() {
		return new HashSet<String> (Arrays.asList("default"));
	}
	
	@Override
	public org.bukkit.World getWorld() {
		return world;
	}
	
	@Override
	public boolean isArea() {
		// There is always a default.
		return true;
	}
	
	@Override
	public boolean hasPermission(Player player) {
		if (player.hasPermission("flags.flag.set.default")) { return true; }
		return false;
	}

	@Override
	public boolean hasBundlePermission(Player player) {
		if (player.hasPermission("flags.bundle.set.default")) { return true; }
		return false;
	}

	@Override
	public Boolean getValue(Flag flag, boolean absolute) {
    	Boolean value = null;
    	if(isArea()) { 
	    	String valueString = Flags.getDataStore().read(getDataPath() + "." + flag.getName() + valueFooter);
	    	
	    	if (valueString != null && valueString.toLowerCase().contains("true")) { 
	    		value = true;
	    	} else if (valueString != null) {
	    		value = false;
	    	}
    	}
    	
    	if(absolute) { return value; }
        return (value != null) ? value : flag.getDefault();
	}

	@Override
	public Set<String> getTrustList(Flag flag) {
    	Set<String> trustedPlayers = Flags.getDataStore().readSet(dataHeader + getSystemID() + "." + flag.getName() + trustFooter);
    	return (trustedPlayers != null) ? trustedPlayers : new HashSet<String>();
	}

	@Override
	public String getMessage(Flag flag, boolean parse) {
		// We are ignore parse here.  We just want to override it.
		String message = Flags.getDataStore().read(getDataPath() + "." + flag.getName() + messageFooter);
	 	
		if (message == null) {
			message = flag.getDefaultAreaMessage();
		}
		return message; // They can use their own color codes
	}

	// ******************************
	// Comparable Interface
	// ******************************
	/**
	 * 0 if the the areas are the same, 3 if they are not.
	 * 
	 * @return The value of the comparison.
	 */
	@Override
	public int compareTo(Area a) {
		if(a instanceof Default && a.getSystemID().equalsIgnoreCase(this.getSystemID())) {
			return 0;
		}		
		return 3;
	}
}
