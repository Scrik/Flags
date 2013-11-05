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
	private final ClaimedResidence residence;

	/**
	 * Creates an instance of ResidenceClaimedResidence based on a Bukkit
	 * Location
	 * 
	 * @param location
	 *            The Bukkit location
	 */
	public ResidenceClaimedResidence(Location location) {
		residence = Residence.getResidenceManager().getByLoc(location);
	}

	/**
	 * Creates an instance of ResidenceClaimedResidence based on a residence
	 * name
	 * 
	 * @param ID
	 *            The claim ID
	 */
	public ResidenceClaimedResidence(String name) {
		residence = Residence.getResidenceManager().getByName(name);
	}

	@Override
	public int compareTo(Area a) {
		if (a instanceof ResidenceClaimedResidence
				&& a.getSystemID().equals(getSystemID())) {
			return 0;
		}

		// Return a -1 if this is a subdivision of the provided area
		if (a instanceof Subdivision
				&& isSubdivision()
				&& String.valueOf(getResidence().getParent().getName())
						.equalsIgnoreCase(a.getSystemID())) {
			return -1;
		}

		return 3;
	}

	@Override
	public String getAreaType() {
		return Message.Residence.get();
	}

	@Override
	protected String getDataPath() {
		return isSubdivision() && !isInherited() ? dataHeader
				+ getSystemSubID() : dataHeader + getSystemID();
	}

	private String getInheritPath() {
		return dataHeader + getSystemSubID() + "." + "InheritParent";
	}

	@Override
	public Set<String> getOwners() {
		return new HashSet<String>(Arrays.asList(getResidence().getOwner()));
	}

	public ClaimedResidence getResidence() {
		return residence;
	}

	@Override
	public String getSystemID() {
		return isSubdivision() ? getResidence().getParent().getName() : getResidence().getName();
	}

	@Override
	public String getSystemSubID() {
		return isSubdivision() ? getResidence().getName() : null;
	}

	@Override
	public org.bukkit.World getWorld() {
		return Bukkit.getServer().getWorld(getResidence().getWorld());
	}

	@Override
	public boolean isArea() {
		return getResidence() != null;
	}

	@Override
	public boolean isInherited() {
		if (!isSubdivision()) {
			return false;
		}

		final String value = Flags.getDataStore().read(getInheritPath());
		if (value == null) {
			return true;
		}
		return Boolean.valueOf(value);
	}

	@Override
	public boolean isSubdivision() {
		return isArea() && getResidence().getParent() != null;
	}

	/**
	 * Permanently removes the area from the data store USE CAUTION!
	 */
	@Override
	public void remove() {
		Flags.getDataStore().write(getDataPath(), (String) null);
	}

	@Override
	public boolean setInherited(Boolean value) {
		if (!isSubdivision()) {
			return false;
		}
		final String storedValue = Flags.getDataStore().read(getInheritPath());

		if (value == null) {
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
