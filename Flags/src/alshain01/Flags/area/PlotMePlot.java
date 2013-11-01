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
	/**
	 * Creates an instance of PlotMePlot based on a Bukkit Location
	 * @param location The Bukkit location
	 */
	public PlotMePlot(Location location) {
		this.plot = PlotManager.getPlotById(location);
	}
	
	/**
	 * Creates an instance of PlotMePlot based on a plot ID and Bukkit world
	 * @param ID The claim ID
	 * @param worldName The Bukkit world
	 */
	public PlotMePlot(String worldName, String plotID) {
		this.plot = PlotManager.getPlotById(Bukkit.getServer().getWorld(worldName), plotID);
	}
	
	// ******************************
	// Area Interface
	// ******************************
	@Override
	protected String getDataPath() {
		return dataHeader + plot.world + "." + getSystemID();
	}
	
	@Override
	public String getSystemID() {
		return (isArea()) ? plot.id : null;
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
	@Override
	public int compareTo(Area a) {
		if(!(a instanceof PlotMePlot)) { return 0; }
		return super.compareTo(a);
	}
	
	// ******************************
	// Removable Interface
	// ******************************
	@Override
	public void remove() {
		Flags.getDataStore().write(getDataPath(), (String)null);
	}
}
