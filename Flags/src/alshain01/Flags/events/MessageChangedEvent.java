package alshain01.Flags.events;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import alshain01.Flags.Flag;
import alshain01.Flags.area.Area;
/** 
 * Event for that occurs when a message is set, changed or removed.
 * This event may occur if a message is changed to the same 
 * string that is currently set or if there is nothing to remove. 
 * 
 * @author Alshain01
 */
public class MessageChangedEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private Area area;
	private Flag flag;
	private String message;
	private CommandSender sender;
	private boolean cancel = false;
	
	/** 
	 * Class Constructor
	 * 
	 * @param area	  	The area the flag is being set for.
	 * @param message	The message the be set
	 * @param flag		The flag the message is being set for 
	 * @param sender  	The sender changing the trust.
	 * 
	 */
	public MessageChangedEvent(Area area, Flag flag, String message, CommandSender sender) {
		this.area = area;
		this.flag = flag;
		this.message = message;
		this.sender = sender;
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
	 * @return The CommandSender associated with the event. Null if no sender involved (caused by plug-in).
	 * @deprecated Ambiguous name.  Use {@link #getSender()} instead.
	 */
	@Deprecated
	public CommandSender getPlayer() {
		return getSender();
	}
	
	/** 
	 * @return The CommandSender associated with the event. Null if no sender involved (caused by plug-in).
	 */
	public CommandSender getSender() {
		return sender;
	}

	/** 
	 * @return The new message (null if being removed).
	 */
	public String getMessage() {
		return message;
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
