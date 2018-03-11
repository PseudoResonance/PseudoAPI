package io.github.pseudoresonance.pseudoapi.bukkit;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import io.github.pseudoresonance.pseudoapi.bukkit.Config;
import io.github.pseudoresonance.pseudoapi.bukkit.Message;
import io.github.pseudoresonance.pseudoapi.bukkit.data.Backend;
import io.github.pseudoresonance.pseudoapi.bukkit.data.Data;
import io.github.pseudoresonance.pseudoapi.bukkit.data.FileBackend;
import io.github.pseudoresonance.pseudoapi.bukkit.data.MysqlBackend;
import io.github.pseudoresonance.pseudoapi.bukkit.data.SqliteBackend;
import net.md_5.bungee.api.ChatColor;

public class DataController {
	
	private static DualHashBidiMap<String, String> uuids = new DualHashBidiMap<String, String>();
	private static Backend backend;
	
	private static File folder;
	
	private static File playerFolder;
	
	private static Connection con;
	private static Statement s;
	private static String prefix;
	
	private static boolean error = false;
	
	public static void updateBackend() {
		try {
			s.close();
			con.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (NullPointerException ex) {}
		backend = Data.getBackend();
		if (backend instanceof FileBackend) {
			FileBackend fb = (FileBackend) backend;
			folder = fb.getFolder();
			folder.mkdir();
			playerFolder = new File(folder, "Players");
			playerFolder.mkdir();
		} else if (backend instanceof MysqlBackend) {
			MysqlBackend mb = (MysqlBackend) backend;
			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection("jdbc:mysql://" + mb.getHost() + ":" + mb.getPort() + "/" + mb.getDatabase(), mb.getUsername(), mb.getPassword());
				s = con.createStatement();
				prefix = mb.getPrefix();
				s.executeUpdate("CREATE TABLE IF NOT EXISTS `" + prefix + "Players` (uuid VARCHAR(100), name VARCHAR(100));");
			} catch (SQLException | ClassNotFoundException e) {
				e.printStackTrace();
				error = true;
			}
		} else if (backend instanceof SqliteBackend) {
			SqliteBackend sb = (SqliteBackend) backend;
			try {
				Class.forName("org.sqlite.JDBC");
				con = DriverManager.getConnection("jdbc:sqlite:" + sb.getLocation().toString() + ".db");
				s = con.createStatement();
				prefix = sb.getPrefix();
				s.executeUpdate("CREATE TABLE IF NOT EXISTS `" + prefix + "Players` (uuid VARCHAR(100), name VARCHAR(100));");
			} catch (SQLException | ClassNotFoundException e) {
				e.printStackTrace();
				error = true;
			}
		}
		loadUUIDS();
	}
	
	public static boolean migrateUUIDS(Backend old, Backend b) {
		boolean success = true;
		DualHashBidiMap<String, String> uuids = getUUIDS(old);
		success = wipe(b);
		success = addUUIDS(uuids, b);
		return success;
	}
	
	public static void loadUUIDS() {
		uuids = new DualHashBidiMap<String, String>();
		if (backend instanceof FileBackend) {
			File[] files = playerFolder.listFiles();
			for (File f : files) {
				String fName = f.getName();
				if (fName.endsWith(".yml")) {
					Config c = new Config(playerFolder, f.getName(), PseudoAPI.plugin);
					String name = c.getConfig().getString("name");
					String uuid = fName.substring(0, fName.length() - 4);
					uuids.put(uuid, name);
				}
			}
		} else if (backend instanceof MysqlBackend || backend instanceof SqliteBackend) {
			try {
				ResultSet set = s.executeQuery("SELECT * FROM `" + prefix + "UUIDS`");
				while (set.next()) {
					String uuid = set.getString("uuid");
					String name = set.getString("name");
					uuids.put(uuid, name);
				}
				set.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static boolean addUUID(String uuid, String name) {
		if (!uuids.containsKey(uuid)) {
			uuids.put(uuid, name);
			if (!error) {
				if (backend instanceof FileBackend) {
					Config c = new Config(playerFolder, uuid, PseudoAPI.plugin);
					c.getConfig().set("name", name);
					c.save();
					return true;
				} else if (backend instanceof MysqlBackend || backend instanceof SqliteBackend) {
					try {
						s.executeUpdate("INSERT INTO `" + prefix + "UUIDS` VALUES (" + uuid + ", " + name + "');");
						return true;
					} catch (SQLException e) {
						e.printStackTrace();
						return false;
					}
				}
			} else {
				Message.sendConsoleMessage(ChatColor.RED + "Backend configuration error! Cannot save data!");
			}
			return false;
		} else {
			uuids.put(uuid, name);
			if (!error) {
				if (backend instanceof FileBackend) {
					Config c = new Config(playerFolder, uuid, PseudoAPI.plugin);
					c.getConfig().set("name", name);
					c.save();
					return true;
				} else if (backend instanceof MysqlBackend || backend instanceof SqliteBackend) {
					try {
						s.executeUpdate("UPDATE `" + prefix + "UUIDS` SET name=" + name + " WHERE uuid=" + uuid + ");");
						return true;
					} catch (SQLException e) {
						e.printStackTrace();
						return false;
					}
				}
			} else {
				Message.sendConsoleMessage(ChatColor.RED + "Backend configuration error! Cannot save data!");
			}
			return false;
		}
	}
	
	public static boolean removeUUID(String uuid) {
		if (uuids.containsKey(uuid)) {
			if (!error) {
				if (backend instanceof FileBackend) {
					File f = new File(playerFolder, uuid + ".yml");
					f.delete();
					return true;
				} else if (backend instanceof MysqlBackend || backend instanceof SqliteBackend) {
					try {
						s.executeUpdate("DELETE FROM `" + prefix + "UUIDS` WHERE uuid=" + uuid + "';");
						return true;
					} catch (SQLException e) {
						e.printStackTrace();
						return false;
					}
				}
				uuids.remove(uuid);
			} else {
				Message.sendConsoleMessage(ChatColor.RED + "Backend configuration error! Cannot save data!");
			}
			return false;
		}
		return false;
	}
	
	public static String getName(String uuid) {
		for (String u : uuids.keySet()) {
			if (u.equalsIgnoreCase(uuid)) {
				return uuids.get(u);
			}
		}
		return null;
	}
	
	public static String getUUID(String name) {
		for (String n : uuids.values()) {
			if (n.equalsIgnoreCase(name)) {
				return uuids.getKey(n);
			}
		}
		return null;
	}
	
	public static DualHashBidiMap<String, String> getUUIDS(Backend b) {
		DualHashBidiMap<String, String> uuids = new DualHashBidiMap<String, String>();
		if (b instanceof FileBackend) {
			FileBackend fb = (FileBackend) b;
			File playerFolder = new File(fb.getFolder(), "Players");
			File[] files = playerFolder.listFiles();
			for (File f : files) {
				String fName = f.getName();
				if (fName.endsWith(".yml")) {
					Config c = new Config(playerFolder, f.getName(), PseudoAPI.plugin);
					String name = c.getConfig().getString("name");
					String uuid = fName.substring(0, fName.length() - 4);
					uuids.put(uuid, name);
				}
			}
		} else if (b instanceof MysqlBackend) {
			MysqlBackend mb = (MysqlBackend) b;
			try {
				Class.forName("com.mysql.jdbc.Driver");
				Connection con = DriverManager.getConnection("jdbc:mysql://" + mb.getHost() + ":" + mb.getPort() + "/" + mb.getDatabase(), mb.getUsername(), mb.getPassword());
				Statement s = con.createStatement();
				ResultSet set = s.executeQuery("SELECT * FROM `" + mb.getPrefix() + "UUIDS`");
				while (set.next()) {
					String uuid = set.getString("uuid");
					String name = set.getString("name");
					uuids.put(uuid, name);
				}
				s.close();
				con.close();
				set.close();
			} catch (SQLException | ClassNotFoundException e) {
				e.printStackTrace();
				error = true;
			}
		} else if (b instanceof SqliteBackend) {
			SqliteBackend sb = (SqliteBackend) b;
			try {
				Class.forName("org.sqlite.JDBC");
				Connection con = DriverManager.getConnection("jdbc:sqlite:" + sb.getLocation().toString() + ".db");
				Statement s = con.createStatement();
				ResultSet set = s.executeQuery("SELECT * FROM `" + sb.getPrefix() + "UUIDS`");
				while (set.next()) {
					String uuid = set.getString("uuid");
					String name = set.getString("name");
					uuids.put(uuid, name);
				}
				s.close();
				con.close();
				set.close();
			} catch (SQLException | ClassNotFoundException e) {
				e.printStackTrace();
				error = true;
			}
		}
		return uuids;
	}
	
	public static boolean addUUIDS(DualHashBidiMap<String, String> uuids, Backend b) {
		boolean error = false;
		if (b instanceof FileBackend) {
			FileBackend fb = (FileBackend) b;
			File playerFolder = new File(fb.getFolder(), "Players");
			for (String uuid : uuids.keySet()) {
				Config c = new Config(playerFolder, uuid, PseudoAPI.plugin);
				c.getConfig().set("name", uuids.get(uuid));
				c.save();
			}
		} else if (b instanceof MysqlBackend) {
			MysqlBackend mb = (MysqlBackend) b;
			try {
				Class.forName("com.mysql.jdbc.Driver");
				Connection con = DriverManager.getConnection("jdbc:mysql://" + mb.getHost() + ":" + mb.getPort() + "/" + mb.getDatabase(), mb.getUsername(), mb.getPassword());
				Statement s = con.createStatement();
				s.executeUpdate("CREATE TABLE IF NOT EXISTS `" + mb.getPrefix() + "UUIDS` (uuid VARCHAR(100), name VARCHAR(100));");
				for (String uuid : uuids.keySet()) {
					s.executeUpdate("INSERT INTO `" + mb.getPrefix() + "UUIDS` VALUES (" + uuid + ", " + uuids.get(uuid) + "');");
				}
				s.close();
				con.close();
			} catch (SQLException | ClassNotFoundException e) {
				e.printStackTrace();
				error = true;
			}
		} else if (b instanceof SqliteBackend) {
			SqliteBackend sb = (SqliteBackend) b;
			try {
				Class.forName("org.sqlite.JDBC");
				Connection con = DriverManager.getConnection("jdbc:sqlite:" + sb.getLocation().toString() + ".db");
				Statement s = con.createStatement();
				s.executeUpdate("CREATE TABLE IF NOT EXISTS `" + sb.getPrefix() + "UUIDS` (uuid VARCHAR(100), name VARCHAR(100));");
				for (String uuid : uuids.keySet()) {
					s.executeUpdate("INSERT INTO `" + sb.getPrefix() + "UUIDS` VALUES (" + uuid + ", " + uuids.get(uuid) + "');");
				}
				s.close();
				con.close();
			} catch (SQLException | ClassNotFoundException e) {
				e.printStackTrace();
				error = true;
			}
		}
		if (error) {
			Message.sendConsoleMessage(ChatColor.RED + "Backend configuration error! Cannot complete request!");
			return false;
		} else {
			return true;
		}
	}
	
	public static boolean wipe(Backend b) {
		boolean error = false;
		if (b instanceof FileBackend) {
			FileBackend fb = (FileBackend) b;
			File playerFolder = new File(fb.getFolder(), "Players");
			for (File file : playerFolder.listFiles())
				if (file.getName().endsWith(".yml"))
					file.delete();
		} else if (b instanceof MysqlBackend) {
			MysqlBackend mb = (MysqlBackend) b;
			try {
				Class.forName("com.mysql.jdbc.Driver");
				Connection con = DriverManager.getConnection("jdbc:mysql://" + mb.getHost() + ":" + mb.getPort() + "/" + mb.getDatabase(), mb.getUsername(), mb.getPassword());
				Statement s = con.createStatement();
				s.executeUpdate("CREATE TABLE IF NOT EXISTS `" + mb.getPrefix() + "UUIDS` (uuid VARCHAR(100), name VARCHAR(100));");
				s.executeUpdate("TRUNCATE `" + mb.getPrefix() + "UUIDS`;");
				s.close();
				con.close();
			} catch (SQLException | ClassNotFoundException e) {
				e.printStackTrace();
				error = true;
			}
		} else if (b instanceof SqliteBackend) {
			SqliteBackend sb = (SqliteBackend) b;
			try {
				Class.forName("org.sqlite.JDBC");
				Connection con = DriverManager.getConnection("jdbc:sqlite:" + sb.getLocation().toString() + ".db");
				Statement s = con.createStatement();
				s.executeUpdate("CREATE TABLE IF NOT EXISTS `" + sb.getPrefix() + "UUIDS` (uuid VARCHAR(100), name VARCHAR(100));");
				s.executeUpdate("TRUNCATE `" + sb.getPrefix() + "UUIDS`;");
				s.close();
				con.close();
			} catch (SQLException | ClassNotFoundException e) {
				e.printStackTrace();
				error = true;
			}
		}
		if (error) {
			Message.sendConsoleMessage(ChatColor.RED + "Backend configuration error! Cannot complete request!");
			return false;
		} else {
			return true;
		}
	}

}
