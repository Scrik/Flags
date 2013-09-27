package alshain01.Flags.commands;

import org.bukkit.command.CommandSender;

import alshain01.Flags.Flags;
import alshain01.Flags.data.DataStore;

/**
 * @deprecated
 * @author Alshain01
 */
public abstract class Admin {
	// Command for plug-in data maintenance
	// Keep this top level to access protected dataStore.
	public static boolean onCommand(CommandSender sender, String[] args, DataStore dataStore) {
		if(args.length < 1) { return false; }
		if(args[0].equalsIgnoreCase("reload")) {
			if(args.length > 1) { return false; }
			return ReloadData(dataStore);
		} else if (args[0].equalsIgnoreCase("getyamlvalue")){
			if(args.length > 2) { return false; }
			return GetYamlValue(sender, args[1], dataStore);
		} else if (args[0].equalsIgnoreCase("compactdb")) {
			if(args.length > 1) { return false; }
			return false;
			//return CompactDatabase(dataStore);
		}
		return false;
	}

	private static boolean ReloadData(DataStore dataStore) {
		Flags.instance.reloadConfig();
		dataStore.reload(Flags.instance);
		//Flags.instance.LoadFlagCounts();
		Flags.instance.getLogger().info("Flags Reloaded");
		return true;
	}
	
	private static boolean GetYamlValue(CommandSender sender, String path, DataStore dataStore) {
		sender.sendMessage(dataStore.read(path));
		return true;
	}
	
/*	private static boolean CompactDatabase(DataStore dataStore) {
		Set<String> areas = SystemManager.getAreaNames();
		
		for(String currentArea : areas) {
			Area area = SystemManager.getArea(currentArea);
			
			// Remove claims that don't exist in the system.
			if(area == null){
				Flags.instance.getLogger().info("Removing data for area that doesn't exist. Area: " + currentArea);
				dataStore.write("data." + currentArea, (String)null);
				Flags.instance.LoadFlagCounts();  // Unmanaged deletion.  Need to reload.
				continue;
			}

			Set<String> flags = SystemManager.getAreaFlags(currentArea);
			for (String currentFlag : flags) {
				Flag flag = new Flag();
				if((currentFlag.contains("trust")) || (currentFlag.equalsIgnoreCase("message"))){
					String flagname = currentFlag;
					flagname.replace("message", "");
					flagname.replace("trust", "");
					if(!flag.setType(flagname)) {
						Flags.instance.getLogger().info("Removing message or trustlist for unknown flag type. Claim: " + currentClaim + "Data: " + currentFlag);
						dataStore.write("data." + currentClaim + "." + currentFlag, (String)null);							
					}
					continue; 
				}

				if (!flag.setType(currentFlag)) { 
					Flags.instance.getLogger().info("Removing unknown flag data. Claim: " + currentClaim + "Flag: " + currentFlag);
					dataStore.write("data." + currentClaim + "." + currentFlag, (String)null);
					continue;
				}

				if (flag.getValue(claim) == flag.getValue(Flags.instance.getServer().getWorld(claim.getClaimWorldName()))) {
					Flags.instance.getLogger().info("Removing claim flag matching the global value. Claim: " + currentClaim + "Flag: " + currentFlag);
					flag.removeValue(claim, null);
					continue;
				}
			}
			
			flags = dataStore.readKeys("data." + currentClaim);
			if (flags.size() == 0) {
				Flags.instance.getLogger().info("Removing claim data with no flags. Claim: " + currentClaim);
				dataStore.write("data." + currentClaim, (String)null);
			}
		}
		Flags.instance.LoadFlagCounts();  // Possible unmanaged deletion.  Need to reload.
		Flags.instance.getLogger().info("Database Compacted");
		return true;
	}*/
}