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
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import alshain01.Flags.Flags;
import alshain01.Flags.Message;

import com.massivecraft.factions.entity.BoardColls;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColls;
import com.massivecraft.mcore.ps.PS;

/**
 * Class for creating areas to manage a Factions Territory.
 * 
 * @author Kevin Seiden
 */
public class FactionsTerritory extends Area implements Removable{
	protected final static String dataHeader = "FactionsData.";
	String factionID = null;
	UUID worldUID = null;
	
	// ******************************
	// Constructors
	// ******************************
	/**
	 * Creates an instance of FactionsTerritory based on a Bukkit World and faction ID
	 * @param factionID The faction ID
	 * @param worldName The Bukkit world
	 */
	public FactionsTerritory (World world, String factionID) {
		this.factionID = factionID;
		this.worldUID = world.getUID();
	}
	
	/**
	 * Creates an instance of FactionsTerritory based on a Bukkit Location
	 * @param location The Bukkit location
	 */
	public FactionsTerritory (Location location) {
		this.factionID = BoardColls.get().getFactionAt(PS.valueOf(location)).getId();
		this.worldUID = location.getWorld().getUID();
	}
	
	public Faction getFaction() {
		return FactionColls.get().getForWorld(Bukkit.getWorld(worldUID).getName()).get(factionID);
	}
	
	// ******************************
	// Area Interface
	// ******************************
	@Override
	protected String getDataPath() {
		return dataHeader + getWorld().getName() + "." + getSystemID();
	}

	@Override
	public String getSystemID() {
		return (isArea()) ? factionID : null;
	}

	@Override
	public String getAreaType() {
		return Message.Factions.get();
	}

	@Override
	public Set<String> getOwners() {
		return new HashSet<String>(Arrays.asList(FactionColls.get().getForWorld(Bukkit.getWorld(worldUID).getName()).get(factionID).getLeader().getName()));
	}

	@Override
	public World getWorld() {
		return Bukkit.getWorld(worldUID);
	}

	@Override
	public boolean isArea() {
		return this.factionID != null 
				&& this.worldUID != null
				&& getFaction() != null ;
	}
	
	// ******************************
	// Comparable Interface
	// ******************************
	/**
	 * 0 if the the plots are the same, 3 if they are not.
	 * @return The value of the comparison.
	 */	
	@Override
	public int compareTo(Area a) {
		return (a instanceof FactionsTerritory && a.getSystemID().equals(this.getSystemID())) ? 0 : 3;
	}
	
	// ******************************
	// Removable Interface
	// ******************************
	@Override
	public void remove() {
		Flags.getDataStore().write(getDataPath(), (String)null);
	}
}
