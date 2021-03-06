/* Copyright 2013 Kevin Seiden. All rights reserved.

 This works is licensed under the Creative Commons Attribution-NonCommercial 3.0

 You are Free to:
    to Share � to copy, distribute and transmit the work
    to Remix � to adapt the work

 Under the following conditions:
    Attribution � You must attribute the work in the manner specified by the author (but not in any way that suggests that they endorse you or your use of the work).
    Non-commercial � You may not use this work for commercial purposes.

 With the understanding that:
    Waiver � Any of the above conditions can be waived if you get permission from the copyright holder.
    Public Domain � Where the work or any of its elements is in the public domain under applicable law, that status is in no way affected by the license.
    Other Rights � In no way are any of the following rights affected by the license:
        Your fair dealing or fair use rights, or other applicable copyright exceptions and limitations;
        The author's moral rights;
        Rights other persons may have either in the work itself or in how the work is used, such as publicity or privacy rights.

 Notice � For any reuse or distribution, you must make clear to others the license terms of this work. The best way to do this is with a link to this web page.
 http://creativecommons.org/licenses/by-nc/3.0/
 */

package alshain01.Flags.area;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;

import org.bukkit.Location;

import alshain01.Flags.Flags;
import alshain01.Flags.SystemType;

public class GriefPreventionClaim extends Area implements Removable, Siege,
		Administrator {
	protected Claim claim;

	/**
	 * Creates an instance of GriefPreventionClaim based on a Bukkit Location
	 * 
	 * @param location
	 *            The Bukkit location
	 */
	public GriefPreventionClaim(Location location) {
		claim = GriefPrevention.instance.dataStore.getClaimAt(location, false, null);
	}

	/**
	 * Creates an instance of GriefPreventionClaim based on a claim ID
	 * 
	 * @param ID
	 *            The claim ID
	 */
	public GriefPreventionClaim(long ID) {
		claim = GriefPrevention.instance.dataStore.getClaim(ID);
	}

	/**
	 * 0 if the the worlds are the same, 3 if they are not.
	 * 
	 * @return The value of the comparison.
	 */
	@Override
	public int compareTo(Area a) {
		if(!(a instanceof GriefPreventionClaim)) {
			return 3;
		}
		
		return (claim == ((GriefPreventionClaim)a).getClaim()) ? 0 : 3;
	}

	@Override
	public String getAreaType() {
		return SystemType.GRIEF_PREVENTION.getAreaType();
	}

	public Claim getClaim() {
		return claim;
	}

	@Override
	public Set<String> getOwners() {
		return new HashSet<String>(Arrays.asList(claim.getOwnerName()));
	}

	@Override
	public String getSystemID() {
		if (isArea() && claim.parent != null) {
			return String.valueOf(claim.parent.getID());
		} else if (isArea()) {
			return String.valueOf(claim.getID());
		} else {
			return null;
		}
	}

	@Override
	public org.bukkit.World getWorld() {
		return claim.getGreaterBoundaryCorner().getWorld();
	}

	@Override
	public boolean isAdminArea() {
		return claim.isAdminClaim();
	}

	@Override
	public boolean isArea() {
		return claim != null;
	}

	@Override
	public boolean isUnderSiege() {
		return !(claim == null || claim.siegeData == null);
	}

	@Override
	public void remove() {
		Flags.getDataStore().remove(this);
	}

	@Override
	public SystemType getType() {
		return SystemType.GRIEF_PREVENTION;
	}
}
