package alshain01.Flags;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;

import alshain01.Flags.Flag;

public class Registrar {
	ConcurrentHashMap<String, Flag> flagStore = new ConcurrentHashMap<String, Flag>();
	
	protected Registrar() { }
	
	/**
	 * Registers a non-player flag
	 * 
	 * @param name The name of the flag
	 * @param description A brief description of the flag
	 * @param def The flag's default state
	 * @param group The group the flag belongs in. 
	 * @return The flag if the flag was successfully registered. Null otherwise.
	 */
	public Flag register(String name, String description, boolean def, String group) {
		if(flagStore.containsKey(name)) { return null; }
		Flag flag = new Flag(name, description, def, group, false, null, null);
		
		Bukkit.getServer().getPluginManager().addPermission(flag.getPermission());
		
		flagStore.put(name, flag);
		return flag;
	}
	
	/**
	 * Registers a player flag
	 * 
	 * @param name The name of the flag
	 * @param description A brief description of the flag
	 * @param def The flag's default state
	 * @param group The group the flag belongs in. 
	 * @param areaMessage The default message for areas.
	 * @param worldMessage The default message for worlds.
	 * @return The flag if the flag was successfully registered. Null otherwise.
	 */
	public Flag register(String name, String description, boolean def, String group, String areaMessage, String worldMessage) {
		if(flagStore.containsKey(name)) { return null; }
		Flag flag = new Flag(name, description, def, group, true, areaMessage, worldMessage);

		Bukkit.getServer().getPluginManager().addPermission(flag.getPermission());
		Bukkit.getServer().getPluginManager().addPermission(flag.getBypassPermission());
		
		flagStore.put(name, flag);
		return flag;
	}
	
	/**
	 * Checks if a flag name has been registered.
	 * 
	 * @param flag The flag name
	 * @return True if the flag name has been registered
	 */
	public boolean isFlag(String flag) {
		return flagStore.containsKey(flag);
	}
	
	/**
	 * Gets a flag based on it's case sensitive name.
	 * 
	 * @param flag The flag to retrieve.
	 * @return The flag requested or null if it does not exist.
	 */
	public Flag getFlag(String flag) {
		return (isFlag(flag)) ? flagStore.get(flag) : null;
	}
	
	/**
	 * Gets a flag, ignoring the case.
	 * 
	 * This is an less efficient method, use it
	 * only when absolutely necessary.
	 * 
	 * @param flag The flag to retrieve.
	 * @return The flag requested or null if it does not exist.
	 */
	public Flag getFlagIgnoreCase(String flag) {
		for(Flag f : getFlags()) {
			if(f.getName().equalsIgnoreCase(flag)) { return f; }
		}
		return null;
	}
	
	/**
	 * Gets a collection of all registered flags.
	 * 
	 * @return A collection of all the flags registered.
	 */
	public Collection<Flag> getFlags() {
		return flagStore.values();
	}

	/**
	 * Gets a set of all registered flag names.
	 * 
	 * @return A list of names of all the flags registered.
	 */
	public Enumeration<String> getFlagNames() {
		return flagStore.keys();
	}
	
	/**
	 * Gets a set of all registered flag group names.
	 * 
	 * @return A list of names of all the flags registered.
	 */
	public Set<String> getFlagGroups() {
		Set<String> groups = new HashSet<String>();

		for(Flag flag : flagStore.values()) {
			if(!groups.contains(flag.getGroup())) {
				groups.add(flag.getGroup());
			}
		}
		return groups;
	}
}
