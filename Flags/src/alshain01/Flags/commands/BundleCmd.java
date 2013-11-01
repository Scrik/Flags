package alshain01.Flags.commands;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import alshain01.Flags.Bundle;
import alshain01.Flags.Flag;
import alshain01.Flags.Flags;
import alshain01.Flags.Message;
import alshain01.Flags.area.Area;

abstract class BundleCmd extends Common {
	protected static boolean areaPermitted(Area area, Player player) {
		// Check that the player can set a bundle at this location
		if (!area.hasBundlePermission(player)) {
			player.sendMessage(Message.AreaPermError.get()
					.replaceAll("\\{AreaType\\}", area.getOwners().toArray()[0].toString())
					.replaceAll("\\{Type\\}", Message.Bundle.get().toLowerCase()));
			return false;
		}
		return true;
	}
	
	protected static boolean bundleEditPermitted(Player player) {
			if (player.hasPermission("flags.command.bundle.edit")) {
			return true;
		}
		player.sendMessage(Message.BundlePermError.get());
		return false;
	}
	
	protected static boolean get(CommandSender sender, CommandLocation location, String bundleName) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Message.NoConsoleError.get());
			return true;
		}
		
		// Acquire the area
		Area area = getArea(sender, location);
		if(area == null) { return false; }
		
		// Acquire the bundle type requested in the command
		Set<String> bundle = Bundle.getBundle(bundleName);
		if (bundle == null || bundle.size() == 0) { 
			sender.sendMessage(Message.InvalidFlagError.get()
					.replaceAll("\\{RequestedName\\}", bundleName)
					.replaceAll("\\{Type\\}", Message.Bundle.get().toLowerCase()));
			return true; 
		}

		for (String f : bundle) {
        	Flag flag = Flags.getRegistrar().getFlag(f);
        	if (flag != null) {
        		sender.sendMessage(Message.GetBundle.get()
        				.replaceAll("\\{Bundle\\}", f)
        				.replaceAll("\\{Value\\}", getValue(area.getValue(flag, false))));
        		continue;
        	}
        	sender.sendMessage("Invald bundle.yml entry: " + f);
		}
		return true;
	}
	
	protected static boolean set(CommandSender sender, CommandLocation location, String bundleName, Boolean value) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Message.NoConsoleError.get());
			return true;
		}
		
		// Acquire the player
		Player player = (Player)sender;
		
		// Acquire the area
		Area area = getArea(sender, location);
		if(area == null) { return false; }
		if(!areaPermitted(area, player)) { return true; }
		
		// Acquire the bundle type requested in the command
		Set<String> bundle = Bundle.getBundle(bundleName);
		if (bundle == null || bundle.size() == 0) { 
			sender.sendMessage(Message.InvalidFlagError.get()
					.replaceAll("\\{RequestedName\\}", bundleName)
					.replaceAll("\\{Type\\}", Message.Bundle.get().toLowerCase()));
			return true; 
		}
		
		// Check that the player can set the bundle type at this location
		if (!player.hasPermission("flags.bundle." + bundleName)) {
			sender.sendMessage(Message.FlagPermError.get()
					.replaceAll("\\{Type\\}", Message.Bundle.get().toLowerCase()));
			return true;
		}
		
		boolean success = true;

		// Set the flags
        for (String f : bundle) {
        	Flag flag = Flags.getRegistrar().getFlag(f);
        	if (flag != null) {
        		if(!area.setValue(flag, value, player)) {
        			success = false;
        		}
        		continue;
        	}
        	success = false;
        	sender.sendMessage("Invald bundle.yml entry: " + f);
        }
        
        if(success) {
        	sender.sendMessage(Message.SetBundle.get()
        			.replaceAll("\\{AreaType\\}", area.getAreaType().toLowerCase())
        			.replaceAll("\\{Bundle\\}", bundleName)
        			.replaceAll("\\{Value\\}", getValue(value).toLowerCase()));
        } else {
        	sender.sendMessage(Message.SetMultipleFlagsError.get());
        }
        return true;
	}
	
	protected static boolean remove(CommandSender sender, CommandLocation location, String bundleName) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Message.NoConsoleError.get());
		}
		
		// Acquire the player
		Player player = (Player)sender;
		
		// Acquire the area
		Area area = getArea(sender, location);
		if(area == null) { return false; }
		if(!areaPermitted(area, player)) { return true; }
		
		// Acquire the bundle type requested in the command
		Set<String> bundle = Bundle.getBundle(bundleName);
		if (bundle == null || bundle.size() == 0) { 
			sender.sendMessage(Message.InvalidFlagError.get()
					.replaceAll("\\{RequestedName\\}", bundleName)
					.replaceAll("\\{Type\\}", Message.Bundle.get().toLowerCase()));
			return true; 
		}
		
		// Check that the player can set the bundle type at this location
		if (!player.hasPermission("flags.bundle" + bundleName)) {
			sender.sendMessage(Message.FlagPermError.get()
					.replaceAll("\\{Type\\}", Message.Bundle.get().toLowerCase()));
			return true;
		}
		
		boolean success = true;
		
		// Removing all flags
		for (String f : bundle) {
        	Flag flag = Flags.getRegistrar().getFlag(f);
        	if (flag != null) {
        		if (!area.setValue(flag, null, player)) {
        			success = false;
        		}
        		continue;
        	}
        	success = false;
       		sender.sendMessage("Invald bundle.yml entry: " + f);
		}
		
		if (success) {
			sender.sendMessage(Message.RemoveBundle.get()
					.replaceAll("\\{AreaType\\}", area.getAreaType().toLowerCase())
					.replaceAll("\\{Bundle\\}", bundleName));
		} else {
			sender.sendMessage(Message.RemoveAllFlagsError.get());
		}
    	return true;
	}
	
	protected static boolean add(CommandSender sender, String bundleName, Set<String> flags) {
		if(sender instanceof Player && !bundleEditPermitted((Player)sender)){ return true; }
		
		Set<String> bundle = Bundle.getBundle(bundleName);
		if (bundle == null || bundle.size() == 0) { 
			if(bundle == null) {
				Permission perm = new Permission("flags.bundle." + bundleName, "Grants ability to use the bundle " + bundleName, PermissionDefault.FALSE);
				perm.addParent("flags.bundle", true);
				Bukkit.getServer().getPluginManager().addPermission(perm);
			}
			bundle = new HashSet<String>();
		}
		
		for (String f : flags) {
        	Flag flag = Flags.getRegistrar().getFlagIgnoreCase(f);
        	if (flag == null) {
        		sender.sendMessage(Message.AddBundleError.get());
        		return true;
       		}
        	bundle.add(flag.getName());
		}
       	
		Bundle.setBundle(bundleName, bundle);
		sender.sendMessage(Message.UpdateBundle.get()
				.replaceAll("\\{Bundle\\}", bundleName));
		return true;
	}
	
	protected static boolean delete(CommandSender sender, String bundleName, Set<String> flags) {
		if(sender instanceof Player && !bundleEditPermitted((Player)sender)){ return true; }
		
		Set<String> bundle = Bundle.getBundle(bundleName.toLowerCase());
		if (bundle == null || bundle.size() == 0) { 
			sender.sendMessage(Message.InvalidFlagError.get()
					.replaceAll("\\{RequestedName\\}", bundleName)
					.replaceAll("\\{Type\\}", Message.Bundle.get().toLowerCase()));
			return true;
		}
		
		boolean success = true;
		for (String f : flags) {
        	Flag flag = Flags.getRegistrar().getFlagIgnoreCase(f);
        	if (flag != null) {
        		if (!bundle.remove(flag.getName())) {
        			success = false;
        		}
        		continue;
        	}
        	success = false;
		}
		
		Bundle.setBundle(bundleName, bundle);
		
		if (success) {
			sender.sendMessage(Message.UpdateBundle.get()
					.replaceAll("\\{Bundle\\}", bundleName));
		} else {
			sender.sendMessage(Message.RemoveAllFlagsError.get());
		}
		return true;
	}
	
	protected static boolean erase(CommandSender sender, String bundleName) {
		if(sender instanceof Player && !bundleEditPermitted((Player)sender)){ return true; }
		
		Set<String> bundle = Bundle.getBundleNames();
		if (bundle == null || bundle.size() == 0 || !bundle.contains(bundleName)) {
			sender.sendMessage(Message.EraseBundleError.get());
			return true;
		}
		
		Bundle.setBundle(bundleName, null);
		Bukkit.getServer().getPluginManager().removePermission("flags.bundle." + bundleName);
		
		sender.sendMessage(Message.EraseBundle.get()
				.replaceAll("\\{Bundle\\}", bundleName));
		return true;
	}
	
	protected static boolean help (CommandSender sender, int page) {
		Set<String> bundles = Bundle.getBundleNames();
		if (bundles == null || bundles.size() == 0) { 
			sender.sendMessage(Message.NoFlagFound.get()
					.replaceAll("\\{Type\\}", Message.Bundle.get()));
			return true; 
		}
		
		int total = 1;
		
		//Get total pages
		//1 header per page
		//9 flags per page, except on the first which has a usage line and 8 flags
		total = ((bundles.size() + 1) / 9);
		if ((bundles.size() + 1) % 9 != 0) { 
			total++; // Add the last page, if the last page is not full (less than 9 flags) 
		}
		
		//Check the page number requested
        if (page < 1 || page > total) {
        	page = 1;
        }
        
		sender.sendMessage(Message.HelpHeader.get()
				.replaceAll("\\{Type\\}", Message.Index.get())
				.replaceAll("\\{Page\\}", String.valueOf(page))
				.replaceAll("\\{TotalPages\\}", String.valueOf(total))
				.replaceAll("\\{Type\\}", Message.Bundle.get()));
		
		// Setup for only displaying 10 lines at a time
		int linecount = 1;
		
		// Usage line.  Displays only on the first page.
		if (page == 1) {
			sender.sendMessage(Message.HelpInfo.get()
					.replaceAll("\\{Type\\}", Message.Bundle.get().toLowerCase()));
			linecount++;
		}
		
		// Because the first page has 1 less flag count than the rest, 
		// manually initialize the loop counter by subtracting one from the 
		// start position of all pages other than the first.
		int loop = 0;
		if (page > 1) {
			loop = ((page-1)*9)-1;
		}
		
		String[] bundleArray = new String[bundles.size()];
		bundleArray = bundles.toArray(bundleArray);
		
		// Show the flags
		for (; loop < bundles.size(); loop++) {
			Set<String> flags = Bundle.getBundle(bundleArray[loop]);
			if (flags == null) { continue; }
			StringBuilder description = new StringBuilder("");
			boolean first = true;

			for (String f : flags) {
				if(!first){
					description.append(", ");
				} else {
					first = false;
				}
				
				description.append(f);
			}
			sender.sendMessage(Message.HelpTopic.get()
					.replaceAll("\\{Topic\\}", bundleArray[loop])
					.replaceAll("\\{Description\\}", description.toString()));

			linecount++;
			
			if (linecount > 9) {
				return true; // Page is full, we're done
			}
		}
		return true; // Last page wasn't full (that's ok)
	}
}
