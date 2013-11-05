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

import org.bukkit.Bukkit;
import org.bukkit.Location;

import alshain01.Flags.Flags;

public class GriefPreventionClaim78 extends GriefPreventionClaim implements
		Subdivision {

	/**
	 * Creates an instance of GriefPreventionClaim78 based on a Bukkit Location
	 * 
	 * @param location
	 *            The Bukkit location
	 */
	public GriefPreventionClaim78(Location location) {
		super(location);
	}

	/**
	 * Creates an instance of GriefPreventionClaim78 based on a claim ID
	 * 
	 * @param ID
	 *            The claim ID
	 */
	public GriefPreventionClaim78(long ID) {
		super(ID);
	}

	/**
	 * Creates an instance of GriefPreventionClaim78 based on a claim ID and
	 * subclaimID
	 * 
	 * @param ID
	 *            The claim ID
	 * @param subID
	 *            The subclaim ID
	 */
	public GriefPreventionClaim78(long ID, long subID) {
		super(ID);

		claim = claim == null ? null : claim.getSubClaim(subID);
	}

	/**
	 * 0 if the the claims are the same -1 if the claim is a subdivision of the
	 * provided claim. 1 if the claim is a parent of the provided claim. 2 if
	 * they are "sister" subdivisions. 3 if they are completely unrelated.
	 * 
	 * @return The value of the comparison.
	 */
	@Override
	public int compareTo(Area a) {
		if (!(a instanceof GriefPreventionClaim78)) {
			return 3;
		}

		if (a.getSystemID().equals(getSystemID())) {
			// They are related somehow. We need to figure out how.
			// (We can safely assume instance of subdivision because of the
			// first line)

			if (((Subdivision) a).getSystemSubID() == null
					&& getSystemSubID() != null) {
				// a is the parent
				return -1;
			}

			if (((Subdivision) a).getSystemSubID() != null
					&& getSystemSubID() == null) {
				// this is the parent
				return 1;
			}

			if (((Subdivision) a).getSystemSubID() != null
					&& getSystemSubID() != null) {
				// neither are the parent, but the parent ID is the same
				return 2;
			}

			if (((Subdivision) a).getSystemSubID() == null
					&& getSystemSubID() == null
					|| ((Subdivision) a).getSystemSubID().equals(getSystemSubID())) {
				// They are the same claim
				return 0;
			}
		}
		return 3;
	}

	@Override
	protected String getDataPath() {
		return isSubdivision() && !isInherited() 
				? super.getDataPath() + "." + getSystemSubID() 
				: super.getDataPath();
	}

	private String getInheritPath() {
		return dataHeader + getSystemID() + "." 
				+ getSystemSubID() + "." + "InheritParent";
	}

	@Override
	public String getSystemSubID() {
		return isSubdivision() ? String.valueOf(getClaim().getSubClaimID())
				: null;
	}

	@Override
	public org.bukkit.World getWorld() {
		return Bukkit.getServer().getWorld(getClaim().getClaimWorldName());
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
		return isArea() && getClaim().parent != null;
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
