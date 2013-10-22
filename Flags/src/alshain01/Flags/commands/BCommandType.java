package alshain01.Flags.commands;

enum BCommandType {
	SET('s', 4, 0, true, true, "Set <area|world|default> <bundle> <true|false>"),
	GET('g', 3, 0, true, true, "Get <area|world|default> <bundle>"),
	REMOVE ('r', 3, 0, true, true, "Remove <area|world|default> <bundle>"),
	HELP ('h', 1, 1, false, null, "Help [page]"),
	ADD ('a', 3, -1, false, true, "Add <bundle> <flag> [flag]..."),
	DELETE ('d', 3, -1, false, true, "Delete <bundle> <flag> [flag]..."),
	ERASE ('e', 2, 0, false, true, "Erase <bundle>");
	
	char alias;
	int requiredArgs;
	int optionalArgs; //-1 for infinite
	boolean requiresLocation;
	Boolean requiresBundle; // null if bundle isn't even an optional arg.
	String help;
	
	BCommandType(char alias, int requiredArgs, int optionalArgs, boolean hasLocation, Boolean requiresBundle, String help) {
		this.alias = alias;
		this.requiredArgs = requiredArgs;
		this.optionalArgs = optionalArgs;
		this.help = help;
		this.requiresLocation = hasLocation;
		this.requiresBundle = requiresBundle;
	}
	
	protected static BCommandType get(String name) {
		for(BCommandType c : BCommandType.values()) {
			if(name.toLowerCase().equals(c.toString().toLowerCase()) || name.toLowerCase().equals(String.valueOf(c.alias))) {
				return c;
			}
		}
		return null;
	}
	
	protected String getHelp() {
		return "/bundle " + this.help;
	}
}
