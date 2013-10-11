package alshain01.Flags.area;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import alshain01.Flags.Flag;
import alshain01.Flags.Flags;
import alshain01.Flags.events.FlagChangedEvent;
import alshain01.Flags.events.MessageChangedEvent;

public abstract class Area implements Comparable<Area> {
	protected final static String valueFooter = ".Value";
	protected final static String trustFooter = ".Trust";
	protected final static String messageFooter = ".Message";
	
	/* 
	 * @return The data path of the data storage system for the area
	 */
	protected abstract String getDataPath();
	
	/**
	 * Retrieve the land system's ID for this area.
	 * 
	 * @return the area's ID in the format provided by the land management system.
	 */
	public abstract String getSystemID();
	
	/**
	 * Retrieve the friendly name of the area type.
	 * 
	 * @return the area's type as a user friendly name.
	 */
	public abstract String getAreaType();
	
	/**
	 * Retrieve a set of owners for the area.  On many systems, there will only be one.
	 * 
	 * @return the player name of the area owner.
	 */
	public abstract Set<String> getOwners();
	
	/**
	 * Retrieve the world for the area.
	 * 
	 * @return the world associated with the area.
	 */
	public abstract org.bukkit.World getWorld();

	/**
	 * Retrieve whether or not the area exists on the server.
	 * Null Areas return false.
	 * 
	 * @return true if the area exists.
	 */
	public abstract boolean isArea();
	
	/**
	 * Gets the players permission to set flags at this location.
	 * 
	 * @param player The player to check.
	 * @return true if the player has permissions.
	 */
	public abstract boolean hasPermission(Player player);
	
	/**
	 * Gets the players permission to set bundles at this location
	 * 
	 * @param player The player to check.
	 * @return true if the player has permissions.
	 */
	public abstract boolean hasBundlePermission(Player player);
	
	/**
	 * Returns the value of the flag for this area.
	 * 
	 * @param flag The flag to retrieve the value for.
	 * @param absolute True if you want a null value if the flag is not defined. False if you want the inherited default (ensures not null).
	 * @return The value of the flag or the inherited value of the flag from defaults if not defined.
	 */
	public Boolean getValue(Flag flag, boolean absolute) {
    	Boolean value = null;
    	if(isArea()) { 
	    	String valueString = Flags.instance.dataStore.read(getDataPath() + "." + flag.getName() + valueFooter);
	    	
	    	if (valueString != null && valueString.toLowerCase().contains("true")) { 
	    		value = true;
	    	} else if (valueString != null) {
	    		value = false;
	    	}
    	}
    	
    	if(absolute) { return value; }
        return (value != null) ? value :
        	new Default(getWorld()).getValue(flag, false);
	}
	
	/**
	 * Sets the value of the flag for this area.
	 * 
	 * @param flag The flag to set the value for.
	 * @param value The value to set, null to remove.
	 * @param sender The command sender for event call, may be null if no associated player or console.
	 * @return False if the event was canceled.
	 */
	public final boolean setValue(Flag flag, Boolean value, CommandSender sender) {
    	FlagChangedEvent event = new FlagChangedEvent(this, flag, sender, value);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) { return false; }


        if(value == null) {
        	// Remove the flag
        	Flags.instance.dataStore.write(getDataPath() + "." + flag.getName() + valueFooter, (String)null);
        } else {
            // Set the flag
        	Flags.instance.dataStore.write(getDataPath() + "." + flag.getName() + valueFooter, String.valueOf(value));
        }
        return true;
	}
	
	/**
	 * Retrieves the list of trusted players
	 * 
	 * @param flag The flag to retrieve the trust list for.
	 * @return The list of players
	 */
	public Set<String> getTrustList(Flag flag) {
    	Set<String> trustedPlayers = Flags.instance.dataStore.readSet(getDataPath() + "." + flag.getName() + trustFooter);
    	if(trustedPlayers == null) { trustedPlayers = new HashSet<String>(); }
    	trustedPlayers.addAll(getOwners());
    	return trustedPlayers;
	}
	
	/**
	 * Adds or removes a player from the trust list.
	 * 
	 * @param flag The flag to change trust for.
	 * @param trustee The player being trusted or distrusted
	 * @param trusted True if adding to the trust list, false if removing.
	 * @param sender CommandSender for event, may be null if no associated player or console.
	 * @return True if successful.
	 */
	public final boolean setTrust(Flag flag, String trustee, boolean trusted, CommandSender sender) {
    	if (trusted) {
    		return Trust.setTrust(this, flag, trustee, sender, getDataPath() + "." + flag.getName() + trustFooter);
    	}
    	return Trust.removeTrust(this, flag, trustee, sender, getDataPath() + "." + flag.getName() + trustFooter);
	}
	
	/**
	 * Gets the message associated with a player flag.
	 * Translates the color codes and populates instances of {AreaType} and {Owner}
	 * 
	 * @param flag The flag to retrieve the message for.
	 * @return The message associated with the flag.
	 */
	public final String getMessage(Flag flag) {
		return getMessage(flag, true);
	}

	/**
	 * Gets the message associated with a player flag and parses
	 * {AreaType}, {Owner}, {World}, and {Player}
	 * 
	 * @param flag The flag to retrieve the message for.
	 * @param player The player name to insert into the messsage.
	 * @return The message associated with the flag.
	 */
	public final String getMessage(Flag flag, String player) {
		return getMessage(flag, true).replaceAll("\\{Player\\}", player);
	}
	
	/**
	 * Gets the message associated with a player flag.
	 * 
	 * @param flag The flag to retrieve the message for.
	 * @param parse True if you wish to populate instances of {AreaType}, {Owner}, and {World} and translate color codes
	 * @return The message associated with the flag.
	 */
	public String getMessage(Flag flag, boolean parse) {
		if(!isArea()){ return null; }
		String message = Flags.instance.dataStore.read(getDataPath() + "." + flag.getName() + messageFooter);
	 	   
		if (message == null) {
			message = new Default(getWorld()).getMessage(flag);
		}
		
		if (parse) {
			message = message
					.replaceAll("\\{AreaType\\}", getAreaType().toLowerCase())
					.replaceAll("\\{Owner\\}", getOwners().toArray()[0].toString());
			message = ChatColor.translateAlternateColorCodes('&', message);
		}
		return message;
	}
	
	/**
	 * Sets or removes the message associated with a player flag.
	 * 
	 * @param flag The flag to set the message for.
	 * @param message The message to set, null to remove.
	 * @param sender CommandSender for event, may be null if no associated player or console.
	 * @return True if successful
	 */
	public final boolean setMessage(Flag flag, String message, CommandSender sender) {
		if(!isArea()) { return false; }
	 	
		MessageChangedEvent event = new MessageChangedEvent(this, flag, message, sender);
		Bukkit.getServer().getPluginManager().callEvent(event);
		if (event.isCancelled()) { return false; }
	 	   
		Flags.instance.dataStore.write(getDataPath() + "." + flag.getName() + messageFooter, message.replaceAll("§", "&"));
		return true;
	}
}