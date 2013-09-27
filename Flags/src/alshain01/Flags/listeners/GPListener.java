package alshain01.Flags.listeners;
import org.bukkit.event.Listener;

/**
 * Listener for Grief Prevention events
 * @author Alshain01
 * @deprecated
 */
public class GPListener implements Listener {
/*	@EventHandler (ignoreCancelled = true)
	private void onSiegeStart(SiegeStartEvent e) {
		List<Claim> siegedClaims = e.getSiegeData().claims;
		Flag flag = Flags.instance.getRegistrar().getFlag("Siege");
		Player player = e.getSiegeData().attacker;
		
		for(Claim c : siegedClaims) {
			Area area = new GriefPreventionClaim(c.getID());
			if (!area.getValue(flag, false) && !flag.hasBypassPermission(player) && !area.getTrustList(flag).contains(player.getName())) {
				player.sendMessage(area.getMessage(flag)
						.replaceAll("<0>", player.getName()));
				e.setCancelled(true);
				return;
			}
		}
	}*/
}