package alshain01.Flags.area;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import alshain01.Flags.Flags;
import alshain01.Flags.Message;
import uk.co.jacekk.bukkit.infiniteplots.InfinitePlots;
import uk.co.jacekk.bukkit.infiniteplots.plot.Plot;
import uk.co.jacekk.bukkit.infiniteplots.plot.PlotLocation;

public class InfinitePlotsPlot extends Area implements Removable {
	protected final static String dataHeader = "InfinitePlotsData";
	protected Plot plot = null;
	
	// ******************************
	// Constructors
	// ******************************
	public InfinitePlotsPlot() { }
	 
	public InfinitePlotsPlot(Location location) {
		reconstructAt(location);
	}
	
	public InfinitePlotsPlot(String worldName, String plotLocation) {
		String[] plotLocData = plotLocation.split(":");
		this.plot = InfinitePlots.getInstance().getPlotManager()
				.getPlotAt(new PlotLocation(worldName, Integer.valueOf(plotLocData[0]), Integer.valueOf(plotLocData[1])));
	}
	
	// ******************************
	// Area Interface
	// ******************************
	@Override
	public void reconstructAt(Location location) {
		this.plot = InfinitePlots.getInstance().getPlotManager().getPlotAt(PlotLocation.fromWorldLocation(location));
	}
	
	@Override
	protected String getDataPath() {
		return dataHeader + plot.getLocation().getWorldName() + "." + getSystemID();
	}

	@Override
	public String getSystemID() {
		if(isArea()) {
			return plot.getLocation().getX() + ":" + plot.getLocation().getZ();
		}
		return null;
	}

	@Override
	public String getAreaType() {
		return Message.InfinitePlots.get();
	}

	@Override
	public Set<String> getOwners() {
		return new HashSet<String>(Arrays.asList(plot.getAdmin()));
	}

	@Override
	public World getWorld() {
		return Bukkit.getServer().getWorld(plot.getLocation().getWorldName());
	}

	@Override
	public boolean isArea() {
		return plot != null && plot.getAdmin() != null;
	}
	
	// ******************************
	// Comparable Interface
	// ******************************
	/**
	 * 0 if the the plots are the same, 3 if they are not.
	 * 
	 * @return The value of the comparison.
	 */	
	@Override
	public int compareTo(Area a) {
		if(a instanceof InfinitePlotsPlot && a.getSystemID().equals(this.getSystemID())) {
			return 0;
		}

		return 3;
	}
	
	// ******************************
	// Removable Interface
	// ******************************
	/**
	 * Permanently removes the area from the data store
	 * USE CAUTION!
	 */
	@Override
	public void remove() {
 	   Flags.getDataStore().write(getDataPath(), (String)null);
	}
}
