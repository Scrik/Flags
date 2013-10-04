package alshain01.Flags.area;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import alshain01.Flags.Flag;
import alshain01.Flags.Flags;
import alshain01.Flags.events.TrustChangedEvent;

final class Trust {
	private Trust(){}
	
	protected final static boolean setTrust (Area area, Flag flag, String trustee, CommandSender sender, String path) {
		// Set the trust
		Set<String> trustList = Flags.instance.dataStore.readSet(path);

		if (trustList == null) {
			trustList = new HashSet<String>(Arrays.asList(trustee.toLowerCase()));
		} else {
			if (trustList.contains(trustee.toLowerCase())) { return false; } // Player was already in the list!
			trustList.add(trustee.toLowerCase());
		}
   
		TrustChangedEvent event = new TrustChangedEvent(area, flag, trustee, true, sender);
		Bukkit.getServer().getPluginManager().callEvent(event);
		if (event.isCancelled()) { return false; }
   
		//Set the list
		Flags.instance.dataStore.write(path, trustList);
		return true;
	}
	
	protected final static boolean removeTrust (Area area, Flag flag, String trustee, CommandSender sender, String path) {
	   // Remove the trust
	   Set<String> trustList = Flags.instance.dataStore.readSet(path);
	   
	   if (trustList == null || !trustList.contains(trustee.toLowerCase())) { return false; }
	   
	   TrustChangedEvent event = new TrustChangedEvent(area, flag, trustee, false, sender);
	   Bukkit.getServer().getPluginManager().callEvent(event);
	   if (event.isCancelled()) { return false; }
	   
	   trustList.remove(trustee.toLowerCase());
	   Flags.instance.dataStore.write(path, trustList);
	   
	   return true;
	}
	
	protected final static boolean hasTrust(Flag flag, Player player, String path) {
		Set<String> trustList = Flags.instance.dataStore.readSet(path);
		if (trustList != null && trustList.contains(player.getName().toLowerCase())) {
			return true;
		}

		if (player.isOp()
				|| player.hasPermission("flags.*") 
				|| player.hasPermission("flags.bypass.*")
				|| player.hasPermission(flag.getBypassPermission())) {
			return true;
		}
		return false;
	}
}
