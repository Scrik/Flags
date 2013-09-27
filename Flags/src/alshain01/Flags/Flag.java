package alshain01.Flags;	

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Flag {
	private boolean def;
	private String name;
	private String description;

	private boolean player;
	private String area;
	private String world;
	private String plugin;

	protected Flag(String name, String description, boolean def, String plugin, boolean player, String area, String world) {
		this.name = name;
		this.description = description;
		this.def = def;
		this.plugin = plugin;
		this.area = area;
		this.world = world;
		this.player = player;					
	}

	/**
	 * @return True if this flag is a player flag.
	 */
	public boolean isPlayerFlag() {
		return player;
	}

	/**
	 * @return The default value of this flag.
	 */
	public boolean getDefault() {
		return def;
	}

	/**
	 * @return The name of this flag.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return The default area message for this flag.
	 */
	public String getDefaultAreaMessage() {
		return ChatColor.translateAlternateColorCodes('&', area);
	}
	
	/**
	 * @return The group this flag is assigned to.
	 */
	public String getGroup() {
		return plugin;
	}
	
	/**
	 * @return The default world message for this flag.
	 */
	public String getDefaultWorldMessage() {
		return ChatColor.translateAlternateColorCodes('&', world);
	}
	
	/**
	 * @return The flag's description.
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * @return The permission string (flags.flagtype.flagname)
	 */
	public String getPermission() {
		return "flags.flagtype." + name.toLowerCase();
	}
	
	/**
	 * @return True if the provided player has permission to modify this flag.
	 */
    public final boolean hasPermission(Player player) {
		if (player.isOp() 
				|| player.hasPermission("flags.*") 
				|| player.hasPermission("flags.flagtype.*")
				|| player.hasPermission(getPermission())) {
			return true;
		}
		return false;
    }
	
    /**
     * @return The bypass permission string (flags.bypass.flagname)
	 */
	public String getBypassPermission() {
		return "flags.bypass." + name.toLowerCase();
	}
	
	/**
	 * @return True if the provided player has permission to ignore the effects of this flag.
	 */
    public final boolean hasBypassPermission(Player player) {
		if (player.isOp()
				|| player.hasPermission("flags.*") 
				|| player.hasPermission("flags.bypass.*")
				|| player.hasPermission(getBypassPermission())) {
			return true;
		}
		return false;
    }
}