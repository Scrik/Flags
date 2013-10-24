package alshain01.Flags.commands;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import alshain01.Flags.Director;
import alshain01.Flags.Flag;
import alshain01.Flags.Flags;
import alshain01.Flags.Message;
import alshain01.Flags.Director.LandSystem;
import alshain01.Flags.economy.PurchaseType;

/**
 * Command handler for Flags
 * 
 * @author Alshain01
 */
public class Command {
	private Command(){}
	
	/**
	 * Executes the flag command, returning its success 
	 * 
	 * @param sender Source of the command
	 * @param args   Passed command arguments
	 * @return		 true if a valid command, otherwise false
	 */
	public static boolean onFlagCommand(CommandSender sender, String[] args) {
		if (args.length < 1) { return false; }
		final FCommandType command = FCommandType.get(args[0]);

		if(command == null) { return false;	}

		// Check argument length (-1 means infinite optional args)
		if(args.length < command.requiredArgs
				|| (command.optionalArgs > 0 && args.length > command.requiredArgs + command.optionalArgs)) {
			Flags.instance.Debug("Invalid Arguments");
			sender.sendMessage(command.getHelp());
			return true;
		}

		// Check the command location for those that apply
		CommandLocation location = null;
		if(command.requiresLocation) {
			location = CommandLocation.get(args[1]);
			if(location == null) {
				Flags.instance.Debug("Required Location Missing");
				sender.sendMessage(command.getHelp());
				return true;
			}
			
			// Make sure we can set flags at that location
			if (Director.getSystem() == LandSystem.NONE && (location == CommandLocation.AREA || location == CommandLocation.DEFAULT)) {
				sender.sendMessage(Message.NoSystemError.get());
				return true;
			}
		}
		
		// Location based commands require the player to be in the world
		// Inherit is a special case, doesn't require a location but assumes one exists
		if((location != null || command == FCommandType.INHERIT) && !(sender instanceof Player)) {
			sender.sendMessage(Message.NoConsoleError.get());
			return true;
		}

		// Get the flag if required.
		Flag flag = null;
		if(command.requiresFlag != null) {
			if(command.requiresFlag || (!command.requiresFlag && args.length >= 3)) {
				flag = Flags.instance.getRegistrar().getFlagIgnoreCase(args[2]);
				if(flag == null) {
					sender.sendMessage(Message.InvalidFlagError.get()
							.replaceAll("\\{RequestedName\\}", args[2])
							.replaceAll("\\{Type\\}", Message.Flag.get().toLowerCase()));
					return true;
				}
			}
		}

		// Process the command
		boolean success = false;
		if(command == FCommandType.HELP) {
			success = FlagCmd.help(sender, getPage(args), getGroup(args));
		} else if(command == FCommandType.INHERIT) {
			success = FlagCmd.inherit((Player)sender, getValue(args, 1));
		} else if(command == FCommandType.GET) {
			success = FlagCmd.get((Player)sender, location, flag);
		} else if(command == FCommandType.SET) {
			success = FlagCmd.set((Player)sender, location, flag, getValue(args, 3));
		} else if (command == FCommandType.REMOVE) {
			success = FlagCmd.remove((Player)sender, location, flag);
		} else if (command == FCommandType.VIEWTRUST) {
			success = FlagCmd.viewTrust((Player)sender, location, flag);
		} else if (command == FCommandType.TRUST) {
			// List of players for trust
			Set<String> players = getPlayers(args, command.requiredArgs - 1);
			
			success = FlagCmd.trust((Player)sender, location, flag, players);
		} else if (command == FCommandType.DISTRUST) {
			// List of players for distrust
			Set<String> players = new HashSet<String>();
			if(args.length > command.requiredArgs) { players = getPlayers(args, command.requiredArgs); } // Players can be omitted to distrust all
	    	
			success = FlagCmd.distrust((Player)sender, location, flag, players);
		} else if (command == FCommandType.PRESENTMESSAGE) {
			success = FlagCmd.presentMessage((Player)sender, location, flag);
		} else if (command == FCommandType.MESSAGE) {
	  		// Build the message from the remaining arguments
			StringBuilder message = new StringBuilder();
			for (int x = 3; x < args.length; x++) {
				message.append(args[x]);
				if (x < args.length - 1) {
					message.append(" ");
				}
			}
			
			success = FlagCmd.message((Player)sender, location, flag, message.toString());
		} else if (command == FCommandType.ERASEMESSAGE) {
			success = FlagCmd.erase((Player)sender, location, flag);
		} else if (command == FCommandType.CHARGE) {
			final PurchaseType t = PurchaseType.get(args[1]);
			if (t == null) { 
				success = false; 
			} else {
				if(args.length > 3) {
					success = FlagCmd.setPrice(sender, t, flag, args[3]);
				} else {
					success = FlagCmd.getPrice(sender, t, flag);
				}
			}
		}
		
		if(!success) { sender.sendMessage(command.getHelp()); }
		return true;
	}
	
	/**
	 * Executes the bundle command, returning its success 
	 * 
	 * @param sender Source of the command
	 * @param args   Passed command arguments
	 * @return		 true if a valid command, otherwise false
	 */
	public static boolean onBundleCommand(CommandSender sender, String[] args) {
		if (args.length < 1) { return false; }
		final BCommandType command = BCommandType.get(args[0]);

		if(command == null) { return false;	}
		
		// Check argument length (-1 means infinite optional args)
		if(args.length < command.requiredArgs
				|| (command.optionalArgs > 0 && args.length > command.requiredArgs + command.optionalArgs)) { 
			sender.sendMessage(command.getHelp());
			return true;
		}

		// Check the command location for those that apply
		CommandLocation location = null;
		if(command.requiresLocation) {
			location = CommandLocation.get(args[1]);
			if(location == null) {
				sender.sendMessage(command.getHelp());
				return true;
			}
			
			// Location based commands require the player to be in the world
			if(!(sender instanceof Player)) {
				sender.sendMessage(Message.NoConsoleError.get());
				return true;
			}
			
			// Make sure we can set flags at that location
			if (Director.getSystem() == LandSystem.NONE && (location == CommandLocation.AREA || location == CommandLocation.DEFAULT)) {
				sender.sendMessage(Message.NoSystemError.get());
				return true;
			}
		}
		
		String bundle = null;
		if(command.requiresBundle) {
			if(command.requiresLocation) {
				bundle = args[2];
			} else {
				bundle = args[1];
			}
		}
		
		// Process the command
		boolean success = false;
		if(command == BCommandType.HELP) {
			success = BundleCmd.help(sender, getPage(args));
		} else if(command == BCommandType.GET) {
			success = BundleCmd.get((Player)sender, location, bundle);
		} else if(command == BCommandType.SET) {
			Boolean value = getValue(args, 3);
			if(value == null) {
				success = false;
			} else {
				success = BundleCmd.set((Player)sender, location, bundle, getValue(args, 3));
			}
		} else if (command == BCommandType.REMOVE) {
			success = BundleCmd.remove((Player)sender, location, bundle);
		} else if(command == BCommandType.ADD) {
			Set<String> flags = new HashSet<String>();
			for (int x = 2; x < args.length; x++) {
				flags.add(args[x]);
			}
			
			success = BundleCmd.add((Player)sender, bundle, flags);
		} else if(command == BCommandType.DELETE) {
			Set<String> flags = new HashSet<String>();
			for (int x = 2; x < args.length; x++) {
				flags.add(args[x]);
			}
			
			success = BundleCmd.delete(sender, bundle, flags);
		} else if (command == BCommandType.ERASE) {
			success = BundleCmd.erase(sender, bundle);
		}
		
		if(!success) { sender.sendMessage(command.getHelp()); }
		return true;
	}
	
	/**
	 * Returns a list of players starting with argument 4
	 * 
	 * @param args Command arguments
	 * @return A list of players
	 */
	private static Set<String> getPlayers(String[] args, int start) {
		Set<String> players = new HashSet<String>();
		for(int a = start; a < args.length; a++) {
			players.add(args[a]);
		}
		return players;
	}
	
	/**
	 * Returns a page number from argument 2 or 3
	 * 
	 * @param args Command arguments
	 * @return The page number.
	 */
	private static int getPage(String[] args) {
		if(args.length < 2) { return 1; }
		
		String page;
		if(args.length >= 3) { page = args[2]; }
		else { page = args[1]; }
		
		// Either group or page was omitted, which one?
        try {
	        return Integer.valueOf(page);
        } catch(Exception e){
        	// It was a string.
        	return 1;
		}
	}
	
	private static String getGroup(String[] args) {
		if(args.length < 2) { return null; }
		if(args.length >= 3) { return args[1]; }
		
		// Either group or page was omitted, which one?
		try {
			Integer.valueOf(args[1]);
		} catch (Exception e) {
			// It was a string.
			return args[1];
		}
		// It was an integer
		return null;
	}
	
	/**
	 * Returns a true, false, or null value from the argument
	 * 
	 * @param args
	 * @return
	 */
	private static Boolean getValue(String[] args, int argument) {
		if (args.length > argument) {
			if(args[argument].toLowerCase().charAt(0) == 't') {
				return true;
			} else if (args[argument].toLowerCase().charAt(0) == 'f') {
				return false;
			}
		}
		return null;
	}
}