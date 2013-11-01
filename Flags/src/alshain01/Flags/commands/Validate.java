package alshain01.Flags.commands;

import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import alshain01.Flags.Director;
import alshain01.Flags.Flag;
import alshain01.Flags.Message;
import alshain01.Flags.area.Area;
import alshain01.Flags.area.Default;
import alshain01.Flags.area.Subdivision;
import alshain01.Flags.area.World;

public class Validate {
	private Validate() {}
	
	protected static boolean notNull(CommandSender cs, Object o) {
		if(o == null) {
			if (o instanceof Area) {
				cs.sendMessage(Message.NoAreaError.get()
						.replaceAll("\\{AreaType\\}", Director.getSystemAreaType().toLowerCase()));
			}
			return false;
		}
		return true;
	}
	
	protected static boolean isSubdivision(CommandSender cs, Area a) {
		if(!(a instanceof Subdivision) || !((Subdivision)a).isSubdivision()) {
			cs.sendMessage(Message.SubdivisionError.get());
			return false;
		}
		return true;
	}
	
	protected static boolean isTrustList(CommandSender cs, Set<String> tl, String a, String f) {
		if(tl != null && !tl.isEmpty()) { return true; }
		cs.sendMessage(Message.InvalidTrustError.get()
				.replaceAll("\\{AreaType\\}", a.toLowerCase())
				.replaceAll("\\{Flag\\}", f));
		return false;
	}
	
	protected static boolean isPermitted(Player p, Object o) {
		if(o instanceof Flag) {
			if(p.hasPermission(((Flag)o).getPermission())) { return true; }
			p.sendMessage(Message.FlagPermError.get()
					.replaceAll("\\{Type\\}", Message.Flag.get().toLowerCase()));
			return false;
		}
		
		if(o instanceof Area) {
			Area area = (Area)o;
			if (area.hasPermission(p)) { return true; }
			p.sendMessage(((area instanceof World || area instanceof Default) 
					? Message.WorldPermError.get() : Message.AreaPermError.get())
						.replaceAll("\\{AreaType\\}", area.getAreaType())
						.replaceAll("\\{OwnerName\\}", area.getOwners().toArray()[0].toString())
						.replaceAll("\\{Type\\}", Message.Flag.get().toLowerCase()));
			return false;
		}
		return true;
	}
	
	protected static boolean isPlayerFlag(CommandSender cs, Flag f) {
		if(f.isPlayerFlag()) { return true; }
		cs.sendMessage(Message.PlayerFlagError.get()
				.replaceAll("\\{Flag\\}", f.getName()));
		return false;
	}
}
