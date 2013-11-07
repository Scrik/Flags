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

import me.ryanhamshire.GriefPrevention.GriefPrevention;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.bekvon.bukkit.residence.Residence;

import alshain01.Flags.LandSystem;
import alshain01.Flags.economy.EPurchaseType;

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
		// Don't change the version here, not needed (will change in update)
		if(!exists(plugin)) {
			writeVersion(new DBVersion(1,0,0));
		}
		return true;
	}

	public boolean exists(JavaPlugin plugin) {
		final File fileObject = 
				new File(plugin.getDataFolder()	+ "\\default.yml");
		return fileObject.exists();
	}

	@Override
	public DBVersion readVersion() {
		if(!isSet("Default.Database.Version")) { return new DBVersion(0,0,0); }
		String[] ver = read("Default.Database.Version").split("\\.");
		return new DBVersion(Integer.valueOf(ver[0]), Integer.valueOf(ver[1]), Integer.valueOf(ver[2]));
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

	public void writeVersion(DBVersion version) {
		write("Default.Database.Version", version.major + "." + version.minor + "." + version.build);
	}

	@Override
	public void update(JavaPlugin plugin) {
		DBVersion ver = readVersion();
		if(ver.major <= 1 && ver.minor <= 2 && ver.build < 2) {
			CustomYML cYml = getYml("data");
			ConfigurationSection cSec = null;
			if(LandSystem.getActive() == LandSystem.GRIEF_PREVENTION) {
				cSec = cYml.getConfig().getConfigurationSection("GriefPreventionData");
			} else if(LandSystem.getActive() == LandSystem.RESIDENCE) {
				cSec = cYml.getConfig().getConfigurationSection("ResidenceData");
			}
			if(cSec != null) {
				Set<String> keys = cSec.getKeys(true);
				for(String k : keys) {
					
					if(k.contains("Value") || k.contains("Message") 
							|| k.contains("Trust") || k.contains("Inherit")) {
						
						String id = k.split("\\.")[0];
						String world;
						
						if(LandSystem.getActive() == LandSystem.GRIEF_PREVENTION) {
							world = GriefPrevention.instance.dataStore.getClaim(Long.valueOf(id)).getGreaterBoundaryCorner().getWorld().getName();
						} else {
							world = Residence.getResidenceManager().getByName(id).getWorld();
						}
						
						cSec.set(world + "." + k, cSec.get(k));
					}
				}
				// Remove the old
				for(String k : keys) {
					if(k.split("\\.").length == 1 && Bukkit.getWorld(k.split("\\.")[0]) == null) {
						cSec.set(k, null);
					}
				}

			}
			
			Set<String> keys = cYml.getConfig().getKeys(true);
			for(String k : keys) {
				if(k.contains("Value")) {
					cYml.getConfig().set(k, Boolean.valueOf(cYml.getConfig().getString(k)));
				}
			}
			cYml.saveConfig();
			
			cYml = getYml("default");
			keys = cYml.getConfig().getKeys(true);
			for(String k : keys) {
				if(k.contains("Value")) {
					cYml.getConfig().set(k, Boolean.valueOf(cYml.getConfig().getString(k)));
				}
			}
			cYml.saveConfig();
			
			cYml = getYml("world");
			keys = cYml.getConfig().getKeys(true);
			for(String k : keys) {
				if(k.contains("Value")) {
					cYml.getConfig().set(k, Boolean.valueOf(cYml.getConfig().getString(k)));
				}
			}
			cYml.saveConfig();
			
			writeVersion(new DBVersion(1,2,2));
		}
		
	}
	
	@Override
	public boolean readBoolean(String path) {
		return getYml(path).getConfig().getBoolean(path);
	}
	
	@Override
	public double readPrice(String flag, EPurchaseType type) {
		final String path = "Price." + type.toString() + "." + flag;
		final FileConfiguration cYml = getYml(path).getConfig();
		return cYml.getString(path) != null ? cYml.getDouble(path) : 0;
	}
	
	@Override
	public void writePrice(String flag, EPurchaseType type, double price) {
		final String path = "Price." + type.toString() + "." + flag;
		getYml(path).getConfig().set(path, price);
	}
	
	@Override
	public void write(String path, Boolean value) {
		final CustomYML cYml = getYml(path);
		cYml.getConfig().set(path, value);
		cYml.saveConfig();
	}

	@Override
	public final Set<String> readBundles() {
		final String path = "Bundle";
		return read(path) != null
				? getYml(path).getConfig().getConfigurationSection(path).getKeys(false)
				: new HashSet<String>();
	}
	
	@Override
	public final Set<String> readBundle(String bundle) {
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
	public final void writeBundle(String name, Set<String> flags) {
		final String path = "Bundle." + name;
		final CustomYML cYml = getYml(path);
		if (flags == null || flags.size() == 0) {
			deleteBundle(name);
			return;
		}
		
		final List<String> list = new ArrayList<String>();
		for (final String s : flags) {
			list.add(s);
		}
		
		cYml.getConfig().set(path, list);
		cYml.saveConfig();
	}
	
	@Override
	public void deleteBundle(String name) {
		write("Bundle." + name, (String)null);
	}
	
	@Override
	public void write(String path, Set<String> set) {
		final CustomYML cYml = getYml(path);

		final List<String> list = new ArrayList<String>();
		for (final String s : set) {
			list.add(s);
		}
		
		cYml.getConfig().set(path, list);
		cYml.saveConfig();
	}

	@Override
	public void write(String path, String value) {
		final CustomYML cYml = getYml(path);
		cYml.getConfig().set(path, value);
		cYml.saveConfig();
	}
}
