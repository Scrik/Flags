package alshain01.Flags;

import java.util.Set;

/**
 * Class for flag bundle management.
 * 
 * @author Alshain01
 */
public final class Bundle {
	/**
	 * Gets a bundle from the data store.
	 * 
	 * @param bundle
	 *            The bundle name to retrieve
	 * @return A list containing the bundle. Null if it doesn't exist.
	 */
	public final static Set<String> getBundle(String bundle) {
		return Flags.getDataStore().readSet("Bundle." + bundle.toLowerCase());
	}

	/**
	 * Gets a set of bundle names created on the server.
	 * 
	 * @return A set of bundles names configured on the server.
	 */
	public final static Set<String> getBundleNames() {
		return Flags.getDataStore().readKeys("Bundle");
	}

	/**
	 * Checks if a bundle name exists in the data store.
	 * 
	 * @param bundle
	 *            A string bundle name.
	 * @return True if the string is a valid bundle name.
	 */
	public final static boolean isBundle(String bundle) {
		return getBundleNames().contains(bundle.toLowerCase());
	}

	/**
	 * Sets a bundle to the data file.
	 * 
	 * @param name
	 *            The bundle name
	 * @param flags
	 *            A list of flags in the bundle. (does not verify validity)
	 */
	public final static void setBundle(String name, Set<String> flags) {
		if (flags == null || flags.size() == 0) {
			Flags.getDataStore().write("Bundle." + name, (String) null);
			return;
		}
		Flags.getDataStore().write("Bundle." + name, flags);
	}

	private Bundle() {
	}
}
