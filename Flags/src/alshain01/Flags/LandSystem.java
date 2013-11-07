package alshain01.Flags;

public enum LandSystem {
	NONE(null, null),
	GRIEF_PREVENTION("GriefPrevention",	"Grief Prevention"),
	WORLDGUARD("WorldGuard", "WorldGuard"),
	RESIDENCE("Residence", "Residence"),
	INFINITEPLOTS("InfinitePlots", "InfinitePlots"),
	FACTIONS("Factions", "Factions"),
	PLOTME("PlotMe", "PlotMe");

	/**
	 * Gets the enumeration that matches the case sensitive plugin.yml name.
	 * 
	 * @return The enumeration. LandSystem.NONE if no matches found.
	 */
	public static LandSystem getByName(String name) {
		for (final LandSystem p : LandSystem.values()) {
			if (name.equals(p.pluginName)) {
				return p;
			}
		}
		return LandSystem.NONE;
	}
	
	/**
	 * Gets the enumeration of the land system that flags is currently using.
	 * 
	 * @return The enumeration.
	 */
	public static LandSystem getActive() {
		return Flags.currentSystem;
	}

	private String pluginName = null, displayName = null;

	private LandSystem(String name, String displayName) {
		pluginName = name;
		this.displayName = displayName;
	}

	/**
	 * Gets the data header of the land system as stored in the database.
	 * 
	 * @return The enumeration.
	 */
	public String getDataPath() {
		if(this == NONE) { return "World"; }
		return displayName + "Data";
	}
	
	/**
	 * Gets a user friendly string, including spaces, for the plug-in.
	 * 
	 * @return The user friendly name of the plugin
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Gets the plug-in name as indicated in it's plugin.yml
	 * 
	 * @return The case sensitive plugin.yml name for the enumerated value
	 */
	@Override
	public String toString() {
		return pluginName;
	}
}