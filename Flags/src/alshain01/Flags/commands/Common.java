package alshain01.Flags.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import alshain01.Flags.Flag;
import alshain01.Flags.Flags;
import alshain01.Flags.Message;
import alshain01.Flags.Director;
import alshain01.Flags.area.Area;
import alshain01.Flags.area.Default;
import alshain01.Flags.area.World;
/**
 * Top level class for command based functions
 * 
 * @author john01dav
 * @author Alshain01
 */
public abstract class Common {
	protected static String getValue(boolean value) {
        return (value) ? Message.ValueColorTrue.get() : Message.ValueColorFalse.get();
	}

	protected static boolean allPermitted(Flag flag, Area area, Player player) {
		return (flagPermitted(flag, player) && areaPermitted(area, player)) ? true : false;
	}
	
	protected static boolean flagPermitted(Flag flag, Player player) {
		if (player.isOp()
				|| player.hasPermission("flags.*") 
				|| player.hasPermission("flags.bypass.*")
				|| player.hasPermission(flag.getBypassPermission())) {
			return true;
		}
		player.sendMessage(Message.FlagPermError.get()
				.replaceAll("\\{Type\\}", Message.Flag.get().toLowerCase()));
		return false;
	}
	
	protected static boolean areaPermitted(Area area, Player player) {
		// Check that the player can set a flag at this location
		if (!area.hasPermission(player)) {
			if (area instanceof World) {
				player.sendMessage(Message.WorldPermError.get()
						.replaceAll("\\{AreaType\\}", area.getAreaType())
						.replaceAll("\\{Type\\}", Message.Flag.get().toLowerCase()));
			} else {
				player.sendMessage(Message.AreaPermError.get()
						.replaceAll("\\{AreaType\\}", area.getAreaType())
						.replaceAll("\\{OwnerName\\}", area.getOwners().toArray()[0].toString())
						.replaceAll("\\{Type\\}", Message.Flag.get().toLowerCase()));
			}
			return false;
		}
		return true;
	}
	
	protected static Flag getFlag(CommandSender sender, String flagname, boolean notify) {
		Flag flag = Flags.instance.getRegistrar().getFlagIgnoreCase(flagname);
		if (flag == null) {
			if(notify) {
				sender.sendMessage(Message.InvalidFlagError.get()
						.replaceAll("\\{RequestedName\\}", flagname)
						.replaceAll("\\{Type\\}", Message.Flag.get().toLowerCase()));
			}
			return null;
		}
		return flag;
	}
	
	protected static Flag getFlag(CommandSender sender, String flagname) {
		return getFlag(sender, flagname, true);
	}
	
	protected static Player getPlayer(CommandSender sender) {
		if (!(sender instanceof Player)) { 
			sender.sendMessage(Message.NoConsoleError.get());
			return null; 
		}
		return (Player) sender;
	}
	
	protected static Area getArea(CommandSender sender, char location) {
		if (location == 'd') {
			return new Default(((Player)sender).getWorld());
		} else if (location == 'w') {
			return new World(((Player)sender).getWorld());
		} else if (location == 'a') {
			Area area = Director.getAreaAt(((Player)sender).getLocation());
			if(area instanceof World) {
				sender.sendMessage(Message.NoAreaError.get()
						.replaceAll("\\{AreaType\\}", Director.getAreaType().toLowerCase()));
				area = null;
			}
			return area;
		}
		// Invalid location selection
		return null;
	}
}
