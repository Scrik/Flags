package alshain01.Flags.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import alshain01.Flags.Flag;
import alshain01.Flags.Registrar;
import alshain01.Flags.Flags;
import alshain01.Flags.Message;
import alshain01.Flags.area.Area;
import alshain01.Flags.area.Default;
import alshain01.Flags.area.Subdivision;
import alshain01.Flags.economy.PurchaseType;

abstract class FlagCmd extends Common {
	protected static boolean get(Player player, CommandLocation location, Flag flag) {
		// Acquire the area
		Area area = getArea(player, location);
		if(area == null) { return false; }

		if (flag != null) {
			player.sendMessage(Message.GetFlag.get()
					.replaceAll("\\{AreaType\\}", area.getAreaType().toLowerCase())
					.replaceAll("\\{Flag\\}", flag.getName())
					.replaceAll("\\{Value\\}", getValue(area.getValue(flag, false)).toLowerCase()));
			return true;
		}
		
		// No flag provided, list all set flags for the area
		StringBuilder message = new StringBuilder(Message.GetAllFlags.get()
				.replaceAll("\\{AreaType\\}", area.getAreaType().toLowerCase()));
		boolean first = true; // Governs whether we insert a comma or not (true means no)
		Area defaultArea = new Default(player.getWorld());
		
		for (Flag f : Flags.instance.getRegistrar().getFlags()) {
			// Get the flag's value
			Boolean value = area.getValue(f, true);
			
			// Output the flag name
			if (value != null) {
				if ((area instanceof Default && value != f.getDefault()) 
						|| (!(area instanceof Default) && value != defaultArea.getValue(f, false))){
					if (!first) {
						message.append(", ");
					} else {
						first = false;
					}
					message.append(f.getName());
				}
			}
		}
		message.append(".");
		player.sendMessage(message.toString());

		return true;
	}
	
	protected static boolean set(Player player, CommandLocation location, Flag flag, Boolean value) {
		// Acquire the area
		Area area = getArea(player, location);
		if(area == null) { return false; }
		
		// Check permissions
		if (!allPermitted(flag, area, player)) { return true; } 
		
		// Acquire the value (maybe)
		if(value == null) {
			value = !area.getValue(flag, false);
		}
		
        // Set the flag
    	if(area.setValue(flag, value, player)) {
    			player.sendMessage(Message.SetFlag.get()
    					.replaceAll("\\{AreaType\\}", area.getAreaType().toLowerCase())
    					.replaceAll("\\{Flag\\}", flag.getName())
    					.replaceAll("\\{Value\\}", getValue(value).toLowerCase()));
    	}
        return true;
	}
	
	protected static boolean remove(Player player, CommandLocation location, Flag flag) {
		// Acquire the area
		Area area = getArea(player, location);
		if(area == null) { return false; }
		if (!areaPermitted(area, player)) { return true; }
		
		// Removing single flag type
		if (flag != null) {
			// Check that the player can set the flag type at this location
			if (!flagPermitted(flag, player)) { return true; }
			
			if(area.setValue(flag, null, player)) {
				player.sendMessage(Message.RemoveFlag.get()
						.replaceAll("\\{AreaType\\}", area.getAreaType().toLowerCase())
						.replaceAll("\\{Flag\\}", flag.getName()));
			}
			return true;
		}
		
		// Removing all flags if the player has permission
		boolean success = true;
		for (Flag f : Flags.instance.getRegistrar().getFlags()) {
			if(area.getValue(f, true) != null) {
				if (flagPermitted(flag, player)) {
					if (!area.setValue(f, null, player)) {
						success = false;
					}
				} else {
					success = false;
				}
			}
		}
		
		if (success) {
			player.sendMessage(Message.RemoveAllFlags.get()
					.replaceAll("\\{AreaType\\}", area.getAreaType().toLowerCase()));
		} else {
			player.sendMessage(Message.RemoveAllFlagsError.get());
		}
		return true;
	}
	
	protected static boolean help (CommandSender sender, int page, String group) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Message.NoConsoleError.get());
		}
		
		Registrar registrar = Flags.instance.getRegistrar();
		List<String> groupNames = new ArrayList<String>();
		List<String> allowedFlagNames = new ArrayList<String>();
		Set<String> flagNames = Flags.instance.getRegistrar().getFlagNames();

		// First we need to filter out the flags and groups to show to the particular user.
		for (String f : flagNames) {
			Flag flag = registrar.getFlag(f);
			// Add flags for the requested group only
			if(group == null || group.equalsIgnoreCase(flag.getGroup())) {
				// Only show flags that can be used.
				if(flag.hasPermission((Player)sender)){
					allowedFlagNames.add(flag.getName());
					// Add the group, but only once and only if a group hasn't been requested
					if(group == null && !groupNames.contains(flag.getGroup())) {
						groupNames.add(flag.getGroup()); // Add the flags group.
					}
				}
			}
		}
		
		// No flags were found, there should always be flags.
		List<String> combinedHelp = new ArrayList<String>();
		if(allowedFlagNames.size() == 0) { 
			sender.sendMessage(Message.NoFlagFound.get()
					.replaceAll("{Type}", Message.Flag.get().toLowerCase()));
			return true;
		}
		Collections.sort(allowedFlagNames);
		combinedHelp.addAll(allowedFlagNames);
		
		// Show them alphabetically and group them together for easier coding
		if(groupNames.size() > 0) {
			Collections.sort(groupNames);
			combinedHelp.addAll(groupNames);
		}
		
		//Get total pages
		//1 header per page
		//9 flags per page, except on the first which has a usage line and 8 flags
		int total = ((combinedHelp.size() + 1) / 9);
		
		
		// Add the last page, if the last page is not full (less than 9 flags)
		if ((combinedHelp.size() + 1) % 9 != 0) { total++; }
		
		//Check the page number requested
        if (page < 1 || page > total) { page = 1; }
        
		String indexType = Message.Index.get();
		if(group != null) {
			indexType = registrar.getFlag(combinedHelp.get(0)).getGroup();
		}
		
		sender.sendMessage(Message.HelpHeader.get()
				.replaceAll("\\{Group\\}", indexType)
				.replaceAll("\\{Page\\}", String.valueOf(page))
				.replaceAll("\\{TotalPages\\}", String.valueOf(total))
				.replaceAll("\\{Type\\}", Message.Flag.get()));
		
		// Setup for only displaying 10 lines at a time (including the header)
		int linecount = 0;
		
		// Usage line.
		if (page == 1) {
			if(group == null) {
				sender.sendMessage(Message.HelpInfo.get()
						.replaceAll("\\{Type\\}", Message.Flag.get().toLowerCase()));
				linecount++;
			} else {
				sender.sendMessage(Message.GroupHelpInfo.get()
						.replaceAll("\\{Type\\}", registrar.getFlag(combinedHelp.get(0)).getGroup()));
			}
		}
		
		// Find the start position in the array of names
		int position = ((page - 1) * 9) - 1;
		if(position < 0) { position = 0; }
		

		
		// Output the results
		while (position < combinedHelp.size()) {
			if(groupNames.contains(combinedHelp.get(position))) {
				sender.sendMessage(Message.HelpTopic.get()
						.replaceAll("\\{Topic\\}", combinedHelp.get(position))
						.replaceAll("\\{Description\\}", Message.GroupHelpDescription.get().replaceAll("\\{Group\\}", combinedHelp.get(position))));
			} else {
				sender.sendMessage(Message.HelpTopic.get()
						.replaceAll("\\{Topic\\}", combinedHelp.get(position))
						.replaceAll("\\{Description\\}", registrar.getFlag(combinedHelp.get(position)).getDescription()));
			}
			
			if(++linecount == 9) {
				return true;
			}
			position++;
		}
		return true;
	}
	

	
	protected static boolean viewTrust(Player player, CommandLocation location, Flag flag) {
		// Acquire the flag
		if(!flag.isPlayerFlag()) {
			player.sendMessage(Message.PlayerFlagError.get()
					.replaceAll("\\{Flag\\}", flag.getName()));
			return true;
		}
		
		// Acquire the area
		Area area = getArea(player, location);
		if(area == null) { return false; }

		// Acquire the current trust list
		Set<String> trustList = area.getTrustList(flag);
		if(trustList.size() == 0) {
			player.sendMessage(Message.InvalidTrustError.get()
					.replaceAll("\\{AreaType\\}", area.getAreaType().toLowerCase())
					.replaceAll("\\{Flag\\}", flag.getName()));
			return true;
		}
		
		// List all set flags
		StringBuilder message = new StringBuilder(Message.GetTrust.get()					
				.replaceAll("\\{AreaType\\}", area.getAreaType().toLowerCase())
				.replaceAll("\\{Flag\\}", flag.getName()));
		boolean first = true; // Governs whether we insert a comma or not (true means no)
		
		for (String p : trustList) {	
			// Output the flag name
			if (!first) {
				message.append(", ");
			} else {
				first = false;
			}
			message.append(p);
		}
	
		message.append(".");
		player.sendMessage(message.toString());

		return true;
	}
	
	protected static boolean trust(Player player, CommandLocation location, Flag flag, Set<String> playerList) {
		if(playerList.size() == 0) { return false; }
		
		// Acquire the area
		Area area = getArea(player, location);
		if(area == null) { return false; }

		// Check that the player can set the flag type at this location
		if (!allPermitted(flag, area, player)) { return true; }
		
		if(!flag.isPlayerFlag()) {
			player.sendMessage(Message.PlayerFlagError.get()
					.replaceAll("\\{Flag\\}", flag.getName()));
			return true;
		}
		
		boolean success = true;
		for(String p : playerList) {
			if(!area.setTrust(flag, p, true, player)) {
				success = false;
			}
		}
		if (success) {
			sender.sendMessage(Message.SetTrust.get()
					.replaceAll("\\{AreaType\\}", area.getAreaType().toLowerCase())
					.replaceAll("\\{Flag\\}", flag.getName()));
		} else {
			sender.sendMessage(Message.SetTrustError.get());
		}
		return true;
	}
	
	protected static boolean distrust(CommandSender sender, char location, String flagName, Set<String> playerList) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Message.NoConsoleError.get());
		}
		
		// Acquire the area
		Area area = getArea(sender, location);
		if(area == null) { return false; }
		if (!areaPermitted(area, (Player)sender)) { return true; }

		// Acquire the flag
		Flag flag = null;
		if(flagName != null) {
			flag = getFlag(sender, flagName, true);
			if (flag == null || !flagPermitted(flag, (Player)sender)) { return true; }
			if(!flag.isPlayerFlag()) {
				sender.sendMessage(Message.PlayerFlagError.get()
						.replaceAll("\\{Flag\\}", flagName));
				return true;
			}
		}

		
		
		Set<String> trustList = area.getTrustList(flag);
		if(trustList == null) {
			sender.sendMessage(Message.InvalidTrustError.get()
					.replaceAll("\\{AreaType\\}", area.getAreaType().toLowerCase())
					.replaceAll("\\{Flag\\}", flag.getName()));
			return true;
		}
		
		boolean success = true;
		// Remove all players
		if (playerList.size() == 0) {
			for (String p : trustList) {
				if (!area.setTrust(flag, p, false, (Player)sender)) {
					success = false;
				}
			}
		} else {
			// Remove 1 or more players
			for (String player : playerList) {
				if (!area.setTrust(flag, player, false, (Player)sender)) {
					success = false;
				}
			}
		}

		if (success) {
			sender.sendMessage(Message.RemoveTrust.get()
					.replaceAll("\\{AreaType\\}", area.getAreaType().toLowerCase())
					.replaceAll("\\{Flag\\}", flag.getName()));
		} else {
			sender.sendMessage(Message.RemoveTrustError.get());
		}
		
		return true;
	}

	protected static boolean presentMessage(CommandSender sender, char location, String flagName) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Message.NoConsoleError.get());
			return true;
		}
	
		// Acquire the flag
		Flag flag = getFlag(sender, flagName, true);
		if(flag == null) { return true; }
		if(!flag.isPlayerFlag()) {
			sender.sendMessage(Message.PlayerFlagError.get()
					.replaceAll("\\{Flag\\}", flagName));
			return true;
		}
		
		// Acquire the area
		Area area = getArea(sender, location);
		if(area == null) { return false; }
		
		// Send the message
		sender.sendMessage(area.getMessage(flag)
				.replaceAll("\\{Player\\}", ((Player)sender).getName()));
		return true;
	}

	protected static boolean message(CommandSender sender, char location, String flagName, String message) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Message.NoConsoleError.get());
			return true;
		}
		
		// Acquire the flag
		Flag flag = getFlag(sender, flagName, true);
		if(flag == null) { return true; }
		
		if(!flag.isPlayerFlag()) {
			sender.sendMessage(Message.PlayerFlagError.get()
					.replaceAll("\\{Flag\\}", flagName));
			return true;
		}
		
		// Acquire the player
		Player player = (Player)sender;
		
		// Acquire the area
		Area area = getArea(sender, location);
		if(area == null) { return false; }
		
		// Check that the player can set the flag type at this location
		if (!allPermitted(flag, area, player)) { return true; }
		
		if(area.setMessage(flag, message, player)) {;
			player.sendMessage(area.getMessage(flag)
					.replaceAll("\\{Player\\}", player.getName()));
		}
		return true;
	}

	protected static boolean erase(CommandSender sender, char location, String flagName) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Message.NoConsoleError.get());
			return true;
		}
		
		// Acquire the flag
		Flag flag = getFlag(sender, flagName, true);
		if(flag == null) { return true; }
		
		if(!flag.isPlayerFlag()) {
			sender.sendMessage(Message.PlayerFlagError.get()
					.replaceAll("\\{Flag\\}", flagName));
			return true;
		}
		
		// Acquire the area
		Area area = getArea(sender, location);
		if(area == null) { return false; }
		
		// Check that the player can set the flag type at this location
		if (!allPermitted(flag, area, (Player)sender)) { return true; }
		
		if (area.setMessage(flag, null, sender)) {;
			sender.sendMessage(area.getMessage(flag)
					.replaceAll("\\{Player\\}", ((Player)sender).getName()));
		}
		return true;
	}
	
	protected static boolean inherit(Player player, Boolean value) {
		// Acquire the area
		Area area = getArea(player, 'a');
		if(area == null) { return true; }
		
		if(!(area instanceof Subdivision) || !((Subdivision)area).isSubdivision()) {
			player.sendMessage(Message.SubdivisionError.get());
			return true;
		}
		
		// Acquire the value (if it isn't provided)
		if (value == null) {
			value = !((Subdivision)area).isInherited();
		}

		((Subdivision)area).setInherited(null); // Toggle
		player.sendMessage(Message.SetInherited.get()
				.replaceAll("\\{Value\\}", getValue(value).toLowerCase()));
		return true;		
	}
	
	protected static boolean getPrice(CommandSender sender, PurchaseType type, String flagName) {
		Flag flag = getFlag(sender, flagName, true);
		if(flag == null) { return true; }
		
		String price;
		if(Flags.instance.economy != null) {
			price = Flags.instance.economy.format(flag.getPrice(type));
		} else {
			price = String.valueOf(flag.getPrice(type));
		}
		
		sender.sendMessage(Message.GetPrice.get()
				.replaceAll("\\{PurchaseType\\}", type.getLocal().toLowerCase())
				.replaceAll("\\{Flag\\}", flag.getName())
				.replaceAll("\\{Price\\}", price));
		return true;
	}
	
	protected static boolean setPrice(CommandSender sender, PurchaseType type, String flagName, String price) {
		Flag flag = getFlag(sender, flagName, true);
		if(flag == null) { return true; }
		
		double p;
		try {
			p = Double.valueOf(price);
		} catch (NumberFormatException ex) {
			return false;
		}
		
		flag.setPrice(type, p);
		sender.sendMessage(Message.SetPrice.get()
				.replaceAll("\\{PurchaseType\\}", type.getLocal().toLowerCase())
				.replaceAll("\\{Flag\\}", flag.getName())
				.replaceAll("\\{Price\\}", price));
		return true;
	}
}