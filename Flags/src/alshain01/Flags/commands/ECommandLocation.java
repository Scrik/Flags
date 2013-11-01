package alshain01.Flags.commands;

enum ECommandLocation {
	AREA('a'), WORLD('w'), DEFAULT('d');

	char alias;
	
	ECommandLocation(char alias) {
		this.alias = alias;
	}
	
	public static ECommandLocation get(String name) {
		for(ECommandLocation c : ECommandLocation.values()) {
			if(name.toLowerCase().equals(c.toString().toLowerCase()) || name.toLowerCase().equals(String.valueOf(c.alias))) {
				return c;
			}
		}
		return null;
	}
}