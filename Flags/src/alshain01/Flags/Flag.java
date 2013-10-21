package alshain01.Flags;	

import org.bukkit.entity.Player;

import alshain01.Flags.economy.PurchaseType;

/**
 * Represents a flag registered with the plug-in.
 * 
 * @author Alshain01
 */
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
	 * Retrieves whether or not the flag is a player flag. (Supports messaging and trust)
	 * 
	 * @return True if this flag is a player flag.
	 */
	public boolean isPlayerFlag() {
		return player;
	}

	/**
	 * Retrieves the plug-in default value of the flag.
	 * 
	 * @return The default value.
	 */
	public boolean getDefault() {
		return def;
	}

	/**
	 * Retrieves the name of the flag.
	 * 
	 * @return The name.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Retrieves a string representation of the flag meta.
	 * 
	 * @return The flag as a string
	 */
	@Override
	public String toString() {
		return "N=" + name + ",V=" + def + ",P=" + player + ",G=" + plugin + ",D=" + description + ",A=" + area + ",W=" + world;
	}
	
	/**
	 * Retrieves the default area message of the flag.
	 * 
	 * @return The default area message.
	 */
	public String getDefaultAreaMessage() {
		return area;
	}
	
	/**
	 * Retrieves the group the flag is assigned to.
	 * 
	 * @return The group this flag is assigned to.
	 */
	public String getGroup() {
		return plugin;
	}
	
	/**
	 * Retrieves the default world message of the flag
	 * 
	 * @return The default world message.
	 */
	public String getDefaultWorldMessage() {
		return world;
	}
	
	/**
	 * Retrieves the flag's description
	 * 
	 * @return The description.
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Retrieves the flagtype permission string.
	 * 
	 * @return The permission string (flags.flagtype.flagname)
	 */
	public String getPermission() {
		return "flags.flagtype." + name.toLowerCase();
	}
	
	/**
	 * Retrieves whether or not the provided player has permission to modify this flag.
	 * 
	 * @return True if the provided player has permission.
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
     * Retrieves the bypass permission string.
     * 
     * @return The bypass permission string (flags.bypass.flagname)
	 */
	public String getBypassPermission() {
		return "flags.bypass." + name.toLowerCase();
	}
	
	/**
	 * Retrieves whether or not the provided player has bypass permission for this flag.
	 * 
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
    
    public final double getPrice(PurchaseType type) {
    	if(!Flags.instance.dataStore.isSet("Price." + type.toString() + "." + name)) { return 0; }
    	return Flags.instance.dataStore.readDouble("Price." + type.toString() + "." + name);
    }
}