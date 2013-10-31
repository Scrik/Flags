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

public class FactionsTerritory extends Area implements Removable{
	protected final static String dataHeader = "FactionsData.";
	Faction faction = null;
	String worldName = null;
	
	// ******************************
	// Constructors
	// ******************************
	public FactionsTerritory (String worldName, String factionID) {
		this.faction = FactionColls.get().getForWorld(worldName).get(factionID);
		this.worldName = worldName;
	}
	
	public FactionsTerritory (Location location) {
		BoardColls.get().getFactionAt(PS.valueOf(location));
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
		if (isArea()) {
			return faction.getId();
		}
		return null;
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
		return Bukkit.getServer().getWorld(worldName);
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
	 * 
	 * @return The value of the comparison.
	 */	
	@Override
	public int compareTo(Area a) {
		if(a instanceof FactionsTerritory && a.getSystemID().equals(this.getSystemID())) {
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
