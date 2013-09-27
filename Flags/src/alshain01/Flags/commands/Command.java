package alshain01.Flags.commands;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.command.CommandSender;

import alshain01.Flags.Director;
import alshain01.Flags.Message;
import alshain01.Flags.Director.LandSystem;

/**
 * Command handler for Flags
 * 
 * @author Alshain01
 */
public abstract class Command {
	/**
	 * Executes the flag command, returning its success 
	 * 
	 * @param sender Source of the command
	 * @param args   Passed command arguments
	 * @return		 true if a valid command, otherwise false
	 */
	public static boolean onFlagCommand(CommandSender sender, String[] args) {
		final String command = "flag";
		// Action must always be present
		if (args.length < 1) { return false; }
		char action = args[0].toLowerCase().charAt(0);

		// Actions: Help (1 arg minimum)
		if (action == 'h') {
			if(!FlagCmd.help(sender, getPage(args), getGroup(args))) { return getHelp(sender, command, action); }
			return true;
		} else if (action == 'i') {
			if(!FlagCmd.inherit(sender, getValue(args, 1))) { return getHelp(sender, command, action); }
			return true;
		}
		
		String flag = null;
		if (args.length < 2) { return getHelp(sender, command, action); }
		char location = args[1].toLowerCase().charAt(0);
		
		if (Director.getSystem() == LandSystem.NONE && (location == 'a' || location == 'd')) {
			sender.sendMessage(Message.NoSystemError.get());
			return true;
		}

		if (args.length > 2) { flag = args[2]; } // Flag name can be omitted for some commands

		// Actions: Get, Remove, Count, Distrust (2 args minimum)
		if (action == 'g') {
			if(!FlagCmd.get(sender, location, flag)) { return getHelp(sender, command, action);	}
			return true;
		} else if (action == 'r') {
	    	if(!FlagCmd.remove(sender, location, flag)) { return getHelp(sender, command, action); }
	    	return true;
	    } else if (action == 'd') {
			// List of players for trust
			Set<String> players = new HashSet<String>();
			if(args.length > 3) { players = getPlayers(args); } // Players can be omitted to distrust all
	    	
			if(!FlagCmd.distrust(sender, location, flag, players)) { return getHelp(sender, command, action); }
			return true;
	    }
	    
		if (args.length < 3) { return getHelp(sender, command, action); }
	    
		// Actions: Set, Trust, ViewTrust, PresentMessage, EraseMessage (3 args minimum)
		if(action == 's') {
			if(!FlagCmd.set(sender, location, flag, getValue(args, 3))) { return getHelp(sender, command, action); }
			return true;
		} else if (action == 't') {
			// List of players for trust
			Set<String> players = getPlayers(args);

			if(!FlagCmd.trust(sender, location, flag, players)) { return getHelp(sender, command, action); }
			return true;
		} else if (action == 'p') {
			if(!FlagCmd.presentMessage(sender, location, flag)) { return getHelp(sender, command, action); }
			return true;
		} else if (action == 'e') {
			if(!FlagCmd.erase(sender, location, flag)) { return getHelp(sender, command, action); }
			return true;
		} else if (action == 'v') {
			if(!FlagCmd.viewTrust(sender, location, flag)) { return getHelp(sender, command, action); }
			return true;
		}
		
		if (args.length < 4) { return getHelp(sender, command, action); }
		
		// Actions: Message (4 args minimum)
		else if(action == 'm') {
	  		// Build the message from the remaining arguments
			StringBuilder message = new StringBuilder();
			for (int x = 3; x < args.length; x++) {
				message.append(args[x]);
				if (x < args.length - 1) {
					message.append(" ");
				}
			}
			if(!FlagCmd.message(sender, location, flag, message.toString())) { return getHelp(sender, command, action); }
			return true;
		}
		
		return false;
	}
	
	/**
	 * Executes the bundle command, returning its success 
	 * 
	 * @param sender Source of the command
	 * @param args   Passed command arguments
	 * @return		 true if a valid command, otherwise false
	 */
	public static boolean onBundleCommand(CommandSender sender, String[] args) {
		final String command = "bundle";
		// Action must always be present
		if (args.length < 1) { return false; }
		char action = args[0].toLowerCase().charAt(0);
		
		// Actions: Help (1 arg minimum)
		if (action == 'h') {
			if(!BundleCmd.help(sender, getPage(args))) { return getHelp(sender, command, action); }
			return true;
		}
		
		String bundle = null;
		if (args.length < 2) { return getHelp(sender, command, action); }
		char location = args[1].toLowerCase().charAt(0);
		
		if (Director.getSystem() == LandSystem.NONE && (action == 'g' || action == 's' || action == 'r') && (location == 'a' || location == 'd')) {
			sender.sendMessage(Message.NoSystemError.get());
			return true;
		}
		if (args.length > 2) { bundle = args[2]; } // Bundle name can be omitted for some commands
		
		// Actions: Remove, Erase (2 args minimum)
		if (action == 'g') {
			if(!BundleCmd.get(sender, location, bundle)) { return getHelp(sender, command, action); }
			return true;
		} else if (action == 'e') {
			if(!BundleCmd.erase(sender, args[1])) { return getHelp(sender, command, action); };
			return true;
		}
		
		if (args.length < 3) { return getHelp(sender, command, action); }
		
		// Actions: Set, Add, Delete (3 args minimum)
		// Build the flag list from the remaining arguments
		Set<String> flags = new HashSet<String>();
		for (int x = 2; x < args.length; x++) {
			flags.add(args[x]);
		}
			
		if (action == 'a') {
			if(!BundleCmd.add(sender, args[1], flags)) { return getHelp(sender, command, action); };
			return true;
		} else if (action == 'd') {
			if(!BundleCmd.delete(sender, args[1], flags)) { return getHelp(sender, command, action); };
			return true;
		} else if (action == 'r') {
			if(!BundleCmd.remove(sender, location, bundle)) { return getHelp(sender, command, action); };
			return true;
		} 
		
		if (args.length < 4) { return getHelp(sender, command, action); }
		
		if(action == 's') {
			if(!BundleCmd.set(sender, location, bundle, getValue(args, 3))) { return getHelp(sender, command, action); };
			return true;
		}		

		return false;
	}
	
	/**
	 * Returns a list of players starting with argument 4
	 * 
	 * @param args Command arguments
	 * @return A list of players
	 */
	private static Set<String> getPlayers(String[] args) {
		Set<String> players = new HashSet<String>();
		for(int a = 3; a < args.length; a++) {
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
	 * Returns a true, false, or null value from argument 4
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
	
	/**
	 * Returns a help message for a failed command if possible 
	 * 
	 * @param sender  Source of the command
	 * @param command The name of the command.
	 * @param action  The command action.
	 * @return		  true if a help message was sent, otherwise false
	 */
	private static boolean getHelp(CommandSender sender, String command, char action) {
		// Flag only actions
		if (command.equalsIgnoreCase("flag")) {
			if (action == 's') {
				sender.sendMessage("/flag Set <area|world|default> <flag> [true|false]");
				return true;
			}
			
			if (action == 'g') {
				sender.sendMessage("/flag Get <area|world|default> [flag]");
				return true;
			}
			
			if (action == 'r') {
				sender.sendMessage("/flag Remove <area|world|default> [flag]");
				return true;
			}
			
			if (action == 'h') {
				sender.sendMessage("/flag Help [group] [page]");
				return true;
			}
			
			if (action == 't') {
				sender.sendMessage("/flag Trust <area|world|default> <player> [player]...");
				return true;
			}
			
			if (action == 'd') {
				sender.sendMessage("/flag Distrust <area|world|default> [player] [player]...");
				return true;
			}
			
			if (action == 'v') {
				sender.sendMessage("/flag ViewTrust <area|world|default> <flag>");
				return true;
			}
			
			if (action == 'm') {
				sender.sendMessage("/flag Message <area|world|default> <flag> <message>");
				return true;
			}
			
			if (action == 'p') {
				sender.sendMessage("/flag PresentMessage <area|world|default> <flag>");
				return true;
			}
			
			if (action == 'e') {
				sender.sendMessage("/flag EraseMessage <area|world|default> <flag>");
				return true;
			}
			
			if (action == 'i') {
				sender.sendMessage("/flag Inherit [true|false]");
				return true;
			}
		}
		
		// Bundle only actions
		if (command.equalsIgnoreCase("bundle")) {
			if (action == 's') {
				sender.sendMessage("/bundle Set <area|world|default> <bundle> <true|false>");
				return true;
			}
			
			if (action == 'g') {
				sender.sendMessage("/bundle Get <area|world|default> <bundle>");
				return true;
			}
			
			if (action == 'r') {
				sender.sendMessage("/bundle Remove <area|world|default> <bundle>");
				return true;
			}

			if (action == 'h') {
				sender.sendMessage("/bundle Help [page]");
				return true;
			}
			
			if (action == 'a') {
				sender.sendMessage("/bundle Add <bundle> <flag> [flag]...");
				return true;
			}
		
			if (action == 'd') {
				sender.sendMessage("/bundle Delete <bundle> <flag> [flag]...");
				return true;
			}
			
			if (action == 'e') {
				sender.sendMessage("/bundle Erase <bundle>");
				return true;
			}
		}

		return false;
	}
}