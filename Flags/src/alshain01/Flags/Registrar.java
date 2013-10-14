package alshain01.Flags;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import alshain01.Flags.Flag;

public class Registrar {
	ConcurrentHashMap<String, Flag> flagStore = new ConcurrentHashMap<String, Flag>();;
	
	protected Registrar() { }
	
	/**
	 * Registers a non-player flag
	 * 
	 * @param name The name of the flag
	 * @param description A brief description of the flag
	 * @param def The flag's default state
	 * @param group The group the flag belongs in. 
	 * @return True if the flag was successfully registered.
	 */
	public boolean register(String name, String description, boolean def, String group) {
		if(!flagStore.containsKey(name)) {
			flagStore.put(name, new Flag(name, description, def, group, false, null, null));
			return true;
		}
		return false;
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
	 * @return True if the flag was successfully registered.
	 */
	public boolean register(String name, String description, boolean def, String group, String areaMessage, String worldMessage) {
		if(!flagStore.containsKey(name)) {
			flagStore.put(name, new Flag(name, description, def, group, true, areaMessage, worldMessage));
			return true;
		}
		return false;
	}
	
	/**
	 * Informs whether or not a flag name has been registered.
	 * 
	 * @param flag The flag name
	 * @return True if the flag name has been registered
	 */
	public boolean isFlag(String flag) {
		return flagStore.containsKey(flag);
	}
	
	/**
	 * Retrieves a flag based on it's case sensitive name.
	 * 
	 * @param flag The flag to retrieve.
	 * @return The flag requested or null if it does not exist.
	 */
	public Flag getFlag(String flag) {
		if(isFlag(flag)) {
			return flagStore.get(flag);
		}
		return null;
	}
	
	/**
	 * Retrieves a flag, ignoring the case.
	 * This is an inefficient method, use it
	 * only when absolutely necessary.
	 * 
	 * @param flag The flag to retrieve.
	 * @return The flag requested or null if it does not exist.
	 */
	public Flag getFlagIgnoreCase(String flag) {
		for(Flag f : getFlags())
			if(f.getName().equalsIgnoreCase(flag)) {
				return f;
			}
		return null;
	}
	
	/**
	 * Retrieves a collection of all registered flags.
	 * 
	 * @return A collection of all the flags registered.
	 */
	public Collection<Flag> getFlags() {
		return flagStore.values();
	}

	/**
	 * Retrieves a set of all registered flag names.
	 * 
	 * @return A list of names of all the flags registered.
	 */
	public Set<String> getFlagNames() {
		return new HashSet<String>(Collections.list(flagStore.keys()));
	}
}
