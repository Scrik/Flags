package alshain01.Flags.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import alshain01.Flags.area.Area;

/**
 * Event that occurs when a player first enters a new area.
 * 
 * @author Alshain01
 */
public class PlayerChangedAreaEvent extends Event implements Cancellable {
	
	private static final HandlerList handlers = new HandlerList();
	Area area;
	Player player;
	Area exitArea;
	boolean cancel = false;
	
	/**
	 * Class Constructor
	 * @param player The player involved with the event
	 * @param area  The area involved with the event
	 * @param areaLeft The area the player left to get to this area
	 */
	public PlayerChangedAreaEvent (Player player, Area area, Area areaLeft) {
		this.area = area;
		this.player = player;
		this.exitArea = areaLeft;
	}
	
	/** 
	 * @return The Player associated with the event.
	 */
	public Player getPlayer() {
		return player;
	}
	
	/** 
	 * @return The area associated with the event.
	 */
	public Area getArea() {
		return area;
	}
	
	/**
	 * @return The area the player left to enter this area.  Null if entering from world.
	 */
	public Area getAreaLeft() {
		return exitArea;
	}
	
	/**
	 * HandlerList for GlobalFlagSetEvent
	 * 
	 * @return A list of event handlers, stored per-event. Based on lahwran's fevents
	 */	
    public HandlerList getHandlers() {
        return handlers;
    }
    
	/** 
	 * Static HandlerList for GlobalFlagSetEvent
	 * 
	 * @return A list of event handlers, stored per-event. Based on lahwran's fevents
	 */
    public static HandlerList getHandlerList() {
        return handlers;
    }

	/** 
	 * Gets the cancellation state of this event. A cancelled event will not be executed in the server, but will still pass to other plugins
	 *  
     * @return true if this event is cancelled
	 */
	@Override
	public boolean isCancelled() {
		return cancel;
	}

    /**
     * Sets the cancellation state of this event. A cancelled event will not be executed in the server, but will still pass to other plugins. 
	 *
     *@param cancel - true if you wish to cancel this event
	 */
	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
}
