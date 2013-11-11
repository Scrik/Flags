/* Copyright 2013 Kevin Seiden. All rights reserved.

 This works is licensed under the Creative Commons Attribution-NonCommercial 3.0

 You are Free to:
    to Share — to copy, distribute and transmit the work
    to Remix — to adapt the work

 Under the following conditions:
    Attribution — You must attribute the work in the manner specified by the author (but not in any way that suggests that they endorse you or your use of the work).
    Non-commercial — You may not use this work for commercial purposes.

 With the understanding that:
    Waiver — Any of the above conditions can be waived if you get permission from the copyright holder.
    Public Domain — Where the work or any of its elements is in the public domain under applicable law, that status is in no way affected by the license.
    Other Rights — In no way are any of the following rights affected by the license:
        Your fair dealing or fair use rights, or other applicable copyright exceptions and limitations;
        The author's moral rights;
        Rights other persons may have either in the work itself or in how the work is used, such as publicity or privacy rights.

 Notice — For any reuse or distribution, you must make clear to others the license terms of this work. The best way to do this is with a link to this web page.
 http://creativecommons.org/licenses/by-nc/3.0/
 */

package alshain01.Flags;

public enum AreaType {
	DEFAULT("Default", "Default"),
	WORLD("World", "World"),
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
	public static AreaType getByName(String name) {
		for (final AreaType p : AreaType.values()) {
			if (name.equals(p.pluginName)) {
				return p;
			}
		}
		return AreaType.WORLD;
	}
	
	/**
	 * Gets the enumeration of the land system that flags is currently using.
	 * 
	 * @return The enumeration.
	 */
	public static AreaType getActive() {
		return Flags.currentSystem;
	}

	private String pluginName = null, displayName = null;

	private AreaType(String name, String displayName) {
		pluginName = name;
		this.displayName = displayName;
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