package io.github.wolfleader116.wolfapi.bukkit;

import java.util.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import io.github.wolfleader116.wolfapi.bukkit.Config;
import io.github.wolfleader116.wolfapi.bukkit.Message;
import io.github.wolfleader116.wolfapi.bukkit.data.Backend;
import io.github.wolfleader116.wolfapi.bukkit.data.Data;
import io.github.wolfleader116.wolfapi.bukkit.data.FileBackend;
import io.github.wolfleader116.wolfapi.bukkit.data.MysqlBackend;
import io.github.wolfleader116.wolfapi.bukkit.data.SqliteBackend;
import net.md_5.bungee.api.ChatColor;

public class DataController {
	
	private static DualHashBidiMap<String, String> uuids = new DualHashBidiMap<String, String>();
	private static Backend backend;
	
	private static Config c;
	
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
			c = new Config(fb.getFolder(), fb.getFile(), WolfAPI.plugin);
		} else if (backend instanceof MysqlBackend) {
			MysqlBackend mb = (MysqlBackend) backend;
			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection("jdbc:mysql://" + mb.getHost() + ":" + mb.getPort() + "/" + mb.getDatabase(), mb.getUsername(), mb.getPassword());
				s = con.createStatement();
				prefix = mb.getPrefix();
				s.executeUpdate("CREATE TABLE IF NOT EXISTS `" + prefix + "Anvils` (x int, y int, z int, world VARCHAR(100));");
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
				s.executeUpdate("CREATE TABLE IF NOT EXISTS `" + prefix + "Anvils` (x int, y int, z int, world VARCHAR(100));");
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
			List<String> list = c.getConfig().getStringList("WolfAPI.UUIDS");
			for (String s : list) {
				String[] parts = s.split(",");
				uuids.put(parts[0], parts[1]);
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
					List<String> list = c.getConfig().getStringList("WolfAPI.UUIDS");
					list.add(uuid + "," + name);
					c.set("WolfAPI.UUIDS", list);
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
			String oldName = uuids.get(uuid);
			uuids.put(uuid, name);
			if (!error) {
				if (backend instanceof FileBackend) {
					List<String> list = c.getConfig().getStringList("WolfAPI.UUIDS");
					list.remove(uuid + "," + oldName);
					list.add(uuid + "," + name);
					c.set("WolfAPI.UUIDS", list);
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
			String name = uuids.get(uuid);
			if (!error) {
				if (backend instanceof FileBackend) {
					List<String> list = c.getConfig().getStringList("WolfAPI.UUIDS");
					list.remove(uuid + "," + name);
					c.set("WolfAPI.UUIDS", list);
					c.save();
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
		return uuids.get(uuid);
	}
	
	public static String getUUID(String name) {
		return uuids.getKey(name);
	}
	
	public static DualHashBidiMap<String, String> getUUIDS(Backend b) {
		DualHashBidiMap<String, String> uuids = new DualHashBidiMap<String, String>();
		if (b instanceof FileBackend) {
			FileBackend fb = (FileBackend) b;
			Config config = new Config(fb.getFolder(), fb.getFile(), WolfAPI.plugin);
			List<String> list = config.getConfig().getStringList("WolfAPI.UUIDS");
			for (String s : list) {
				String[] parts = s.split(",");
				uuids.put(parts[0], parts[1]);
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
			Config config = new Config(fb.getFolder(), fb.getFile(), WolfAPI.plugin);
			List<String> list = config.getConfig().getStringList("WolfAPI.UUIDS");
			for (String uuid : uuids.keySet()) {
				list.add(uuid + "," + uuids.get(uuid));
			}
			config.set("WolfAPI.UUIDS", list);
			config.save();
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
			Config config = new Config(fb.getFolder(), fb.getFile(), WolfAPI.plugin);
			config.set("WolfAPI.UUIDS", null);
			config.save();
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
