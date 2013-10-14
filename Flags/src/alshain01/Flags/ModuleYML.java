package alshain01.Flags;
import org.bukkit.configuration.file.*;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Modified YAML manager from http://wiki.bukkit.org/Configuration_API_Reference
 * Handles reading YAML from a source file without saving it to disk.
 * 
 * @author bukkit.org
 * @author Alshain01
 */
public final class ModuleYML {
	private final JavaPlugin plugin;
	private final String fileName;
	private FileConfiguration fileConfig = null;

	/**
	 * Create an instance of ModuleYML
	 * 
	 * @param plugin The plugin that contains the yml file as a resource
	 * @param dataFile The file name
	 */
	public ModuleYML(JavaPlugin plugin, String dataFile){
		this.plugin = plugin;
		this.fileName = dataFile;
	}

	/**
	 * Reloads the file to the MemorySection
	 */
	public void reloadModuleData() {
    	try {
	        fileConfig = new YamlConfiguration();
	        fileConfig.load(plugin.getResource(fileName));
    	} catch (Exception e) {
    		plugin.getLogger().severe("Could not load data from " + fileName);
    	}
    }

	/**
	 * Retrieves the file configuration for the module data.
	 * 
	 * @return  the custom file configuration.
	 */
	public FileConfiguration getModuleData() {
        if (fileConfig == null) {
            this.reloadModuleData();
        }
        return fileConfig;
    }
}
