package io.github.pseudoresonance.pseudoapi.bukkit.playerdata;

import java.io.File;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.apache.commons.dbcp2.BasicDataSource;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import io.github.pseudoresonance.pseudoapi.bukkit.Config;
import io.github.pseudoresonance.pseudoapi.bukkit.ConfigOptions;
import io.github.pseudoresonance.pseudoapi.bukkit.Message;
import io.github.pseudoresonance.pseudoapi.bukkit.Message.Errors;
import io.github.pseudoresonance.pseudoapi.bukkit.PseudoAPI;
import io.github.pseudoresonance.pseudoapi.bukkit.data.Backend;
import io.github.pseudoresonance.pseudoapi.bukkit.data.Data;
import io.github.pseudoresonance.pseudoapi.bukkit.data.FileBackend;
import io.github.pseudoresonance.pseudoapi.bukkit.data.MySQLBackend;
import io.github.pseudoresonance.pseudoapi.bukkit.data.SQLBackend;
import io.github.pseudoresonance.pseudoapi.bukkit.listeners.PlayerJoinLeaveL;
import net.md_5.bungee.api.ChatColor;

public class PlayerDataController {

	private static int uniquePlayers = 0;

	private static ArrayList<Column> dataTypes = new ArrayList<Column>();
	private static ArrayList<Column> serverDataTypes = new ArrayList<Column>();

	private static HashMap<String, HashMap<String, Object>> playerData = new HashMap<String, HashMap<String, Object>>();

	private static DualHashBidiMap<String, String> uuids = new DualHashBidiMap<String, String>();

	private static Backend b;

	public static void update() {
		b = Data.getBackend();
		setup();
		playerData.clear();
		getUUIDS();
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			PlayerJoinLeaveL.playerJoin(p);
		}
	}

	public static boolean addColumn(Column col) {
		for (Column column : dataTypes)
			if (column.getName().equalsIgnoreCase(col.getName()))
				return false;
		dataTypes.add(col);
		if (b instanceof SQLBackend) {
			SQLBackend sb = (SQLBackend) b;
			BasicDataSource data = sb.getDataSource();
			try (Connection c = data.getConnection()) {
				for (Column column : serverDataTypes) {
					if (column.getName().equalsIgnoreCase(col.getName())) {
						try (Statement st = c.createStatement()) {
							String key = col.getName();
							String value = col.getType();
							String defaultValue = col.getDefaultValue();
							try {
								st.execute("ALTER TABLE `" + sb.getPrefix() + "Players` MODIFY " + key + " " + value + " DEFAULT " + defaultValue + ";");
								return true;
							} catch (SQLException e) {
								int error = e.getErrorCode();
								if (b instanceof MySQLBackend) {
									if (error == 1406 || error == 1264 || error == 1265 || error == 1366 || error == 1292) {
										try (Statement st2 = c.createStatement()) {
											String suffix = "_OLD";
											try {
												st2.execute("ALTER TABLE `" + sb.getPrefix() + "Players` CHANGE " + key + " " + key + suffix + " " + value + ";");
												return true;
											} catch (SQLException ex) {
												PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "Error when renaming column: " + key + " to: " + key + suffix + " in table: " + sb.getPrefix() + "Players in database: " + sb.getName());
												PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "SQLError " + ex.getErrorCode() + ": (State: " + ex.getSQLState() + ") - " + ex.getMessage());
												break;
											}
										} catch (SQLException ex) {
											PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "Error when creating statement in database: " + sb.getName());
											PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "SQLError " + ex.getErrorCode() + ": (State: " + ex.getSQLState() + ") - " + ex.getMessage());
										}
									}
								}
								PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "Error when converting column: " + key + " to type: " + value + " with default value: " + defaultValue + " in table: " + sb.getPrefix() + "Players in database: " + sb.getName());
								PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
							}
						} catch (SQLException e) {
							PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "Error when creating statement in database: " + sb.getName());
							PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
						}
					}
				}
				try (Statement st = c.createStatement()) {
					String key = col.getName();
					String value = col.getType();
					String defaultValue = col.getDefaultValue();
					try {
						st.execute("ALTER TABLE `" + sb.getPrefix() + "Players` ADD " + key + " " + value + " DEFAULT " + defaultValue + ";");
						return true;
					} catch (SQLException e) {
						PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "Error when adding column: " + key + " of type: " + value + " with default value: " + defaultValue + " in table: " + sb.getPrefix() + "Players in database: " + sb.getName());
						PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
					}
				} catch (SQLException e) {
					PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "Error when creating statement in database: " + sb.getName());
					PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
				}
			} catch (SQLException e) {
				PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "Error while accessing database: " + sb.getName());
				PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
			}
		}
		return false;
	}

	public static void setup() {
		if (b instanceof FileBackend) {
			FileBackend fb = (FileBackend) b;
			File dir = fb.getFolder();
			if (!dir.isDirectory()) {
				dir.mkdir();
			}
			File folder = new File(fb.getFolder(), "Players");
			try {
				if (folder.isDirectory()) {
					File[] files = folder.listFiles();
					for (File f : files) {
						if (f.getName().endsWith(".yml")) {
							uniquePlayers++;
						}
					}
				} else
					folder.mkdir();
			} catch (SecurityException e) {
				PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "No permission to access: " + folder.getAbsolutePath());
			}
		} else if (b instanceof SQLBackend) {
			SQLBackend sb = (SQLBackend) b;
			BasicDataSource data = sb.getDataSource();
			try (Connection c = data.getConnection()) {
				try (Statement s = c.createStatement()) {
					ArrayList<Column> newColumns = new ArrayList<Column>();
					ArrayList<Column> editColumns = new ArrayList<Column>();
					ArrayList<Column> oldColumns = new ArrayList<Column>();
					ArrayList<Column> columns = new ArrayList<Column>();
					boolean uuidCol = false;
					boolean usernameCol = false;
					boolean create = false;
					try (ResultSet rs = s.executeQuery("DESCRIBE `" + sb.getPrefix() + "Players`;")) {
						while (rs.next()) {
							String field = rs.getString("Field");
							String type = rs.getString("Type");
							String defaultValue = String.valueOf(rs.getObject("Default"));
							if (field.equalsIgnoreCase("uuid"))
								uuidCol = true;
							else if (field.equalsIgnoreCase("username"))
								usernameCol = true;
							else
								columns.add(new Column(field, type, defaultValue));
						}
					} catch (SQLException e) {
						if (e.getErrorCode() == 1146) {
							create = true;
						} else {
							PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "Error when getting table description in table: " + sb.getPrefix() + "Players in database: " + sb.getName());
							PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
						}
					}
					serverDataTypes = columns;
					if (create) {
						try (Statement st = c.createStatement()) {
							try {
								st.execute("CREATE TABLE IF NOT EXISTS `" + sb.getPrefix() + "Players` (`uuid` VARCHAR(36) PRIMARY KEY, `username` VARCHAR(16), `firstjoin` TIMESTAMP DEFAULT CURRENT_TIMESTAMP, `lastjoinleave` TIMESTAMP DEFAULT CURRENT_TIMESTAMP, `playtime` BIGINT(20) UNSIGNED DEFAULT 0);");
							} catch (SQLException e) {
								PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "Error when creating table: " + sb.getPrefix() + "Players in database: " + sb.getName());
								PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
							}
						} catch (SQLException e) {
							PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "Error when creating statement in database: " + sb.getName());
							PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
						}
						try (Statement st = c.createStatement()) {
							for (Column col : dataTypes) {
								String key = col.getName();
								String value = col.getType();
								String defaultValue = col.getDefaultValue();
								try {
									st.execute("ALTER TABLE `" + sb.getPrefix() + "Players` ADD " + key + " " + value + " DEFAULT " + defaultValue + ";");
								} catch (SQLException e) {
									PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "Error when adding column: " + key + " of type: " + value + " with default value: " + defaultValue + " in table: " + sb.getPrefix() + "Players in database: " + sb.getName());
									PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
								}
							}
						} catch (SQLException e) {
							PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "Error when creating statement in database: " + sb.getName());
							PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
						}
					} else {
						if (uuidCol && usernameCol) {
							for (Column col : dataTypes) {
								boolean found = false;
								for (Column testCol : columns) {
									if (testCol.equals(col)) {
										found = true;
										break;
									} else if (testCol.getName().equalsIgnoreCase(col.getName())) {
										editColumns.add(col);
										found = true;
										break;
									}
								}
								if (!found)
									newColumns.add(col);
							}
							try (Statement st = c.createStatement()) {
								for (Column col : editColumns) {
									String key = col.getName();
									String value = col.getType();
									String defaultValue = col.getDefaultValue();
									try {
										st.execute("ALTER TABLE `" + sb.getPrefix() + "Players` MODIFY " + key + " " + value + " DEFAULT " + defaultValue + ";");
									} catch (SQLException e) {
										int error = e.getErrorCode();
										if (b instanceof MySQLBackend) {
											if (error == 1406 || error == 1264 || error == 1265 || error == 1366 || error == 1292) {
												oldColumns.add(col);
												newColumns.add(col);
											}
										}
										PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "Error when converting column: " + key + " to type: " + value + " with default value: " + defaultValue + " in table: " + sb.getPrefix() + "Players in database: " + sb.getName());
										PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
									}
								}
							} catch (SQLException e) {
								PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "Error when creating statement in database: " + sb.getName());
								PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
							}
							try (Statement st = c.createStatement()) {
								for (Column col : oldColumns) {
									String key = col.getName();
									String value = col.getType();
									String suffix = "_OLD";
									try {
										st.execute("ALTER TABLE `" + sb.getPrefix() + "Players` CHANGE " + key + " " + key + suffix + " " + value + ";");
									} catch (SQLException e) {
										PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "Error when renaming column: " + key + " to: " + key + suffix + " in table: " + sb.getPrefix() + "Players in database: " + sb.getName());
										PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
									}
								}
							} catch (SQLException e) {
								PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "Error when creating statement in database: " + sb.getName());
								PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
							}
							try (Statement st = c.createStatement()) {
								for (Column col : newColumns) {
									String key = col.getName();
									String value = col.getType();
									String defaultValue = col.getDefaultValue();
									try {
										st.execute("ALTER TABLE `" + sb.getPrefix() + "Players` ADD " + key + " " + value + " DEFAULT " + defaultValue + ";");
									} catch (SQLException e) {
										PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "Error when adding column: " + key + " of type: " + value + " with default value: " + defaultValue + " in table: " + sb.getPrefix() + "Players in database: " + sb.getName());
										PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
									}
								}
							} catch (SQLException e) {
								PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "Error when creating statement in database: " + sb.getName());
								PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
							}
						} else {
							Message.sendConsoleMessage(ChatColor.RED + "Selected backend contains incorrectly formatted table: " + sb.getPrefix() + "Players! Disabling PseudoAPI!");
							Bukkit.getServer().getPluginManager().disablePlugin(PseudoAPI.plugin);
							return;
						}
					}
					try (Statement st = c.createStatement()) {
						try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM `" + sb.getPrefix() + "Players`;")) {
							if (rs.next())
								uniquePlayers = rs.getInt("COUNT(*)");
							else
								uniquePlayers = 0;
						} catch (SQLException e) {
							PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "Error when counting rows in table: " + sb.getPrefix() + "Players in database: " + sb.getName());
							PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
						}
					} catch (SQLException e) {
						PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "Error when creating statement in database: " + sb.getName());
						PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
					}
				} catch (SQLException e) {
					PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "Error when creating statement in database: " + sb.getName());
					PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
				}
			} catch (SQLException e) {
				PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "Error while accessing database: " + sb.getName());
				PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
			}
		}
		getUUIDS();
	}

	public static void playerJoin(String uuid, String username) {
		if (!playerData.containsKey(uuid)) {
			playerData.put(uuid, getPlayer(uuid));
		}
		String name = getName(uuid);
		uuids.put(uuid, username);
		HashMap<String, Object> settings = new HashMap<String, Object>();
		settings.put("username", username);
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		settings.put("lastjoinleave", timestamp);
		if (name == null)
			settings.put("firstjoin", timestamp);
		setPlayerSettings(uuid, settings);
	}

	public static void playerLeave(String uuid, String username) {
		Object o = getPlayerSetting(uuid, "lastjoinleave");
		Timestamp joinLeaveTS = null;
		if (o instanceof Timestamp) {
			joinLeaveTS = (Timestamp) o;
		}
		if (o instanceof Date) {
			joinLeaveTS = new Timestamp(((Date) o).getTime());
		}
		if (joinLeaveTS != null) {
			long joinLeave = joinLeaveTS.getTime();
			long diff = System.currentTimeMillis() - joinLeave;
			Object ob = getPlayerSetting(uuid, "playtime");
			if (ob instanceof BigInteger || ob instanceof Long) {
				long playTime = 0;
				if (ob instanceof BigInteger)
					playTime = ((BigInteger) ob).longValueExact();
				else
					playTime = (Long) ob;
				playTime = diff >= 0 ? playTime + diff : playTime - diff;
				setPlayerSetting(uuid, "playtime", playTime);
			} else if (ob == null) {
				setPlayerSetting(uuid, "playtime", diff);
			}
		}
		playerJoin(uuid, username);
		setPlayerSettings(uuid, playerData.get(uuid));
		if (ConfigOptions.bungeeEnabled) {
			playerData.get(uuid).clear();
			playerData.remove(uuid);
		}
	}
	
	public static Set<String> getNames() {
		return uuids.values();
	}
	
	public static Set<String> getUUIDs() {
		return uuids.keySet();
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

	private static void getUUIDS() {
		if (b instanceof FileBackend) {
			FileBackend fb = (FileBackend) b;
			File folder = new File(fb.getFolder(), "Players");
			try {
				File[] files = folder.listFiles();
				if (files != null) {
					for (File f : files) {
						if (f.getName().endsWith(".yml")) {
							Config c = new Config(folder, f.getName(), PseudoAPI.plugin);
							FileConfiguration fc = c.getConfig();
							String name = fc.getString("username");
							uuids.put(f.getName().substring(0, f.getName().length() - 4), name);
						}
					}
				}
			} catch (SecurityException e) {
				PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "No permission to access: " + folder.getAbsolutePath());
			}
		} else if (b instanceof SQLBackend) {
			SQLBackend sb = (SQLBackend) b;
			BasicDataSource data = sb.getDataSource();
			try (Connection c = data.getConnection()) {
				try (Statement st = c.createStatement()) {
					try (ResultSet rs = st.executeQuery("SELECT uuid,username FROM `" + sb.getPrefix() + "Players` ORDER BY `lastjoinleave` ASC;")) {
						while (rs.next()) {
							uuids.put(rs.getString("uuid"), rs.getString("username"));
						}
					} catch (SQLException e) {
						PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "Error when getting player names and uuids from table: " + sb.getPrefix() + "Players in database: " + sb.getName());
						PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
					}
				} catch (SQLException e) {
					PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "Error when creating statement in database: " + sb.getName());
					PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
				}
			} catch (SQLException e) {
				PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "Error while accessing database: " + sb.getName());
				PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
			}
		}
	}

	public static void setPlayerSettings(String uuid, HashMap<String, Object> values) {
		HashMap<String, Object> original;
		HashMap<String, Object> changed = new HashMap<String, Object>();
		if (playerData.containsKey(uuid)) {
			original = playerData.get(uuid);
			if (original == null) {
				original = getPlayer(uuid);
			}
		} else {
			original = getPlayer(uuid);
		}
		if (original == null) {
			original = new HashMap<String, Object>();
		}
		for (String key : values.keySet()) {
			Object o = values.get(key);
			if (original.containsKey(key)) {
				Object test = original.get(key);
				if (test != null)
					if (test.equals(o))
						continue;
			}
			changed.put(key, o);
			original.put(key, o);
		}
		if (playerData.containsKey(uuid))
			playerData.put(uuid, original);
		if (b instanceof FileBackend) {
			FileBackend fb = (FileBackend) b;
			File folder = new File(fb.getFolder(), "Players");
			try {
				if (!new File(folder, uuid + ".yml").isDirectory()) {
					Config c = new Config(folder, uuid + ".yml", PseudoAPI.plugin);
					FileConfiguration fc = c.getConfig();
					for (String key : changed.keySet()) {
						Object value = changed.get(key);
						fc.set(key, value);
					}
					c.save();
				}
			} catch (SecurityException e) {
				PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "No permission to access: " + folder.getAbsolutePath());
			}
		} else if (b instanceof SQLBackend) {
			SQLBackend sb = (SQLBackend) b;
			BasicDataSource data = sb.getDataSource();
			try (Connection c = data.getConnection()) {
				for (String key : changed.keySet()) {
					try (PreparedStatement ps = c.prepareStatement("INSERT INTO `" + sb.getPrefix() + "Players` (`uuid`,`" + key + "`) VALUES (?,?) ON DUPLICATE KEY UPDATE `" + key + "`=?;")) {
						Object value = changed.get(key);
						ps.setString(1, uuid);
						ps.setObject(2, value);
						ps.setObject(3, value);
						try {
							ps.execute();
						} catch (SQLException e) {
							PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "Error updating player: " + uuid + " from table: " + sb.getPrefix() + "Players in key: " + key + " with value: " + String.valueOf(value) + " in database: " + sb.getName());
							PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
						}
					} catch (SQLException e) {
						PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "Error when preparing statement in database: " + sb.getName());
						PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
					}
				}
			} catch (SQLException e) {
				PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "Error while accessing database: " + sb.getName());
				PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
			}
		}
	}

	public static void setPlayerSetting(String uuid, String key, Object value) {
		HashMap<String, Object> original;
		if (playerData.containsKey(uuid)) {
			original = playerData.get(uuid);
			if (original == null) {
				original = getPlayer(uuid);
			}
		} else {
			original = getPlayer(uuid);
		}
		if (original == null) {
			original = new HashMap<String, Object>();
		}
		if (original.containsKey(key)) {
			Object test = original.get(key);
			if (test != null)
				if (test.equals(value))
					return;
		}
		original.put(key, value);
		if (playerData.containsKey(uuid))
			playerData.put(uuid, original);
		if (b instanceof FileBackend) {
			FileBackend fb = (FileBackend) b;
			File folder = new File(fb.getFolder(), "Players");
			try {
				if (!new File(folder, uuid + ".yml").isDirectory()) {
					Config c = new Config(folder, uuid + ".yml", PseudoAPI.plugin);
					FileConfiguration fc = c.getConfig();
					fc.set(key, value);
					c.save();
				}
			} catch (SecurityException e) {
				PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "No permission to access: " + folder.getAbsolutePath());
			}
		} else if (b instanceof SQLBackend) {
			SQLBackend sb = (SQLBackend) b;
			BasicDataSource data = sb.getDataSource();
			try (Connection c = data.getConnection()) {
				try (PreparedStatement ps = c.prepareStatement("INSERT INTO `" + sb.getPrefix() + "Players` (`uuid`,`" + key + "`) VALUES (?,?) ON DUPLICATE KEY UPDATE `" + key + "`=?;")) {
					ps.setString(1, uuid);
					ps.setObject(2, value);
					ps.setObject(3, value);
					try {
						ps.execute();
					} catch (SQLException e) {
						PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "Error updating player: " + uuid + " from table: " + sb.getPrefix() + "Players in key: " + key + " with value: " + String.valueOf(value) + " in database: " + sb.getName());
						PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
					}
				} catch (SQLException e) {
					PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "Error when preparing statement in database: " + sb.getName());
					PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
				}
			} catch (SQLException e) {
				PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "Error while accessing database: " + sb.getName());
				PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
			}
		}
	}

	public static Object getPlayerSetting(String uuid, String key) {
		HashMap<String, Object> data = playerData.get(uuid);
		if (data != null && !(data.isEmpty()))
			return data.get(key);
		else
			return getPlayerSingle(uuid, key);
	}

	public static HashMap<String, Object> getPlayerSettings(String uuid) {
		HashMap<String, Object> data = playerData.get(uuid);
		if (data != null && !(data.isEmpty()))
			return data;
		else {
			data = getPlayer(uuid);
			playerData.put(uuid, data);
			return data;
		}
	}

	private static Object getPlayerSingle(String uuid, String key) {
		if (b instanceof FileBackend) {
			FileBackend fb = (FileBackend) b;
			File folder = new File(fb.getFolder(), "Players");
			try {
				if (new File(folder, uuid + ".yml").isFile()) {
					Config c = new Config(folder, uuid + ".yml", PseudoAPI.plugin);
					FileConfiguration fc = c.getConfig();
					Object o = fc.get(key);
					return o;
				}
			} catch (SecurityException e) {
				PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "No permission to access: " + folder.getAbsolutePath());
			}
		} else if (b instanceof SQLBackend) {
			SQLBackend sb = (SQLBackend) b;
			BasicDataSource data = sb.getDataSource();
			try (Connection c = data.getConnection()) {
				try (PreparedStatement ps = c.prepareStatement("SELECT " + key + " FROM `" + sb.getPrefix() + "Players` WHERE `uuid`=? LIMIT 1;")) {
					ps.setString(1, uuid);
					try (ResultSet rs = ps.executeQuery()) {
						if (rs.next()) {
							Object o = rs.getObject(1);
							return o;
						}
					} catch (SQLException e) {
						PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "Error when getting key: " + key + " from player: " + uuid + " from table: " + sb.getPrefix() + "Players in database: " + sb.getName());
						PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
					}
				} catch (SQLException e) {
					PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "Error when preparing statement in database: " + sb.getName());
					PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
				}
			} catch (SQLException e) {
				PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "Error while accessing database: " + sb.getName());
				PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
			}
		}
		return null;
	}

	private static HashMap<String, Object> getPlayer(String uuid) {
		if (b instanceof FileBackend) {
			FileBackend fb = (FileBackend) b;
			File folder = new File(fb.getFolder(), "Players");
			try {
				if (new File(folder, uuid + ".yml").isFile()) {
					Config c = new Config(folder, uuid + ".yml", PseudoAPI.plugin);
					FileConfiguration fc = c.getConfig();
					Set<String> keys = fc.getKeys(false);
					HashMap<String, Object> result = new HashMap<String, Object>(keys.size());
					for (String s : keys) {
						result.put(s, fc.get(s));
					}
					return result;
				}
			} catch (SecurityException e) {
				PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "No permission to access: " + folder.getAbsolutePath());
			}
		} else if (b instanceof SQLBackend) {
			SQLBackend sb = (SQLBackend) b;
			BasicDataSource data = sb.getDataSource();
			try (Connection c = data.getConnection()) {
				try (PreparedStatement ps = c.prepareStatement("SELECT * FROM `" + sb.getPrefix() + "Players` WHERE `uuid`=? LIMIT 1;")) {
					ps.setString(1, uuid);
					try (ResultSet rs = ps.executeQuery()) {
						ResultSetMetaData md = rs.getMetaData();
						int columns = md.getColumnCount();
						HashMap<String, Object> result = new HashMap<String, Object>(columns);
						if (rs.next()) {
							for (int i = 1; i <= columns; i++) {
								result.put(md.getColumnName(i), rs.getObject(i));
							}
							return result;
						}
					} catch (SQLException e) {
						PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "Error when getting player: " + uuid + " from table: " + sb.getPrefix() + "Players in database: " + sb.getName());
						PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
					}
				} catch (SQLException e) {
					PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "Error when preparing statement in database: " + sb.getName());
					PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
				}
			} catch (SQLException e) {
				PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "Error while accessing database: " + sb.getName());
				PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
			}
		}
		return null;
	}
	
	public static void migrateBackends(Backend origin, Backend destination) {
		HashMap<String, HashMap<String, Object>> values = getBackend(origin);
		setBackend(destination, values);
	}

	private static void setBackend(Backend b, HashMap<String, HashMap<String, Object>> values) {
		if (b instanceof FileBackend) {
			FileBackend fb = (FileBackend) b;
			fb.getFolder().mkdir();
			File folder = new File(fb.getFolder(), "Players");
			folder.mkdir();
			try {
				for (String uuid : values.keySet()) {
					if (!new File(folder, uuid + ".yml").isDirectory()) {
						HashMap<String, Object> playerData = values.get(uuid);
						Config c = new Config(folder, uuid + ".yml", PseudoAPI.plugin);
						FileConfiguration fc = c.getConfig();
						for (String key : playerData.keySet()) {
							Object value = playerData.get(key);
							fc.set(key, value);
						}
						c.save();
					}
				}
			} catch (SecurityException e) {
				PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "No permission to access: " + folder.getAbsolutePath());
			}
		} else if (b instanceof SQLBackend) {
			SQLBackend sb = (SQLBackend) b;
			if (sb instanceof MySQLBackend) {
				MySQLBackend mb = (MySQLBackend) sb;
				try (Connection c = DriverManager.getConnection(mb.getURL(),mb.getUsername(),mb.getPassword())) {
					for (String uuid : values.keySet()) {
						HashMap<String, Object> playerData = values.get(uuid);
						for (String key : playerData.keySet()) {
							try (PreparedStatement ps = c.prepareStatement("INSERT INTO `" + sb.getPrefix() + "Players` (`uuid`,`" + key + "`) VALUES (?,?) ON DUPLICATE KEY UPDATE `" + key + "`=?;")) {
								Object value = playerData.get(key);
								ps.setString(1, uuid);
								ps.setObject(2, value);
								ps.setObject(3, value);
								try {
									ps.execute();
								} catch (SQLException e) {
									PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "Error updating player: " + uuid + " from table: " + sb.getPrefix() + "Players in key: " + key + " with value: " + String.valueOf(value) + " in database: " + sb.getName());
									PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
								}
							} catch (SQLException e) {
								PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "Error when preparing statement in database: " + sb.getName());
								PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
							}
						}
					}
				} catch (SQLException e) {
					PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "Error while accessing database: " + sb.getName());
					PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
				}
			}
		}
	}

	private static HashMap<String, HashMap<String, Object>> getBackend(Backend b) {
		HashMap<String, HashMap<String, Object>> allData = new HashMap<String, HashMap<String, Object>>();
		if (b instanceof FileBackend) {
			FileBackend fb = (FileBackend) b;
			fb.getFolder().mkdir();
			File folder = new File(fb.getFolder(), "Players");
			folder.mkdir();
			try {
				File[] files = folder.listFiles();
				for (File f : files) {
					if (f.getName().endsWith(".yml")) {
						Config c = new Config(folder, f.getName(), PseudoAPI.plugin);
						FileConfiguration fc = c.getConfig();
						Set<String> keys = fc.getKeys(false);
						HashMap<String, Object> result = new HashMap<String, Object>(keys.size());
						for (String s : keys) {
							result.put(s, fc.get(s));
						}
						allData.put(f.getName().substring(0, f.getName().length() - 4), result);
					}
				}
			} catch (SecurityException e) {
				PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "No permission to access: " + folder.getAbsolutePath());
			}
		} else if (b instanceof SQLBackend) {
			SQLBackend sb = (SQLBackend) b;
			if (sb instanceof MySQLBackend) {
				MySQLBackend mb = (MySQLBackend) sb;
				try (Connection c = DriverManager.getConnection(mb.getURL(),mb.getUsername(),mb.getPassword())) {
					try (Statement st = c.createStatement()) {
						try (ResultSet rs = st.executeQuery("SELECT * FROM `" + sb.getPrefix() + "Players`;")) {
							ResultSetMetaData md = rs.getMetaData();
							int columns = md.getColumnCount();
							while (rs.next()) {
								HashMap<String, Object> result = new HashMap<String, Object>(columns);
								String uuid = "";
								for (int i = 1; i <= columns; i++) {
									if (md.getColumnName(i).equalsIgnoreCase("uuid"))
										uuid = rs.getString(i);
									else
										result.put(md.getColumnName(i), rs.getObject(i));
								}
								allData.put(uuid, result);
							}
						} catch (SQLException e) {
							PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "Error when getting player data from table: " + sb.getPrefix() + "Players in database: " + sb.getName());
							PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
						}
					} catch (SQLException e) {
						PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "Error when creating statement in database: " + sb.getName());
						PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
					}
				} catch (SQLException e) {
					PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "Error while accessing database: " + sb.getName());
					PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
				}
			}
		}
		return null;
	}

	public static int getUniquePlayers() {
		return uniquePlayers;
	}

}
