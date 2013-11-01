package alshain01.Flags.area;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import alshain01.Flags.Flag;
import alshain01.Flags.Flags;
import alshain01.Flags.Message;

/**
 * Class for creating areas to manage server defaults.
 * 
 * @author Kevin Seiden
 */
public class Default extends Area {
	private final static String dataHeader = "Default.";
	private final static HashSet<String> owners = new HashSet<String>(Arrays.asList("default"));
	private UUID worldUID = null;
	
	// ******************************
	// Constructors
	// ******************************
	/**
	 * Creates an instance of Default based on a Bukkit Location
	 * @param location The Bukkit location
	 */
	public Default(Location location) {
		this(location.getWorld());
	}
	
	/**
	 * Creates an instance of Default based on a Bukkit World
	 * @param world The Bukkit world
	 */
	public Default(org.bukkit.World world) {
		this.worldUID = world.getUID();
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
		return Bukkit.getWorld(this.worldUID).getName();
	}
	
	@Override
	public String getAreaType() {
		return Message.Default.get();
	}
	
	@Override
	public Set<String> getOwners() {
		return owners;
	}
	
	@Override
	public org.bukkit.World getWorld() {
		return Bukkit.getWorld(this.worldUID);
	}
	
	@Override
	public boolean isArea() {
		return this.worldUID != null && Bukkit.getWorld(this.worldUID) != null;
	}
	
	@Override
	public boolean hasPermission(Player player) {
		return player.hasPermission("flags.area.flag.default");
	}

	@Override
	public boolean hasBundlePermission(Player player) {
		return player.hasPermission("flags.area.bundle.default");
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

	/**
	 * Gets the message associated with a player flag.
	 * 
	 * @param flag The flag to retrieve the message for.
	 * @param parse Ignored by Default area.
	 * @return The message associated with the flag.
	 */
	@Override
	public String getMessage(Flag flag, boolean parse) {
		// We are ignore parse here.  We just want to override it.
		String message = Flags.getDataStore().read(getDataPath() + "." + flag.getName() + messageFooter);
	 	return (message != null) ? message : flag.getDefaultAreaMessage();
	}

	// ******************************
	// Comparable Interface
	// ******************************
	@Override
	public int compareTo(Area a) {
		if(!(a instanceof Default)) { return 0; }
		return super.compareTo(a);
	}
}
