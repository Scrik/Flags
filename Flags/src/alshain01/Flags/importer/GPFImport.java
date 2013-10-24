package alshain01.Flags.importer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;

import alshain01.Flags.Flag;
import alshain01.Flags.Flags;
import alshain01.Flags.area.Area;
import alshain01.Flags.area.Default;
import alshain01.Flags.area.GriefPreventionClaim78;
import alshain01.Flags.area.Subdivision;
import alshain01.Flags.area.World;

public final class GPFImport {
	private static final String dataFolder = "plugins\\GriefPreventionFlags\\";
	private static ImportYML world = null;
	private static ImportYML data = null;
	
	private GPFImport(){}
	
	public static void importGPF() {
		if(dataExists() && getVersion().equals("1.6.0")) {
			Flags.getInstance().getLogger().info("Importing GriefPreventionFlags Database");
			importData(data, "data");
			if(world != null) {
				importData(world, "world");
			}
			File gpFolder = new File(dataFolder);
			for(File file : gpFolder.listFiles()) {
				file.delete();
			}
			new File(dataFolder).delete();
		}
	}
	
	private static final void importData(ImportYML data, String header) {
		if (data.getCustomConfig().getString(header) != null) {
			// Get a deep list of all the values to parse through.
			Set<String> keys = data.getCustomConfig().getConfigurationSection(header).getKeys(true);
			
			for(String k : keys) {
				if(k.toLowerCase().contains("database")) { continue; } // Database version for GPFlags, not used by Flags.
				String[] path = k.split("\\.");
				
				// Acquire the claim

				Area area;
				if (path.length == 3) {
					if (header.equalsIgnoreCase("data")) {
						// Subdivision
						area = new GriefPreventionClaim78(Long.valueOf(path[0]), Long.valueOf(path[1]));
					} else if (header.equalsIgnoreCase("world") && path[1].equalsIgnoreCase("unclaimed")) {
						// World
						area = new World(Bukkit.getServer().getWorld(path[0]));
					} else if (header.equalsIgnoreCase("world") && path[1].equalsIgnoreCase("global")) {
						// Default
						area = new Default(Bukkit.getServer().getWorld(path[0]));
					} else {
						continue;
					}
				} else if (path.length == 2) {
					if (header.equalsIgnoreCase("data")) {
						// Claim
						area = new GriefPreventionClaim78(Long.valueOf(path[0]));
					} else {
						continue;
					}
				} else {
					continue;
				}
					
				if(!area.isArea()) { continue; }
				
				// Acquire the flag name
				String flagName = path[path.length - 1].toLowerCase();
					
				// Parse a trust list
				if(flagName.contains("trust")) {
					flagName = flagName.replace("trust", "");
					Flag flag = Flags.getRegistrar().getFlagIgnoreCase(flagName);
					if(flag == null) { continue; }
					
					List<String> players = readList(data, header + "." + k);
					if(players != null) {
						for(String p : players) {
							area.setTrust(flag, p, true, null);
						}
					}
					continue;
				}
				
				//Parse a message
				if(flagName.contains("message")) {
					flagName = flagName.replace("message", "");
					Flag flag = Flags.getRegistrar().getFlagIgnoreCase(flagName);
					if(flag == null) { continue; }

					String message = data.getCustomConfig().getString(header + "." + k);
					area.setMessage(flag, message, null);
					continue;
				}
				
				if(flagName.contains("inheritparent")) {
					if(area instanceof Subdivision && ((GriefPreventionClaim78)area).isSubdivision()) {
						((GriefPreventionClaim78)area).setInherited(true);
					}
					continue;
				}
				
				Flag flag = Flags.getRegistrar().getFlagIgnoreCase(flagName);
				if(flag == null) { continue; }
				boolean value = Boolean.valueOf(data.getCustomConfig().getString(header + "." + k)); 
				area.setValue(flag, value, null);
			}
		}
	}
	
	private static List<String> readList(ImportYML data, String path) {
		List<?> listData = data.getCustomConfig().getList(path);
		if(listData == null) { return null; }
		
		List<String> stringData = new ArrayList<String>();
		
		for (int o = 0; o < listData.size(); o++) {
			stringData.add(((String)listData.get(o)).toLowerCase());
		}
		return stringData;
	}

	
	private static boolean dataExists() {
		File fileObject = new File(dataFolder + "data.yml");
		if(fileObject.exists()) {
			data = new ImportYML(fileObject.getAbsolutePath());
			
			fileObject = new File(dataFolder + "world.yml");
			if(fileObject.exists()) {
				world = new ImportYML(fileObject.getAbsolutePath());
			}
			
			return true;
		}
		return false;
	}

	private static String getVersion() {
		return data.getCustomConfig().getString("data.database.version");
	}
}
