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
import uk.co.jacekk.bukkit.infiniteplots.InfinitePlots;
import uk.co.jacekk.bukkit.infiniteplots.plot.Plot;
import uk.co.jacekk.bukkit.infiniteplots.plot.PlotLocation;

public class InfinitePlotsPlot extends Area implements Removable {
	protected final static String dataHeader = "InfinitePlotsData.";
	int plotX, plotZ;
	UUID worldUID = null;
	//protected Plot plot = null;
	
	// ******************************
	// Constructors
	// ******************************
	/**
	 * Creates an instance of InfinitePlotsPlot based on a Bukkit Location
	 * @param location The Bukkit location
	 */
	public InfinitePlotsPlot(Location location) {
		Plot plot = InfinitePlots.getInstance().getPlotManager().getPlotAt(PlotLocation.fromWorldLocation(location));
		plotX = plot.getLocation().getX();
		plotZ = plot.getLocation().getZ();
		worldUID = plot.getLocation().getWorld().getUID();
	}
	
	/**
	 * Creates an instance of InfinitePlotsPlot based on a Bukkit world and Plot Location
	 * @param worldName The Bukkit world name
	 * @param ID The Plot Location (not Bukkit location)
	 */
	public InfinitePlotsPlot(World world, int X, int Z) {
		plotX = X;
		plotZ = Z;
		worldUID = world.getUID();
		//this.plot = InfinitePlots.getInstance().getPlotManager()
		//		.getPlotAt(new PlotLocation(worldName, Integer.valueOf(plotLocData[0]), Integer.valueOf(plotLocData[1])));
	}
	
	public Plot getPlot() {
		return InfinitePlots.getInstance().getPlotManager().getPlotAt(new PlotLocation(Bukkit.getWorld(worldUID).getName(), plotX, plotZ));
	}
	
	// ******************************
	// Area Interface
	// ******************************
	@Override
	protected String getDataPath() {
		return dataHeader + Bukkit.getWorld(worldUID).getName() + "." + getSystemID();
	}

	@Override
	public String getSystemID() {
		return (isArea()) ? plotX + ";" + plotZ : null;
	}

	@Override
	public String getAreaType() {
		return Message.InfinitePlots.get();
	}

	@Override
	public Set<String> getOwners() {
		return new HashSet<String>(Arrays.asList(getPlot().getAdmin()));
	}

	@Override
	public World getWorld() {
		return Bukkit.getServer().getWorld(Bukkit.getWorld(worldUID).getName());
	}

	@Override
	public boolean isArea() {
		return worldUID != null
				&& getPlot() != null
				&& getPlot().getAdmin() != null 
				&& getPlot().getAdmin().isEmpty();
	}
	
	// ******************************
	// Comparable Interface
	// ******************************
	/**
	 * 0 if the the worlds are the same, 3 if they are not.
	 * @return The value of the comparison.
	 */
	@Override
	public int compareTo(Area a) {
		return (a instanceof InfinitePlotsPlot && a.getSystemID().equals(this.getSystemID())) ? 0 : 3;
	}
	
	// ******************************
	// Removable Interface
	// ******************************
	@Override
	public void remove() {
 	   Flags.getDataStore().write(getDataPath(), (String)null);
	}
}
