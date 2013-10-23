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
	Faction faction;
	World world;
	
	// ******************************
	// Constructors
	// ******************************
	public FactionsTerritory (Faction faction, World world) {
		this.faction = faction;
		this.world = world;
	}
	
	public FactionsTerritory (String worldName, String factionID) {
		this.faction = FactionColls.get().getForWorld(worldName).get(factionID);
		this.world = Bukkit.getServer().getWorld(worldName);
	}
	
	public FactionsTerritory (Location location) {
		BoardColls.get().getFactionAt(PS.valueOf(location));
		this.world = location.getWorld();
	}
	
	@Override
	public int compareTo(Area a) {
		if(a instanceof FactionsTerritory && a.getSystemID() == getSystemID()) {
			return 0;
		}
		return 3;
	}

	@Override
	protected String getDataPath() {
		return dataHeader + getSystemID();
	}

	@Override
	public String getSystemID() {
		return faction.getId();
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
		return world;
	}

	@Override
	public boolean isArea() {
		if(this.faction != null) { return true; }
		return false;
	}

	@Override
	public void remove() {
		Flags.instance.dataStore.write(getDataPath(), (String)null);
	}
}
