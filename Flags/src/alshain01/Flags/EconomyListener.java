/* Copyright 2013 Kevin Seiden. All rights reserved.

 This works is licensed under the Creative Commons Attribution-NonCommercial 3.0

 You are Free to:
    to Share — to copy, distribute and transmit the work
    to Remix — to adapt the work

 Under the following conditions:
    Attribution — You must attribute the work in the manner specified by the author (but not in any way that suggests that they endorse you or your use of the work).
    Non-commercial — You may not use this work for commercial purposes.

 With the understanding that:
    Waiver — Any of the above conditions can be waived if you get permission from the copyright holder.
    Public Domain — Where the work or any of its elements is in the public domain under applicable law, that status is in no way affected by the license.
    Other Rights — In no way are any of the following rights affected by the license:
        Your fair dealing or fair use rights, or other applicable copyright exceptions and limitations;
        The author's moral rights;
        Rights other persons may have either in the work itself or in how the work is used, such as publicity or privacy rights.

 Notice — For any reuse or distribution, you must make clear to others the license terms of this work. The best way to do this is with a link to this web page.
 http://creativecommons.org/licenses/by-nc/3.0/
*/

package alshain01.Flags;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import alshain01.Flags.area.Administrator;
import alshain01.Flags.area.Default;
import alshain01.Flags.area.World;
import alshain01.Flags.economy.BaseValue;
import alshain01.Flags.economy.PurchaseType;
import alshain01.Flags.economy.TransactionType;
import alshain01.Flags.events.FlagChangedEvent;
import alshain01.Flags.events.MessageChangedEvent;

/**
 * Economy manager for Flags
 * 
 * @author Alshain01
 */
final class EconomyListener implements Listener {
	private static boolean makeTransaction(TransactionType transaction, PurchaseType product, Flag flag, Player player) {
		// Get the YAML data path of the price.
		String pricePath = "Price." + product.toString() + "." + flag.getName();

		// Plug-in has not been configured for this flag, so it's assumed free.	
		if (!Flags.instance.dataStore.isSet(pricePath)) { return false; }
		
		// Get the price as a string
		double price = Flags.instance.dataStore.readDouble(pricePath);
		
		// Plug-in was configured for flag to be free.
		if (price == (double)0) { return false; } 

		EconomyResponse r;
		if (transaction == TransactionType.Withdraw) {
			// Check to see if they have the money.
			if (price > Flags.instance.economy.getBalance(player.getName())) {
				player.sendMessage(Message.LowFunds.get()
						.replaceAll("\\{PurchaseType\\}", product.getLocal().toLowerCase())
						.replaceAll("\\{Price\\}", Flags.instance.economy.format(price))
						.replaceAll("\\{Flag\\}", flag.getName()));
				return true;
			}
		
			// They have the money, make transaction
			r = Flags.instance.economy.withdrawPlayer(player.getName(), price);
		} else {
			// Deposit
			r = Flags.instance.economy.depositPlayer(player.getName(), price);
		}
		
		if (r.transactionSuccess()) {
			player.sendMessage(transaction.getLocal()
					.replaceAll("<1>", Flags.instance.economy.format(price)));
			return false;
		}
		
		// Something went wrong if we made it this far.
		Flags.instance.getLogger().severe(String.format("An error occured: %s", r.errorMessage));
		player.sendMessage(Message.Error.get()
				.replaceAll("\\{Error\\}", r.errorMessage));
		return true;
						
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	private static void onFlagChanged(FlagChangedEvent e) {
		// Don't charge for admin area, world or default areas & if there is no player, we can't do anything
		if (e.getArea() instanceof Default || e.getArea() instanceof World) { return; }
		if (e.getArea() instanceof Administrator && ((Administrator)e.getArea()).isAdminArea()) { return; }
		if (e.getSender() == null || !(e.getSender() instanceof Player)) { return; }
		
		// Acquire the player
		Player player = (Player)e.getSender();
		
		// Is the flag being deleted?
		if(e.getNewValue() == null) {
			// Check whether or not to refund the account
			if (!PurchaseType.Flag.isRefundable()) { return; }
			
			// Check whether or not to refund the account
			if (BaseValue.ALWAYS.isSet() 
					|| (BaseValue.PLUGIN.isSet() && e.getArea().getValue(e.getFlag(), true) != e.getFlag().getDefault()) 
					|| (BaseValue.DEFAULT.isSet() && e.getArea().getValue(e.getFlag(), true) != new Default(player.getLocation()).getValue(e.getFlag(), true)))
		    { 
				makeTransaction(TransactionType.Deposit, PurchaseType.Flag, e.getFlag(), player);
				return;
			}
		}
		
		// The flag is being set.
		
		// Check whether or not to charge the account
		if (BaseValue.ALWAYS.isSet() 
				|| (BaseValue.PLUGIN.isSet() && e.getArea().getValue(e.getFlag(), true) != e.getFlag().getDefault()) 
				|| (BaseValue.DEFAULT.isSet() && e.getArea().getValue(e.getFlag(), true) != new Default(player.getLocation()).getValue(e.getFlag(), true)))
	    { 
			// Charge the account
			e.setCancelled(makeTransaction(TransactionType.Withdraw, PurchaseType.Flag, e.getFlag(), player));
			return;
		}

		// Check whether or not to refund the account
		if (PurchaseType.Flag.isRefundable() && !BaseValue.ALWAYS.isSet()) {
			makeTransaction(TransactionType.Deposit, PurchaseType.Flag, e.getFlag(), player);
			return;
		}
		
	}
	
	/*
	 * Event handler for MessageChangedEvent
	 * 
	 * @param e The event data set.
	 */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	private static void onMessageChanged(MessageChangedEvent e) {
		// Don't charge for admin area, world or default areas & if there is no player, we can't do anything
		if (e.getArea() instanceof Default || e.getArea() instanceof World) { return; }
		if (e.getArea() instanceof Administrator && ((Administrator)e.getArea()).isAdminArea()) { return; }
		if (e.getSender() == null || !(e.getSender() instanceof Player)) { return; }
		
		// Acquire the player
		Player player = (Player)e.getSender();
		
		// Check to make sure we aren't removing the message
		if (e.getMessage() != null) {
			// Check to make sure the message isn't identical to what we have
			// (if they are just correcting caps, don't charge, I hate discouraging bad spelling & grammar)
			if(e.getArea().getMessage(e.getFlag(), false).equalsIgnoreCase(e.getMessage())) { return; }
			
			// Charge the account
			e.setCancelled(makeTransaction(TransactionType.Withdraw, PurchaseType.Message, e.getFlag(), player));
			return;
		}
		
		// If we got this far, the flag is being removed
		// Check whether or not to refund the account
		if (!PurchaseType.Message.isRefundable()) { return; }
		
		// Make sure the message we are refunding isn't identical to the default message
		if (!(e.getArea().getMessage(e.getFlag(), false).equals(e.getFlag().getDefaultAreaMessage()))) {
			makeTransaction(TransactionType.Deposit, PurchaseType.Message, e.getFlag(), player);
		}
	}
}