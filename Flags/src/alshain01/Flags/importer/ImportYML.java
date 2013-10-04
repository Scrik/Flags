package alshain01.Flags.importer;
import java.io.*;
import org.bukkit.configuration.file.*;

/**
 * Modified YAML manager from http://wiki.bukkit.org/Configuration_API_Reference
 * 
 * @author bukkit.org
 */
class ImportYML {
	//private static JavaPlugin plugin;
	private String dataFile;
	private FileConfiguration customConfig = null;
	private File customConfigFile = null;
	

	// Construct a new CustomYML file
	protected ImportYML(String dataFile){
		this.dataFile = dataFile;
	}

	// Reloads the file to the MemorySection
    protected void reloadCustomConfig() {
        if (customConfigFile == null) {
        	customConfigFile = new File(dataFile);
        }
        customConfig = YamlConfiguration.loadConfiguration(customConfigFile);
    }

	// Get's the custom config file.
    protected FileConfiguration getCustomConfig() {
        if (customConfig == null) {
            this.reloadCustomConfig();
        }
        return customConfig;
    }
}
