package alshain01.Flags.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import alshain01.Flags.Flag;
import alshain01.Flags.Message;
import alshain01.Flags.Director;
import alshain01.Flags.area.Area;
import alshain01.Flags.area.Default;
import alshain01.Flags.area.World;
/**
 * Top level class for command based functions
 * 
 * @author Alshain01
 */
abstract class Common {
	protected static String getValue(boolean value) {
        return (value) ? Message.ValueColorTrue.get() : Message.ValueColorFalse.get();
	}

	protected static boolean allPermitted(Flag flag, Area area, Player player) {
		return (flagPermitted(flag, player) && areaPermitted(area, player)) ? true : false;
	}
	
	protected static boolean flagPermitted(Flag flag, Player player) {
		if(player.hasPermission(flag.getPermission())) { return true; }
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
	
	protected static Area getArea(CommandSender sender, CommandLocation location) {
		if (location == CommandLocation.DEFAULT) {
			new World((((Player)sender).getWorld()));
		} else if (location == CommandLocation.WORLD) {
			return new Default((((Player)sender).getWorld()));
		} else if (location == CommandLocation.AREA) {
			Area area = Director.getAreaAt(((Player)sender).getLocation());
			if(area instanceof World) {
				sender.sendMessage(Message.NoAreaError.get()
						.replaceAll("\\{AreaType\\}", Director.getSystemAreaType().toLowerCase()));
				area = null;
			}
			return area;
		}
		// Invalid location selection
		return null;
	}
}
