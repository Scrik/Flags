package alshain01.Flags.commands;

enum EFlagCommand {
	SET('s', 3, 1, true, true, "Set <area|world|default> <flag> [true|false]"),
	GET('g', 2, 1, true, false, "Get <area|world|default> [flag]"),
	REMOVE ('r', 2, 1, true, false, "Remove <area|world|default> [flag]"),
	TRUST('t', 4, -1, true, true, "Trust <area|world|default> <flag> <player> [player]..."),
	DISTRUST('d', 3, -1, true, true, "Distrust <area|world|default> <flag> [player] [player]..."),
	VIEWTRUST('v', 3, 0, true, true, "ViewTrust <area|world|default> <flag>"),
	MESSAGE('m', 4, -1, true, true, "Message <area|world|default> <flag> <message>"),
	PRESENTMESSAGE('p', 3, 0, true, true, "PresentMessage <area|world|default> <flag>"),
	ERASEMESSAGE('e', 3, 0, true, true, "EraseMessage <area|world|default> <flag>"),
	CHARGE('c', 3, 1, false, true, "Charge <flag|message> <flag> [price]"),
	HELP ('h', 1, 2, false, null, "Help [group] [page]"),
	INHERIT('i', 1, 1, false, null, "Inherit [true|false]");
	
	char alias;
	int requiredArgs;
	int optionalArgs; //-1 for infinite
	boolean requiresLocation;
	Boolean requiresFlag; // null if flag isn't even an optional arg.
	String help;
	
	EFlagCommand(char alias, int requiredArgs, int optionalArgs, boolean hasLocation, Boolean requiresFlag, String help) {
		this.alias = alias;
		this.requiredArgs = requiredArgs;
		this.optionalArgs = optionalArgs;
		this.help = help;
		this.requiresLocation = hasLocation;
		this.requiresFlag = requiresFlag;
	}
	
	protected static EFlagCommand get(String name) {
		for(EFlagCommand c : EFlagCommand.values()) {
			if(name.toLowerCase().equals(c.toString().toLowerCase()) || name.toLowerCase().equals(String.valueOf(c.alias))) {
				return c;
			}
		}
		return null;
	}
	
	protected String getHelp() {
		return "/flag " + this.help;
	}
}
