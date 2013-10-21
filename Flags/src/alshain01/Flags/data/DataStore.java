package alshain01.Flags.data;

import java.util.List;
import java.util.Set;

import org.bukkit.plugin.java.JavaPlugin;

public interface DataStore {
	public boolean exists(JavaPlugin plugin);
	
	public boolean reload(JavaPlugin plugin);
	
	public void write(String path, String value);
	
	public void write(String path, double value);
	
	public void write(String path, List<String> list);
	
	public void write(String path, Set<String> set);
	
	public boolean isSet(String path);
	
	public String read(String path);
	
	public int readInt(String path);
	
	public double readDouble(String path);
	
	public List<String> readList(String path);
	
	public Set<String> readSet(String path);
	
	public Set<String> readKeys(String path);
	
	public int getVersionMajor();
	
	public int getVersionMinor();
	
	public int getBuild();
	
	public void setVersion(String version);
	
	// Wrapper to keep DatabaseManager private.
	public boolean create(JavaPlugin plugin);
	
	public void update(JavaPlugin plugin);
}
