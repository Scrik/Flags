package alshain01.Flags.events;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import alshain01.Flags.Flag;
import alshain01.Flags.area.Area;

/** 
 * Event for that occurs when a trustee is added or removed.
 * This event may occur if there is nothing to add or remove. 
 * (the player being removed is not in the list or 
 * the player being added is already in the list)
 * 
 * @author Alshain01
 */
public class TrustChangedEvent extends Event implements Cancellable{ 

	private static final HandlerList handlers = new HandlerList();
	private Area area;
	private Flag flag;
	private String trustee;
	private CommandSender sender;
	private boolean value;
	private boolean cancel = false;
	
	/** 
	 * Class Constructor
	 * 
	 * @param area	  	The area the flag is being set for.
	 * @param trustee 	The player the trust is changing for.
	 * @param isTrusted	True if the player is being added, false if being removed. 
	 * @param sender  	The sender changing the trust.
	 * 
	 */
	public TrustChangedEvent(Area area, Flag flag, String trustee, boolean isTrusted, CommandSender sender) {
		this.area = area;
		this.flag = flag;
		this.trustee = trustee;
		this.sender = sender;
		this.value = isTrusted;
	}
	
	/** 
	 * @return The area associated with the event.
	 */
	public Area getArea() {
		return area;
	}
	
	/** 
	 * @return The flag type associated with the event.
	 */
	public Flag getFlag() {
		return flag;
	}
	
	/** 
	 * @return The player associated with the event. Null if no sender involved (caused by plug-in).
	 */
	public CommandSender getSender() {
		return sender;
	}
	
	/** 
	 * @return True if the player is being added, false if being removed.
	 */
	public boolean isTrusted() {
		return value;
	}

	/** 
	 * @return The name of the player who's trust is changing
	 */
	public String getTrustee() {
		return trustee;
	}
	
	/** 
	 * HandlerList for FlagSetEvent
	 * 
	 * @return A list of event handlers, stored per-event. Based on lahwran's fevents
	 */	
    public HandlerList getHandlers() {
        return handlers;
    }
    
	/** 
	 * Static HandlerList for FlagSetEvent
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
