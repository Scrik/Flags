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

package alshain01.Flags.data;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.plugin.java.JavaPlugin;

public final class YamlDataStore implements DataStore {
	private static CustomYML data;
	private static CustomYML def;
	private static CustomYML world;
	private static CustomYML bundle;
	private static CustomYML price;

	public YamlDataStore(JavaPlugin plugin) {
		data = new CustomYML(plugin, "data.yml");
		world = new CustomYML(plugin, "world.yml");
		bundle = new CustomYML(plugin, "bundle.yml");
		def = new CustomYML(plugin, "default.yml");
		price = new CustomYML(plugin, "price.yml");

		price.saveDefaultConfig();
		bundle.saveDefaultConfig();
	}

	@Override
	public boolean create(JavaPlugin plugin) {
		// Don't change the version here, not needed
		setVersion("1.0.0");
		return true;
	}

	@Override
	public boolean exists(JavaPlugin plugin) {
		final File fileObject = 
				new File(plugin.getDataFolder()	+ "\\default.yml");
		return fileObject.exists();
	}

	@Override
	public int getBuild() {
		return Integer
				.valueOf(read("Default.Database.Version").split("//.")[2]);
	}

	@Override
	public int getVersionMajor() {
		return Integer
				.valueOf(read("Default.Database.Version").split("//.")[0]);
	}

	@Override
	public int getVersionMinor() {
		return Integer
				.valueOf(read("Default.Database.Version").split("//.")[1]);
	}

	private CustomYML getYml(String path) {
		final String[] pathList = path.split("\\.");

		if (pathList[0].equalsIgnoreCase("world")) {
			return world;
		} else if (pathList[0].equalsIgnoreCase("default")) {
			return def;
		} else if (pathList[0].equalsIgnoreCase("bundle")) {
			return bundle;
		} else if (pathList[0].equalsIgnoreCase("price")) {
			return price;
		} else {
			return data;
		}
	}

	@Override
	public boolean isSet(String path) {
		return (getYml(path).getConfig().getString(path) != null) ? true : false;
	}

	@Override
	public String read(String path) {
		return getYml(path).getConfig().getString(path);
	}

	@Override
	public double readDouble(String path) {
		return getYml(path).getConfig().getDouble(path);
	}

	@Override
	public int readInt(String path) {
		return getYml(path).getConfig().getInt(path);
	}

	@Override
	public Set<String> readKeys(String path) {
		return read(path) != null
				? getYml(path).getConfig().getConfigurationSection(path).getKeys(false)
				: new HashSet<String>();
	}

	@Override
	public Set<String> readSet(String path) {
		final List<?> setData = getYml(path).getConfig().getList(path);
		if (setData == null) {
			return null;
		}

		final Set<String> stringData = new HashSet<String>();
		for (final Object o : setData) {
			stringData.add((String) o);
		}
		return stringData;
	}

	@Override
	public boolean reload(JavaPlugin plugin) {
		data.reload();
		def.reload();
		world.reload();
		bundle.reload();
		price.reload();
		return true;
	}

	@Override
	public void setVersion(String version) {
		write("Default.Database.Version", version);
	}

	@Override
	public void update(JavaPlugin plugin) {
		// No update at this time.
	}

	@Override
	public void write(String path, double value) {
		final CustomYML cYml = getYml(path);
		cYml.getConfig().set(path, value);
		cYml.saveConfig();
	}

	@Override
	public void write(String path, List<String> list) {
		final CustomYML cYml = getYml(path);
		cYml.getConfig().set(path, list);
		cYml.saveConfig();
	}

	@Override
	public final Set<String> getBundles() {
		final String path = "Bundle";
		return read(path) != null
				? getYml(path).getConfig().getConfigurationSection(path).getKeys(false)
				: new HashSet<String>();
	}
	
	@Override
	public final Set<String> getBundle(String bundle) {
		final String path = "Bundle." + bundle;
		HashSet<String> flags = new HashSet<String>();
		if(read(path) == null) {
			return flags;
		}
		
		final List<?> list = getYml(path).getConfig().getList(path);
		for(Object o : list) {
			flags.add((String)o);
		}
		return flags;
	}
	
	@Override
	public final void setBundle(String name, Set<String> flags) {
		if (flags == null || flags.size() == 0) {
			removeBundle(name);
			return;
		}
		
		final List<String> list = new ArrayList<String>();
		for (final String s : flags) {
			list.add(s);
		}
		
		write("Bundle." + name, list);
	}
	
	@Override
	public void removeBundle(String name) {
		write("Bundle." + name, (String)null);
	}
	
	@Override
	public void write(String path, Set<String> set) {
		final List<String> list = new ArrayList<String>();
		for (final String s : set) {
			list.add(s);
		}
		write(path, list);
	}

	@Override
	public void write(String path, String value) {
		final CustomYML cYml = getYml(path);
		cYml.getConfig().set(path, value);
		cYml.saveConfig();
	}
}
