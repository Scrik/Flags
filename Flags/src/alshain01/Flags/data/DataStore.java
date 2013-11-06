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

import java.util.List;
import java.util.Set;

import org.bukkit.plugin.java.JavaPlugin;

public interface DataStore {
	
	// The old world order (to be phased out)
	public boolean isSet(String path);

	public String read(String path);

	public double readDouble(String path);

	public int readInt(String path);

	public Set<String> readKeys(String path);

	public Set<String> readSet(String path);

	public void write(String path, double value);

	public void write(String path, List<String> list);

	public void write(String path, Set<String> set);

	public void write(String path, String value);
	
	// The new world order (transitioned to SQL capability)
	
	public boolean create(JavaPlugin plugin);

	public boolean exists(JavaPlugin plugin);
	
	public boolean reload(JavaPlugin plugin);
	
	public void update(JavaPlugin plugin);
	
	public void setVersion(String version);

	public int getBuild();

	public int getVersionMajor();

	public int getVersionMinor();
	
	public Set<String> getBundles();
	
	public Set<String> getBundle(String bundle);

	public void setBundle(String name, Set<String> flags);
	
	public void removeBundle(String name);
}
