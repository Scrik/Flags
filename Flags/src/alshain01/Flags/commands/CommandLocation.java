package alshain01.Flags.commands;

enum CommandLocation {
	AREA('a'), WORLD('w'), DEFAULT('d');

	char alias;
	
	CommandLocation(char alias) {
		this.alias = alias;
	}
	
	public static CommandLocation get(String name) {
		for(CommandLocation c : CommandLocation.values()) {
			if(name.toLowerCase().equals(c.toString().toLowerCase()) || name.toLowerCase().equals(String.valueOf(c.alias))) {
				return c;
			}
		}
		return null;
	}
}