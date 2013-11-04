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
