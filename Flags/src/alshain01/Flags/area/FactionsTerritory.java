package alshain01.Flags.area;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
	Faction faction = null;
	String worldName = null;
	
	// ******************************
	// Constructors
	// ******************************
	/**
	 * Creates an instance of FactionsTerritory based on a Bukkit World and faction ID
	 * @param factionID The faction ID
	 * @param worldName The Bukkit world
	 */
	public FactionsTerritory (String worldName, String factionID) {
		this.faction = FactionColls.get().getForWorld(worldName).get(factionID);
		this.worldName = worldName;
	}
	
	/**
	 * Creates an instance of FactionsTerritory based on a Bukkit Location
	 * @param location The Bukkit location
	 */
	public FactionsTerritory (Location location) {
		this.faction = BoardColls.get().getFactionAt(PS.valueOf(location));
		this.worldName = location.getWorld().getName();
	}
	
	// ******************************
	// Area Interface
	// ******************************
	@Override
	protected String getDataPath() {
		return dataHeader + worldName + "." + getSystemID();
	}

	@Override
	public String getSystemID() {
		return (isArea()) ? faction.getId() : null;
	}

	@Override
	public String getAreaType() {
		return Message.Factions.get();
	}

	@Override
	public Set<String> getOwners() {
		return new HashSet<String>(Arrays.asList(faction.getLeader().getName()));
	}

	@Override
	public World getWorld() {
		return Bukkit.getWorld(worldName);
	}

	@Override
	public boolean isArea() {
		return this.faction != null && this.worldName != null;
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
