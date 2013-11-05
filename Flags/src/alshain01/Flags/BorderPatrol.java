package alshain01.Flags;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import alshain01.Flags.area.Area;
import alshain01.Flags.events.PlayerChangedAreaEvent;

/**
 * Listener for handling Player Movement
 * 
 * @author Alshain01
 */
final class BorderPatrol implements Listener {
	/*
	 * Storage for the player's last known location
	 */
	private class PreviousMove {
		private long time;
		private Location location;

		private PreviousMove(Player player) {
			location = player.getLocation();
			time = 0;
		}

		private void update() {
			time = new Date().getTime();
		}

		private void update(Location location) {
			this.location = location;
			this.update();
		}
	}

	private static final int eventsDivisor = 
			Flags.getInstance().getConfig().getInt("Flags.BorderPatrol.EventDivisor");
	private static final int timeDivisor = 
			Flags.getInstance().getConfig().getInt("Flags.BorderPatrol.TimeDivisor");
	private static ConcurrentHashMap<String, PreviousMove> moveStore = 
			new ConcurrentHashMap<String, PreviousMove>();
	private static int eventCalls = 0;

	/*
	 * Remove any garbage entries that may have been left behind Probably won't
	 * happen, but just in case.
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private static void onPlayerJoin(PlayerJoinEvent event) {
		if (moveStore.containsKey(event.getPlayer().getName())) {
			moveStore.remove(event.getPlayer().getName());
		}
	}

	/*
	 * Remove the last location to keep memory usage low.
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private static void onPlayerQuit(PlayerQuitEvent event) {
		if (moveStore.containsKey(event.getPlayer().getName())) {
			moveStore.remove(event.getPlayer().getName());
		}
	}

	/*
	 * Monitor the player's movement
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onPlayerMove(PlayerMoveEvent e) {
		// Divide the number of events to prevent heavy event timing
		if (eventCalls++ <= eventsDivisor) {
			return;
		}

		eventCalls = 0;
		PreviousMove playerPrevMove = null;
		boolean process = false;

		if (!moveStore.containsKey(e.getPlayer().getName())) {
			// New player data, process it immediately.
			moveStore.put(e.getPlayer().getName(), playerPrevMove = new PreviousMove(e.getPlayer()));
			process = true;
		} else {
			// Use the old player data
			playerPrevMove = moveStore.get(e.getPlayer().getName());

			// Check to see if we have processed this player recently.
			process = new Date().getTime() - playerPrevMove.time > timeDivisor;
		}

		if (process) {
			// Acquire the area moving to and the area moving from.
			final Area areaTo = Director.getAreaAt(e.getTo());
			final Area areaFrom = Director.getAreaAt(playerPrevMove.location);

			// If they are the same area, don't bother.
			if (areaFrom.compareTo(areaTo) != 0) {
				// Call the event
				final PlayerChangedAreaEvent event = 
						new PlayerChangedAreaEvent(e.getPlayer(), areaTo, areaFrom);
				Bukkit.getServer().getPluginManager().callEvent(event);

				if (event.isCancelled()) {
					e.getPlayer().teleport(playerPrevMove.location,	TeleportCause.PLUGIN);
					playerPrevMove.update();
					return;
				}
			}

			// Update the class instance
			playerPrevMove.update(e.getPlayer().getLocation());
		}
	}
}