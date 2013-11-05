package alshain01.Flags.data;
import java.io.*;
import java.util.logging.Level;
import org.bukkit.configuration.file.*;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Modified YAML manager from http://wiki.bukkit.org/Configuration_API_Reference
 * 
 * @author bukkit.org
 */
public final class CustomYML {
	private static JavaPlugin plugin;
	private String dataFile;
	private FileConfiguration customConfig = null;
	private File customConfigFile = null;
	

	// Construct a new CustomYML file
	public CustomYML(JavaPlugin plugin, String dataFile){
		CustomYML.plugin = plugin;
		this.dataFile = dataFile;
	}

	// Reloads the file to the MemorySection
	public void reload() {
        if (customConfigFile == null) {
        	customConfigFile = new File(plugin.getDataFolder(), dataFile);
        }
        customConfig = YamlConfiguration.loadConfiguration(customConfigFile);
     
        // Look for defaults in the jar
        InputStream defConfigStream = plugin.getResource(dataFile);
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            customConfig.setDefaults(defConfig);
        }
    }

	// Get's the custom config file.
	public FileConfiguration getConfig() {
        if (customConfig == null) {
            this.reload();
        }
        return customConfig;
    }

    //Saves all changes
	public void saveConfig() {
        if (customConfig == null || customConfigFile == null) {
        	return;
        }
        try {
            getConfig().save(customConfigFile);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not save config to " + customConfigFile, ex);
        }
    }

	// Save a default config file to the data folder.
	public void saveDefaultConfig() {
        if (customConfigFile == null) {
            customConfigFile = new File(plugin.getDataFolder(), dataFile);
        }
        
        if (!customConfigFile.exists()) {            
             plugin.saveResource(dataFile, false);
         }
    }

}
