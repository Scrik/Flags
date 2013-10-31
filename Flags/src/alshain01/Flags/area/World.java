package alshain01.Flags.area;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import alshain01.Flags.Flag;
import alshain01.Flags.Flags;
import alshain01.Flags.Message;

public class World extends Area {
	private final static String dataHeader = "World.";
	private final static HashSet<String> owners = new HashSet<String>(Arrays.asList("world"));
	private UUID worldUID = null;
	
	// ******************************
	// Constructors
	// ******************************
	public World(org.bukkit.World world) {
		this.worldUID = world.getUID();
	}
	
	public World(Location location) {
		this.worldUID = location.getWorld().getUID();
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
		return Message.World.get();
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
		return this.worldUID != null;
	}
	
	@Override
	public boolean hasPermission(Player player) {
		return player.hasPermission("flags.area.flag.world");
	}

	@Override
	public boolean hasBundlePermission(Player player) {
		return player.hasPermission("flags.area.bundle.world");
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
		String message = Flags.getDataStore().read(dataHeader + getSystemID() + "." + flag.getName() + messageFooter);
	 	
		if (message == null) {
			message = flag.getDefaultWorldMessage();
		}
		
		if (parse) {
			message = message
					.replaceAll("\\{AreaType\\}", getAreaType().toLowerCase())
					.replaceAll("\\{World\\}", Bukkit.getWorld(this.worldUID).getName());
			message = ChatColor.translateAlternateColorCodes('&', message);
		}
		return message;
	}

	// ******************************
	// Comparable Interface
	// ******************************
	/**
	 * 0 if the the worlds are the same, 3 if they are not.
	 * 
	 * @return The value of the comparison.
	 */
	@Override
	public int compareTo(Area a) {
		return (a instanceof World && a.getSystemID().equalsIgnoreCase(this.getSystemID())) ? 0 : 3;
	}
}
