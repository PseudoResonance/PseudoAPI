package io.github.pseudoresonance.pseudoapi.bukkit.playerdata;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.dbcp2.BasicDataSource;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import io.github.pseudoresonance.pseudoapi.bukkit.Chat;
import io.github.pseudoresonance.pseudoapi.bukkit.Chat.Errors;
import io.github.pseudoresonance.pseudoapi.bukkit.PseudoAPI;
import io.github.pseudoresonance.pseudoapi.bukkit.data.Backend;
import io.github.pseudoresonance.pseudoapi.bukkit.data.Data;
import io.github.pseudoresonance.pseudoapi.bukkit.data.FileBackend;
import io.github.pseudoresonance.pseudoapi.bukkit.data.MySQLBackend;
import io.github.pseudoresonance.pseudoapi.bukkit.data.SQLBackend;
import io.github.pseudoresonance.pseudoapi.bukkit.listeners.PlayerJoinLeaveL;
import io.github.pseudoresonance.pseudoapi.bukkit.utils.ConfigFile;
import net.md_5.bungee.api.ChatColor;

public class ServerPlayerDataController {

	private static ArrayList<Column> dataTypes = new ArrayList<Column>();
	private static ArrayList<Column> serverDataTypes = new ArrayList<Column>();

	private static ConcurrentHashMap<String, HashMap<String, Object>> playerData = new ConcurrentHashMap<String, HashMap<String, Object>>();

	private static Backend b;

	/**
	 * Updates settings and setups up backend
	 */
	public static void update() {
		b = Data.getServerBackend();
		setup();
		playerData.clear();
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			PlayerJoinLeaveL.playerJoin(p);
		}
	}

	/**
	 * Adds the given {@link Column} to the backend
	 * 
	 * @param col Column to add
	 * @return Whether or not adding column was successful
	 */
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
												PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "Error when renaming column: " + key + " to: " + key + suffix + " in table: " + sb.getPrefix() + "Players in database: " + sb.getName());
												PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "SQLError " + ex.getErrorCode() + ": (State: " + ex.getSQLState() + ") - " + ex.getMessage());
												break;
											}
										} catch (SQLException ex) {
											PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "Error when creating statement in database: " + sb.getName());
											PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "SQLError " + ex.getErrorCode() + ": (State: " + ex.getSQLState() + ") - " + ex.getMessage());
										}
									}
								}
								PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "Error when converting column: " + key + " to type: " + value + " with default value: " + defaultValue + " in table: " + sb.getPrefix() + "Players in database: " + sb.getName());
								PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
							}
						} catch (SQLException e) {
							PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "Error when creating statement in database: " + sb.getName());
							PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
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
						PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "Error when adding column: " + key + " of type: " + value + " with default value: " + defaultValue + " in table: " + sb.getPrefix() + "Players in database: " + sb.getName());
						PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
					}
				} catch (SQLException e) {
					PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "Error when creating statement in database: " + sb.getName());
					PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
				}
			} catch (SQLException e) {
				PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "Error while accessing database: " + sb.getName());
				PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
			}
		}
		return false;
	}

	/**
	 * Sets up backend
	 */
	public static void setup() {
		if (b instanceof FileBackend) {
			FileBackend fb = (FileBackend) b;
			File dir = fb.getFolder();
			if (!dir.isDirectory()) {
				dir.mkdir();
			}
			File folder = new File(fb.getFolder(), "Players");
			try {
				if (!folder.isDirectory())
					folder.mkdir();
			} catch (SecurityException e) {
				PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "No permission to access: " + folder.getAbsolutePath());
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
					boolean create = false;
					try (ResultSet rs = s.executeQuery("DESCRIBE `" + sb.getPrefix() + "Players`;")) {
						while (rs.next()) {
							String field = rs.getString("Field");
							String type = rs.getString("Type");
							String defaultValue = String.valueOf(rs.getObject("Default"));
							if (field.equalsIgnoreCase("uuid"))
								uuidCol = true;
							else
								columns.add(new Column(field, type, defaultValue));
						}
					} catch (SQLException e) {
						if (e.getErrorCode() == 1146) {
							create = true;
						} else {
							PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "Error when getting table description in table: " + sb.getPrefix() + "Players in database: " + sb.getName());
							PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
						}
					}
					serverDataTypes = columns;
					if (create) {
						try (Statement st = c.createStatement()) {
							try {
								st.execute("CREATE TABLE IF NOT EXISTS `" + sb.getPrefix() + "Players` (`uuid` VARCHAR(36) PRIMARY KEY);");
							} catch (SQLException e) {
								PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "Error when creating table: " + sb.getPrefix() + "Players in database: " + sb.getName());
								PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
							}
						} catch (SQLException e) {
							PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "Error when creating statement in database: " + sb.getName());
							PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
						}
						try (Statement st = c.createStatement()) {
							for (Column col : dataTypes) {
								String key = col.getName();
								String value = col.getType();
								String defaultValue = col.getDefaultValue();
								try {
									st.execute("ALTER TABLE `" + sb.getPrefix() + "Players` ADD " + key + " " + value + " DEFAULT " + defaultValue + ";");
								} catch (SQLException e) {
									PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "Error when adding column: " + key + " of type: " + value + " with default value: " + defaultValue + " in table: " + sb.getPrefix() + "Players in database: " + sb.getName());
									PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
								}
							}
						} catch (SQLException e) {
							PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "Error when creating statement in database: " + sb.getName());
							PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
						}
					} else {
						if (uuidCol) {
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
										PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "Error when converting column: " + key + " to type: " + value + " with default value: " + defaultValue + " in table: " + sb.getPrefix() + "Players in database: " + sb.getName());
										PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
									}
								}
							} catch (SQLException e) {
								PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "Error when creating statement in database: " + sb.getName());
								PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
							}
							try (Statement st = c.createStatement()) {
								for (Column col : oldColumns) {
									String key = col.getName();
									String value = col.getType();
									String suffix = "_OLD";
									try {
										st.execute("ALTER TABLE `" + sb.getPrefix() + "Players` CHANGE " + key + " " + key + suffix + " " + value + ";");
									} catch (SQLException e) {
										PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "Error when renaming column: " + key + " to: " + key + suffix + " in table: " + sb.getPrefix() + "Players in database: " + sb.getName());
										PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
									}
								}
							} catch (SQLException e) {
								PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "Error when creating statement in database: " + sb.getName());
								PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
							}
							try (Statement st = c.createStatement()) {
								for (Column col : newColumns) {
									String key = col.getName();
									String value = col.getType();
									String defaultValue = col.getDefaultValue();
									try {
										st.execute("ALTER TABLE `" + sb.getPrefix() + "Players` ADD " + key + " " + value + " DEFAULT " + defaultValue + ";");
									} catch (SQLException e) {
										PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "Error when adding column: " + key + " of type: " + value + " with default value: " + defaultValue + " in table: " + sb.getPrefix() + "Players in database: " + sb.getName());
										PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
									}
								}
							} catch (SQLException e) {
								PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "Error when creating statement in database: " + sb.getName());
								PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
							}
						} else {
							Chat.sendConsoleMessage(ChatColor.RED + "Selected backend contains incorrectly formatted table: " + sb.getPrefix() + "Players! Disabling PseudoAPI!");
							Bukkit.getServer().getPluginManager().disablePlugin(PseudoAPI.plugin);
							return;
						}
					}
				} catch (SQLException e) {
					PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "Error when creating statement in database: " + sb.getName());
					PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
				}
			} catch (SQLException e) {
				PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "Error while accessing database: " + sb.getName());
				PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
			}
		}
	}

	public static void playerJoin(String uuid, String username) {
		if (!playerData.containsKey(uuid)) {
			HashMap<String, Object> player = getPlayer(uuid).join();
			if (player != null)
				playerData.put(uuid, player);
			else
				playerData.put(uuid, new HashMap<String, Object>());
		}
	}

	public static void playerLeave(String uuid, String username) {
		if (playerData.containsKey(uuid)) {
			playerData.get(uuid).clear();
			playerData.remove(uuid);
		}
	}

	/**
	 * Sets a list of settings on the given player
	 * 
	 * @param uuid UUID of player to set
	 * @param values {@link HashMap} of player settings to set
	 * @return {@link CompletableFuture} of when settings have been set
	 */
	public static CompletableFuture<Void> setPlayerSettings(String uuid, HashMap<String, Object> values) {
		return CompletableFuture.runAsync(() -> {
			HashMap<String, Object> original;
			LinkedHashMap<String, Object> changed = new LinkedHashMap<String, Object>();
			if (playerData.containsKey(uuid)) {
				original = playerData.get(uuid);
				if (original == null) {
					original = getPlayer(uuid).join();
				}
			} else {
				original = getPlayer(uuid).join();
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
			if (changed.size() > 0) {
				if (playerData.containsKey(uuid))
					playerData.put(uuid, original);
				if (b instanceof FileBackend) {
					FileBackend fb = (FileBackend) b;
					File folder = new File(fb.getFolder(), "Players");
					try {
						if (!new File(folder, uuid + ".yml").isDirectory()) {
							ConfigFile c = new ConfigFile(folder, uuid + ".yml", PseudoAPI.plugin);
							FileConfiguration fc = c.getConfig();
							for (String key : changed.keySet()) {
								Object value = changed.get(key);
								fc.set(key, value);
							}
							c.save();
						}
					} catch (SecurityException e) {
						PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "No permission to access: " + folder.getAbsolutePath());
					}
				} else if (b instanceof SQLBackend) {
					SQLBackend sb = (SQLBackend) b;
					BasicDataSource data = sb.getDataSource();
					try (Connection c = data.getConnection()) {
						String columnList = "";
						String valueList = "";
						String keyList = "";
						for (String key : changed.keySet()) {
							columnList += ",`" + key + "`";
							valueList += ",?";
							keyList += ", `" + key + "`=?";
						}
						keyList = keyList.substring(2);
						String statement = "INSERT INTO `" + sb.getPrefix() + "Players` (`uuid`" + columnList + ") VALUES (?" + valueList + ") ON DUPLICATE KEY UPDATE " + keyList + ";";
						try (PreparedStatement ps = c.prepareStatement(statement)) {
							ps.setString(1, uuid);
							int i = 1;
							Collection<Object> valueCol = changed.values();
							for (Object value : valueCol) {
								i++;
								ps.setObject(i, value);
								ps.setObject(i + valueCol.size(), value);
							}
							try {
								ps.execute();
							} catch (SQLException e) {
								PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "Error updating player: " + uuid + " from table: " + sb.getPrefix() + "Players in database: " + sb.getName());
								PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
							}
						} catch (SQLException e) {
							PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "Error when preparing statement in database: " + sb.getName());
							PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
						}
					} catch (SQLException e) {
						PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "Error while accessing database: " + sb.getName());
						PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
					}
				}
			}
		});
	}

	/**
	 * Sets a single setting on the given player
	 * 
	 * @param uuid UUID of player to set
	 * @param key Key of player setting to set
	 * @param value Value of player setting to set
	 * @return {@link CompletableFuture} of when setting has been set
	 */
	public static CompletableFuture<Void> setPlayerSetting(String uuid, String key, Object value) {
		return CompletableFuture.runAsync(() -> {
			HashMap<String, Object> original;
			if (playerData.containsKey(uuid)) {
				original = playerData.get(uuid);
				if (original == null) {
					original = getPlayer(uuid).join();
				}
			} else {
				original = getPlayer(uuid).join();
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
						ConfigFile c = new ConfigFile(folder, uuid + ".yml", PseudoAPI.plugin);
						FileConfiguration fc = c.getConfig();
						fc.set(key, value);
						c.save();
					}
				} catch (SecurityException e) {
					PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "No permission to access: " + folder.getAbsolutePath());
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
							PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "Error updating player: " + uuid + " from table: " + sb.getPrefix() + "Players in key: " + key + " with value: " + String.valueOf(value) + " in database: " + sb.getName());
							PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
						}
					} catch (SQLException e) {
						PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "Error when preparing statement in database: " + sb.getName());
						PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
					}
				} catch (SQLException e) {
					PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "Error while accessing database: " + sb.getName());
					PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
				}
			}
		});
	}

	/**
	 * Gets a single setting from the given player
	 * Returns cached values if available
	 * 
	 * @param uuid UUID of player to get
	 * @param key Key of setting to get
	 * @return {@link CompletableFuture} of returned setting value
	 */
	public static CompletableFuture<Object> getPlayerSetting(String uuid, String key) {
		return getPlayerSetting(uuid, key, false);
	}

	/**
	 * Gets a single setting from the given player
	 * 
	 * @param uuid UUID of player to get
	 * @param key Key of setting to get
	 * @param forceUpdate Whether or not to force getting an update from the database
	 * @return {@link CompletableFuture} of returned setting value
	 */
	public static CompletableFuture<Object> getPlayerSetting(String uuid, String key, boolean forceUpdate) {
		HashMap<String, Object> data = playerData.get(uuid);
		if (data != null && !(data.isEmpty()) && data.containsKey(key) && !forceUpdate)
			return CompletableFuture.completedFuture(data.get(key));
		else {
			CompletableFuture<Object> fut = getPlayerSingle(uuid, key);
			fut.thenAcceptAsync(ret -> {
				if (playerData.containsKey(uuid)) {
					HashMap<String, Object> dataN = playerData.get(uuid);
					dataN.put(key, ret);
					playerData.put(uuid, dataN);
				}
			});
			return fut;
		}
	}

	/**
	 * Gets a list of settings from the given player
	 * Returns cached values if available
	 * 
	 * @param uuid UUID of player to get
	 * @return {@link CompletableFuture} of returned {@link HashMap} of player settings
	 */
	public static CompletableFuture<HashMap<String, Object>> getPlayerSettings(String uuid) {
		return getPlayerSettings(uuid, false);
	}

	/**
	 * Gets a list of settings from the given player
	 * 
	 * @param uuid UUID of player to get
	 * @param forceUpdate Whether or not to force getting an update from the database
	 * @return {@link CompletableFuture} of returned {@link HashMap} of player settings
	 */
	public static CompletableFuture<HashMap<String, Object>> getPlayerSettings(String uuid, boolean forceUpdate) {
		HashMap<String, Object> data = playerData.get(uuid);
		if (data != null && !(data.isEmpty()) && !forceUpdate)
			return CompletableFuture.completedFuture(data);
		else {
			CompletableFuture<HashMap<String, Object>> fut = getPlayer(uuid);
			fut.thenAcceptAsync(ret -> {
				if (playerData.containsKey(uuid)) {
					playerData.put(uuid, ret);
				}
			});
			return fut;
		}
	}

	private static CompletableFuture<Object> getPlayerSingle(String uuid, String key) {
		return CompletableFuture.supplyAsync(() -> {
			if (b instanceof FileBackend) {
				FileBackend fb = (FileBackend) b;
				File folder = new File(fb.getFolder(), "Players");
				try {
					if (new File(folder, uuid + ".yml").isFile()) {
						ConfigFile c = new ConfigFile(folder, uuid + ".yml", PseudoAPI.plugin);
						FileConfiguration fc = c.getConfig();
						Object o = fc.get(key);
						return o;
					}
				} catch (SecurityException e) {
					PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "No permission to access: " + folder.getAbsolutePath());
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
							PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "Error when getting key: " + key + " from player: " + uuid + " from table: " + sb.getPrefix() + "Players in database: " + sb.getName());
							PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
						}
					} catch (SQLException e) {
						PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "Error when preparing statement in database: " + sb.getName());
						PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
					}
				} catch (SQLException e) {
					PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "Error while accessing database: " + sb.getName());
					PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
				}
			}
			return null;
		});
	}

	private static CompletableFuture<HashMap<String, Object>> getPlayer(String uuid) {
		return CompletableFuture.supplyAsync(() -> {
			if (b instanceof FileBackend) {
				FileBackend fb = (FileBackend) b;
				File folder = new File(fb.getFolder(), "Players");
				try {
					if (new File(folder, uuid + ".yml").isFile()) {
						ConfigFile c = new ConfigFile(folder, uuid + ".yml", PseudoAPI.plugin);
						FileConfiguration fc = c.getConfig();
						Set<String> keys = fc.getKeys(false);
						HashMap<String, Object> result = new HashMap<String, Object>(keys.size());
						for (String s : keys) {
							result.put(s, fc.get(s));
						}
						return result;
					}
				} catch (SecurityException e) {
					PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "No permission to access: " + folder.getAbsolutePath());
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
							PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "Error when getting player: " + uuid + " from table: " + sb.getPrefix() + "Players in database: " + sb.getName());
							PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
						}
					} catch (SQLException e) {
						PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "Error when preparing statement in database: " + sb.getName());
						PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
					}
				} catch (SQLException e) {
					PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "Error while accessing database: " + sb.getName());
					PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
				}
			}
			return null;
		});
	}

	/**
	 * Migrates all data from one {@link Backend} to another
	 * 
	 * @param origin {@link Backend} to migrate from
	 * @param destination {@link Backend} to migrate to
	 * @return Whether or not migration was successful
	 */
	public static CompletableFuture<Void> migrateBackends(Backend origin, Backend destination) {
		return getBackend(origin).thenAcceptAsync(values -> {
			setBackend(destination, values);
		});
	}

	private static CompletableFuture<Void> setBackend(Backend b, HashMap<String, LinkedHashMap<String, Object>> values) {
		return CompletableFuture.runAsync(() -> {
			if (b instanceof FileBackend) {
				FileBackend fb = (FileBackend) b;
				fb.getFolder().mkdir();
				File folder = new File(fb.getFolder(), "Players");
				folder.mkdir();
				try {
					for (String uuid : values.keySet()) {
						if (!new File(folder, uuid + ".yml").isDirectory()) {
							LinkedHashMap<String, Object> playerData = values.get(uuid);
							ConfigFile c = new ConfigFile(folder, uuid + ".yml", PseudoAPI.plugin);
							FileConfiguration fc = c.getConfig();
							for (String key : playerData.keySet()) {
								Object value = playerData.get(key);
								fc.set(key, value);
							}
							c.save();
						}
					}
				} catch (SecurityException e) {
					PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "No permission to access: " + folder.getAbsolutePath());
				}
			} else if (b instanceof SQLBackend) {
				SQLBackend sb = (SQLBackend) b;
				if (sb instanceof MySQLBackend) {
					MySQLBackend mb = (MySQLBackend) sb;
					try (Connection c = DriverManager.getConnection(mb.getURL(), mb.getUsername(), mb.getPassword())) {
						for (String uuid : values.keySet()) {
							LinkedHashMap<String, Object> playerData = values.get(uuid);
							String columnList = "";
							String valueList = "";
							String keyList = "";
							for (String key : playerData.keySet()) {
								columnList += ",`" + key + "`";
								valueList += ",?";
								keyList += ", `" + key + "`=?";
							}
							keyList = keyList.substring(2);
							String statement = "INSERT INTO `" + sb.getPrefix() + "Players` (`uuid`" + columnList + ") VALUES (?" + valueList + ") ON DUPLICATE KEY UPDATE " + keyList + ";";
							try (PreparedStatement ps = c.prepareStatement(statement)) {
								ps.setString(1, uuid);
								int i = 1;
								Collection<Object> valueCol = playerData.values();
								for (Object value : valueCol) {
									i++;
									ps.setObject(i, value);
									ps.setObject(i + valueCol.size(), value);
								}
								try {
									ps.execute();
								} catch (SQLException e) {
									PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "Error updating player: " + uuid + " from table: " + sb.getPrefix() + "Players in database: " + sb.getName());
									PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
								}
							} catch (SQLException e) {
								PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "Error when preparing statement in database: " + sb.getName());
								PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
							}
						}
					} catch (SQLException e) {
						PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "Error while accessing database: " + sb.getName());
						PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
					}
				}
			}
		});
	}

	private static CompletableFuture<HashMap<String, LinkedHashMap<String, Object>>> getBackend(Backend b) {
		return CompletableFuture.supplyAsync(() -> {
			HashMap<String, LinkedHashMap<String, Object>> allData = new HashMap<String, LinkedHashMap<String, Object>>();
			if (b instanceof FileBackend) {
				FileBackend fb = (FileBackend) b;
				fb.getFolder().mkdir();
				File folder = new File(fb.getFolder(), "Players");
				folder.mkdir();
				try {
					File[] files = folder.listFiles();
					for (File f : files) {
						if (f.getName().endsWith(".yml")) {
							ConfigFile c = new ConfigFile(folder, f.getName(), PseudoAPI.plugin);
							FileConfiguration fc = c.getConfig();
							Set<String> keys = fc.getKeys(false);
							LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>(keys.size());
							for (String s : keys) {
								result.put(s, fc.get(s));
							}
							allData.put(f.getName().substring(0, f.getName().length() - 4), result);
						}
					}
				} catch (SecurityException e) {
					PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "No permission to access: " + folder.getAbsolutePath());
				}
			} else if (b instanceof SQLBackend) {
				SQLBackend sb = (SQLBackend) b;
				if (sb instanceof MySQLBackend) {
					MySQLBackend mb = (MySQLBackend) sb;
					try (Connection c = DriverManager.getConnection(mb.getURL(), mb.getUsername(), mb.getPassword())) {
						try (Statement st = c.createStatement()) {
							try (ResultSet rs = st.executeQuery("SELECT * FROM `" + sb.getPrefix() + "Players`;")) {
								ResultSetMetaData md = rs.getMetaData();
								int columns = md.getColumnCount();
								while (rs.next()) {
									LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>(columns);
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
								PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "Error when getting player data from table: " + sb.getPrefix() + "Players in database: " + sb.getName());
								PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
							}
						} catch (SQLException e) {
							PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "Error when creating statement in database: " + sb.getName());
							PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
						}
					} catch (SQLException e) {
						PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "Error while accessing database: " + sb.getName());
						PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage());
					}
				}
			}
			return null;
		});
	}

}
