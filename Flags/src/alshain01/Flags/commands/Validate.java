package alshain01.Flags.commands;

import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permissible;

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
	
	protected static boolean isPlayerFlag(CommandSender cs, Flag f) {
		if(f.isPlayerFlag()) { return true; }
		cs.sendMessage(Message.PlayerFlagError.get()
				.replaceAll("\\{Flag\\}", f.getName()));
		return false;
	}
	
	protected static boolean isTrustList(CommandSender cs, Set<String> tl, String a, String f) {
		if(tl != null && !tl.isEmpty()) { return true; }
		cs.sendMessage(Message.InvalidTrustError.get()
				.replaceAll("\\{AreaType\\}", a.toLowerCase())
				.replaceAll("\\{Flag\\}", f));
		return false;
	}

	protected static boolean isFlag(CommandSender cs, Flag f, String n) {
		if(f != null) { return true; }
		cs.sendMessage(Message.InvalidFlagError.get()
				.replaceAll("\\{RequestedName\\}", n)
				.replaceAll("\\{Type\\}", Message.Flag.get().toLowerCase()));
		return false;
	}
	
	protected static boolean isBundle(CommandSender cs, Set<String> b, String n) {
		if (b != null && !b.isEmpty()) { return true; }
		cs.sendMessage(Message.InvalidFlagError.get()
				.replaceAll("\\{RequestedName\\}", n)
				.replaceAll("\\{Type\\}", Message.Bundle.get().toLowerCase()));
		return false; 
	}
	
	protected static boolean isPermitted(Permissible p, Object o) {
		if(o instanceof Flag) {
			if(p.hasPermission(((Flag)o).getPermission())) { return true; }
			if(p instanceof CommandSender) {
				((CommandSender)p).sendMessage(Message.FlagPermError.get()
						.replaceAll("\\{Type\\}", Message.Flag.get().toLowerCase()));
			}
			return false;
		}
		
		if(o instanceof Area) {
			Area area = (Area)o;
			if (area.hasPermission(p)) { return true; }
			if(p instanceof CommandSender) {
				((CommandSender)p).sendMessage(((area instanceof World || area instanceof Default) 
						? Message.WorldPermError.get() : Message.AreaPermError.get())
							.replaceAll("\\{AreaType\\}", area.getAreaType())
							.replaceAll("\\{OwnerName\\}", area.getOwners().toArray()[0].toString())
							.replaceAll("\\{Type\\}", Message.Flag.get().toLowerCase()));
			}
			return false;
		}
		return true;
	}
	
	protected static boolean isBundlePermitted(Permissible p, Object o) {
		if(o instanceof String) {
			if(p.hasPermission("flags.bundle." + (String)o)) { return true; }
			if(p instanceof CommandSender) {
				((CommandSender)p).sendMessage(Message.FlagPermError.get()
						.replaceAll("\\{Type\\}", Message.Bundle.get().toLowerCase()));
			}
			return false;
		}
		
		if(o instanceof Area) {
			Area area = (Area)o;
			if (area.hasBundlePermission(p)) { return true; }
			if(p instanceof CommandSender) {
				((CommandSender)p).sendMessage(((area instanceof World || area instanceof Default)
						? Message.WorldPermError.get() : Message.AreaPermError.get())
							.replaceAll("\\{AreaType\\}", area.getAreaType())
							.replaceAll("\\{OwnerName\\}", area.getOwners().toArray()[0].toString())
							.replaceAll("\\{Type\\}", Message.Bundle.get().toLowerCase()));
			}
			return false;
		}
		return true;
	}
	
	protected static boolean canEditBundle(Permissible p) {
		if (p.hasPermission("flags.command.bundle.edit")) { return true; }
		if(p instanceof CommandSender) {
			((CommandSender)p).sendMessage(Message.BundlePermError.get());
		}
		return false;
	}
	
	protected static boolean canEditPrice(Permissible p) {
		if (p.hasPermission("flags.command.flag.charge")) { return true; }
		if(p instanceof CommandSender) {
			((CommandSender)p).sendMessage(Message.PricePermError.get());
		}
		return false;
	}
}
