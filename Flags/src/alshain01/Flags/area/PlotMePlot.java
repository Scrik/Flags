package alshain01.Flags.area;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import alshain01.Flags.Flags;
import alshain01.Flags.Message;

import com.worldcretornica.plotme.Plot;
import com.worldcretornica.plotme.PlotManager;

public class PlotMePlot extends Area implements Removable {
	protected final static String dataHeader = "PlotMeData";
	protected Plot plot = null;
	
	// ******************************
	// Constructors
	// ******************************
	public PlotMePlot() { }
	
	public PlotMePlot(Location location) {
		reconstructAt(location);
	}
	
	public PlotMePlot(String worldName, String plotID) {
		this.plot = PlotManager.getPlotById(Bukkit.getServer().getWorld(worldName), plotID);
	}
	
	// ******************************
	// Area Interface
	// ******************************
	@Override
	public void reconstructAt(Location location) {
		this.plot = PlotManager.getPlotById(location);
	}
	
	@Override
	protected String getDataPath() {
		return dataHeader + plot.world + "." + getSystemID();
	}
	
	@Override
	public String getSystemID() {
		if(isArea()) {
			return plot.id;
		}
		return null;
	}
	
	@Override
	public String getAreaType() {
		return Message.PlotMe.get();
	}
	
	@Override
	public Set<String> getOwners() {
		return new HashSet<String>(Arrays.asList(plot.owner));
	}
	
	@Override
	public World getWorld() {
		return Bukkit.getServer().getWorld(plot.world);
	}
	
	@Override
	public boolean isArea() {
		return plot != null && !plot.owner.isEmpty();
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
		if(a instanceof PlotMePlot && a.getWorld() == this.getWorld() && a.getSystemID().equals(this.getSystemID())) {
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
