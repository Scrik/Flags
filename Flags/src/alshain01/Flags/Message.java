package alshain01.Flags;

import org.bukkit.ChatColor;

/**
 * Class for retrieving localized messages.
 * 
 * @author Alshain01
 */
public enum Message {
	// Errors
	NoConsoleError, InvalidFlagError, InvalidTrustError, NoFlagFound,
	SetTrustError, RemoveTrustError, RemoveAllFlagsError, SetMultipleFlagsError,
	AddBundleError, FlagPermError, AreaPermError, WorldPermError, NoAreaError,
	EraseBundleError, BundlePermError, PricePermError, SubdivisionError, NoSystemError,
	PlayerFlagError,
	// Commands
	SetFlag, GetFlag, RemoveFlag, InheritedFlag, SetTrust, GetTrust, RemoveTrust,
	GetAllFlags, RemoveAllFlags, GetBundle, SetBundle, RemoveBundle, UpdateBundle,
	EraseBundle, SetInherited,
	// Areas
	Default, World, GriefPrevention, WorldGuard, Residence, InfinitePlots, Factions, PlotMe,
	// Help
	ConsoleHelpHeader, HelpHeader, HelpTopic, HelpInfo, FlagCount, SetFlagTrustError,
	GroupHelpDescription, GroupHelpInfo,
	// General Translations
	Flag, Bundle, Message, ValueColorTrue, ValueColorFalse, Index, Error,
	// Economy
	SetPrice, GetPrice, LowFunds, Withdraw, Deposit;

	/**
	 * Gets the localized message for the enumeration
	 * 
	 * @return the message associated with the enumeration
	 */
	public final String get() {
		final String message = Flags.messageStore.getConfig().getString(
				"Message." + toString());
		if (message == null) {
			Flags.getInstance().getLogger().warning("ERROR: Invalid message.yml Message for " + toString());
			return "ERROR: Invalid message.yml Message. Please contact your server administrator.";
		}
		return ChatColor.translateAlternateColorCodes('&', message);
	}
}