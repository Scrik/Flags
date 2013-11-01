package alshain01.Flags.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

	protected static Area getArea(CommandSender sender, ECommandLocation location) {
		if (location == ECommandLocation.DEFAULT) {
			return new Default((((Player)sender).getWorld()));
		} else if (location == ECommandLocation.WORLD) {
			return new World((((Player)sender).getWorld()));
		} else if (location == ECommandLocation.AREA) {
			Area area = Director.getAreaAt(((Player)sender).getLocation());
			return (area instanceof World) ? null : area;
		}
		// Invalid location selection
		return null;
	}
}
