package alshain01.Flags.commands;

import java.util.HashSet;
import java.util.Iterator;
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
	protected static boolean get(Player player, ECommandLocation location, String bundleName) {
		String f;
		Flag flag;
		Iterator<String> iter;
		Area area = getArea(player, location);
		Set<String> bundle = Bundle.getBundle(bundleName);
		
		if(!Validate.notNull(player, area)
				|| !Validate.isBundle(player, bundle, bundleName)
				|| !Validate.isBundlePermitted(player, area)
				|| !Validate.isBundlePermitted(player, bundleName))
		{ return true; }

		iter = bundle.iterator();
		while(iter.hasNext()) {
			f = iter.next();
        	flag = Flags.getRegistrar().getFlag(f);
        	if (flag == null) {
        		player.sendMessage("Invald bundle.yml entry: " + f);
        		continue;
        	}

    		player.sendMessage(Message.GetBundle.get()
    				.replaceAll("\\{Bundle\\}", f)
    				.replaceAll("\\{Value\\}", getValue(area.getValue(flag, false))));
		}
		return true;
	}
	
	protected static boolean set(Player player, ECommandLocation location, String bundleName, Boolean value) {
		boolean success = true;
		Flag flag;
		String f;
		Iterator<String> iter;
		Area area = getArea(player, location);
		Set<String> bundle = Bundle.getBundle(bundleName);
		
		if(!Validate.notNull(player, area)
				|| !Validate.isBundle(player, bundle, bundleName)
				|| !Validate.isBundlePermitted(player, area)
				|| !Validate.isBundlePermitted(player, bundleName))
		{ return true; }
		
		iter = bundle.iterator();
		while(iter.hasNext()) {
			f = iter.next();
        	flag = Flags.getRegistrar().getFlag(f);
        	if (flag == null) {
            	success = false;
            	player.sendMessage("Invald bundle.yml entry: " + f);
        		continue;
        	}
        	if(!area.setValue(flag, value, player)) { success = false; }
        }
        
		player.sendMessage((success ? Message.SetBundle.get() : Message.SetMultipleFlagsError.get())
    			.replaceAll("\\{AreaType\\}", area.getAreaType().toLowerCase())
    			.replaceAll("\\{Bundle\\}", bundleName)
    			.replaceAll("\\{Value\\}", getValue(value).toLowerCase()));
        return true;
	}
	
	protected static boolean remove(Player player, ECommandLocation location, String bundleName) {
		boolean success = true;
		String f;
		Iterator<String> iter;
		Flag flag;
		Area area = getArea(player, location);
		Set<String> bundle = Bundle.getBundle(bundleName);
		
		if(!Validate.notNull(player, area)
				|| !Validate.isBundle(player, bundle, bundleName)
				|| !Validate.isBundlePermitted(player, area)
				|| !Validate.isBundlePermitted(player, bundleName))
		{ return true; }
		
		iter = bundle.iterator();
		while(iter.hasNext()) {
			f = iter.next();
        	flag = Flags.getRegistrar().getFlag(f);
        	if (flag == null) {
            	success = false;
           		player.sendMessage("Invald bundle.yml entry: " + f);
        		continue;
        	}
    		if (!area.setValue(flag, null, player)) { success = false; }
		}
		
		player.sendMessage((success ? Message.RemoveBundle.get() : Message.RemoveAllFlags.get())
				.replaceAll("\\{AreaType\\}", area.getAreaType().toLowerCase())
				.replaceAll("\\{Bundle\\}", bundleName));
    	return true;
	}
	
	protected static boolean add(CommandSender sender, String bundleName, Set<String> flags) {
		if(sender instanceof Player && !Validate.canEditBundle((Player)sender)){ return true; }
		
		String f;
		Flag flag;
		Iterator<String> iter;
		Set<String> bundle = Bundle.getBundle(bundleName);
		
		if(bundle == null) {
			Permission perm = new Permission("flags.bundle." + bundleName, 
					"Grants ability to use the bundle " + bundleName, PermissionDefault.FALSE);
			perm.addParent("flags.bundle", true);
			Bukkit.getServer().getPluginManager().addPermission(perm);
			
			bundle = new HashSet<String>();
		}
		
		iter = flags.iterator();
		while(iter.hasNext()) {
			f = iter.next();
			flag = Flags.getRegistrar().getFlagIgnoreCase(f);
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
		if(sender instanceof Player && !Validate.canEditBundle((Player)sender)){ return true; }
		
		boolean success = true;
		String f;
		Flag flag;
		Iterator<String> iter;
		Set<String> bundle = Bundle.getBundle(bundleName.toLowerCase());
		
		if(!Validate.isBundle(sender, bundle, bundleName)) { return true; }

		iter = flags.iterator();
		while(iter.hasNext()) {
			f = iter.next();
        	flag = Flags.getRegistrar().getFlagIgnoreCase(f);
        	if (flag == null) {
            	success = false;
        		continue;
        	}
    		if (!bundle.remove(flag.getName())) { success = false; }
		}
		Bundle.setBundle(bundleName, bundle);
		
		sender.sendMessage((success ? Message.UpdateBundle.get() : Message.RemoveAllFlags.get())
				.replaceAll("\\{Bundle\\}", bundleName));
		return true;
	}
	
	protected static boolean erase(CommandSender sender, String bundleName) {
		if(sender instanceof Player && !Validate.canEditBundle((Player)sender)){ return true; }
		
		Set<String> bundles = Bundle.getBundleNames();
		if (bundles == null || bundles.size() == 0 || !bundles.contains(bundleName)) {
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
		
		//Get total pages: 1 header per page
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
