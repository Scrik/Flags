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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Set;

import me.ryanhamshire.GriefPrevention.GriefPrevention;

import org.bukkit.plugin.java.JavaPlugin;

public final class MySQLDataStore implements SQLDataStore {
	Connection connection = null;
	String url = null;
	String user = null;
	String password = null;
	
	private boolean initialize(JavaPlugin plugin) {
		// Connect to the database.
		url = plugin.getConfig().getString(plugin.getName() + ".Database.Url");
		user = plugin.getConfig().getString(plugin.getName() + ".Database.Username");
		password = plugin.getConfig().getString(plugin.getName() + ".Database.Password");
		
		try {
			connection = DriverManager.getConnection(url, user, password);
			return true;
		} catch (SQLException e) {
			SqlError(e.getMessage());
			return false;
		}
	}	
	
	private void SqlError(String error) {
		GriefPrevention.instance.getLogger().warning("SQL DataStore Error: " + error);
	}
	
	private void executeStatement(String statement) {
		try {
			Statement SQL = connection.createStatement();
			SQL.execute(statement);
		} catch (SQLException e) {
			SqlError(e.getMessage());
		}
	}
	
	private ResultSet executeQuery(String query) {
		try {
			Statement SQL = connection.createStatement();
			return SQL.executeQuery(query);
		} catch (SQLException e) {
			SqlError(e.getMessage());
			return null;
		}
	}
	
	public MySQLDataStore(JavaPlugin plugin) {
		initialize(plugin);
	}
	
	@Override
	public void close() {
		try {
			connection.close();
		} catch (SQLException e) { 
			SqlError(e.getMessage());
		}
	}
	
	@Override
	public boolean reload(JavaPlugin plugin) {
		// Close the connection and reconnect.
		try {
			if(!(this.connection == null) && !this.connection.isClosed()) {
				connection.close();
			}
		} catch (SQLException e) {
			SqlError(e.getMessage());
			return false;
		}

		return initialize(plugin);
	}
	
	@Override
	public boolean isConnected() {
		try {
			return !connection.isClosed();
		} catch (SQLException e) {
			SqlError(e.getMessage());
			return false;
		}
	}
	
	@Override
	public boolean create(JavaPlugin plugin) {
			executeStatement("CREATE TABLE version (major INT, minor INT, build INT);");
			executeStatement("INSERT INTO version (major,minor,build) VALUES(0,0,0);");
			executeStatement("CREATE TABLE IF NOT EXISTS world (world VARCHAR(50), flag VARCHAR(25), default BOOL, world BOOL);");
			executeStatement("CREATE TABLE IF NOT EXISTS data (id BIGINT, subid BIGINT, flag VARCHAR(25), value BOOL);");
			executeStatement("CREATE TABLE IF NOT EXISTS flag (flag VARCHAR(25), name VARCHAR(25), description VARCHAR(255), message VARCHAR(255), worldmessage VARCHAR(255));");
			executeStatement("CREATE TABLE IF NOT EXISTS bundle (name VARCHAR(25), flag VARCHAR(25));");
			executeStatement("CREATE TABLE IF NOT EXISTS messages (name VARCHAR(25), message VARCHAR(255));");
			return true;
	}
	
	@Override
	public boolean exists(JavaPlugin plugin) {
		ResultSet results = executeQuery("SHOW TABLES LIKE 'version';");
	
		try {
			return results.next();
		} catch (SQLException e) {
			SqlError(e.getMessage());
			return false;
		}
	}
	
	@Override
	public int getVersionMajor() {
		ResultSet results = executeQuery("SELECT * FROM version;");
		try {
			results.next();
			return results.getInt("major");
		} catch (SQLException e) {
			SqlError(e.getMessage());
		}
		return 0;
	}

	@Override
	public int getVersionMinor() {
		ResultSet results = executeQuery("SELECT * FROM version;");
		try {
			results.next();
			return results.getInt("minor");
		} catch (SQLException e) {
			SqlError(e.getMessage());
		}
		return 0;
	}

	@Override
	public int getBuild() {
		ResultSet results = executeQuery("SELECT * FROM version;");
		try {
			results.next();
			return results.getInt("build");
		} catch (SQLException e) {
			SqlError(e.getMessage());
		}
		return 0;
	}

	@Override
	public void setVersion(String version) {
		String[] ver = version.split("//.");
		executeQuery("UPDATE version SET major=" + ver[0] + ", minor=" + ver[1] + ", build=" + ver[2] + ";");
	}
	
	@Override
	public void update(JavaPlugin plugin) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void write(String path, String value) {
		// TODO Auto-generated method stub
	}

	@Override
	public void write(String path, List<String> list) {
		// TODO Auto-generated method stub

	}

	@Override
	public String read(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int readInt(String path) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<String> readList(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> readKeys(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> readSet(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void write(String path, Set<String> set) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double readDouble(String path) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isSet(String path) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void write(String path, double value) {
		// TODO Auto-generated method stub
		
	}
}
