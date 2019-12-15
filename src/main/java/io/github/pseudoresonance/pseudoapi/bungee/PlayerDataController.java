package io.github.pseudoresonance.pseudoapi.bungee;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.apache.commons.dbcp2.BasicDataSource;

import io.github.pseudoresonance.pseudoapi.bukkit.data.Backend;
import io.github.pseudoresonance.pseudoapi.bukkit.data.MySQLBackend;
import io.github.pseudoresonance.pseudoapi.bukkit.data.SQLBackend;
import io.github.pseudoresonance.pseudoapi.bukkit.playerdata.Column;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PlayerDataController {

	private static ArrayList<Column> dataTypes = new ArrayList<Column>();
	private static ArrayList<Column> serverDataTypes = new ArrayList<Column>();

	private static HashMap<String, HashMap<String, Object>> playerData = new HashMap<String, HashMap<String, Object>>();

	private static DualHashBidiMap<String, String> uuids = new DualHashBidiMap<String, String>();

	private static Backend b;

	protected static void update() {
		b = Data.getBackend();
		setup();
		for (ProxiedPlayer p : PseudoAPI.plugin.getProxy().getPlayers()) {
			playerServer(p.getUniqueId().toString(), Config.getServerName(p.getServer().getInfo().getName()));
		}
	}

	protected static boolean addColumn(Column col) {
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
												PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("Error when renaming column: " + key + " to: " + key + suffix + " in table: " + sb.getPrefix() + "Players in database: " + sb.getName()).color(ChatColor.RED).create());
												PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("SQLError " + ex.getErrorCode() + ": (State: " + ex.getSQLState() + ") - " + ex.getMessage()).create());
												break;
											}
										} catch (SQLException ex) {
											PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("Error when creating statement in database: " + sb.getName()).create());
											PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("SQLError " + ex.getErrorCode() + ": (State: " + ex.getSQLState() + ") - " + ex.getMessage()).create());
										}
									}
								}
								PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("Error when converting column: " + key + " to type: " + value + " with default value: " + defaultValue + " in table: " + sb.getPrefix() + "Players in database: " + sb.getName()).create());
								PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage()).create());
							}
						} catch (SQLException e) {
							PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("Error when creating statement in database: " + sb.getName()).create());
							PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage()).create());
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
						PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("Error when adding column: " + key + " of type: " + value + " with default value: " + defaultValue + " in table: " + sb.getPrefix() + "Players in database: " + sb.getName()).create());
						PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage()).create());
					}
				} catch (SQLException e) {
					PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("Error when creating statement in database: " + sb.getName()).create());
					PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage()).create());
				}
			} catch (SQLException e) {
				PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("Error while accessing database: " + sb.getName()).create());
				PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage()).create());
			}
		}
		return false;
	}

	protected static void setup() {
		if (b instanceof SQLBackend) {
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
							PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("Error when getting table description in table: " + sb.getPrefix() + "Players in database: " + sb.getName()).create());
							PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage()).create());
						}
					}
					serverDataTypes = columns;
					if (create) {
						try (Statement st = c.createStatement()) {
							try {
								st.execute("CREATE TABLE IF NOT EXISTS `" + sb.getPrefix() + "Players` (`uuid` VARCHAR(36) PRIMARY KEY, `username` VARCHAR(16), `firstjoin` TIMESTAMP DEFAULT CURRENT_TIMESTAMP, `lastjoinleave` TIMESTAMP DEFAULT CURRENT_TIMESTAMP, `playtime` BIGINT(20) UNSIGNED DEFAULT 0, `lastserver` VARCHAR(36) DEFAULT NULL, `locale` VARCHAR(16) DEFAULT NULL, `online` BIT DEFAULT 0, `ip` VARCHAR(15) DEFAULT '0.0.0.0');");
							} catch (SQLException e) {
								PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("Error when creating table: " + sb.getPrefix() + "Players in database: " + sb.getName()).create());
								PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage()).create());
							}
						} catch (SQLException e) {
							PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("Error when creating statement in database: " + sb.getName()).create());
							PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage()).create());
						}
						try (Statement st = c.createStatement()) {
							for (Column col : dataTypes) {
								String key = col.getName();
								String value = col.getType();
								String defaultValue = col.getDefaultValue();
								try {
									st.execute("ALTER TABLE `" + sb.getPrefix() + "Players` ADD " + key + " " + value + " DEFAULT " + defaultValue + ";");
								} catch (SQLException e) {
									PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("Error when adding column: " + key + " of type: " + value + " with default value: " + defaultValue + " in table: " + sb.getPrefix() + "Players in database: " + sb.getName()).create());
									PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage()).create());
								}
							}
						} catch (SQLException e) {
							PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("Error when creating statement in database: " + sb.getName()).create());
							PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage()).create());
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
										PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("Error when converting column: " + key + " to type: " + value + " with default value: " + defaultValue + " in table: " + sb.getPrefix() + "Players in database: " + sb.getName()).create());
										PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage()).create());
									}
								}
							} catch (SQLException e) {
								PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("Error when creating statement in database: " + sb.getName()).create());
								PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage()).create());
							}
							try (Statement st = c.createStatement()) {
								for (Column col : oldColumns) {
									String key = col.getName();
									String value = col.getType();
									String suffix = "_OLD";
									try {
										st.execute("ALTER TABLE `" + sb.getPrefix() + "Players` CHANGE " + key + " " + key + suffix + " " + value + ";");
									} catch (SQLException e) {
										PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("Error when renaming column: " + key + " to: " + key + suffix + " in table: " + sb.getPrefix() + "Players in database: " + sb.getName()).create());
										PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage()).create());
									}
								}
							} catch (SQLException e) {
								PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("Error when creating statement in database: " + sb.getName()).create());
								PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage()).create());
							}
							try (Statement st = c.createStatement()) {
								for (Column col : newColumns) {
									String key = col.getName();
									String value = col.getType();
									String defaultValue = col.getDefaultValue();
									try {
										st.execute("ALTER TABLE `" + sb.getPrefix() + "Players` ADD " + key + " " + value + " DEFAULT " + defaultValue + ";");
									} catch (SQLException e) {
										PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("Error when adding column: " + key + " of type: " + value + " with default value: " + defaultValue + " in table: " + sb.getPrefix() + "Players in database: " + sb.getName()).create());
										PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage()).create());
									}
								}
							} catch (SQLException e) {
								PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("Error when creating statement in database: " + sb.getName()).create());
								PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage()).create());
							}
						} else {
							PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("Selected backend contains incorrectly formatted table: " + sb.getPrefix() + "Players! Disabling PseudoAPI!").color(ChatColor.RED).create());
							PseudoAPI.plugin.onDisable();
							return;
						}
					}
				} catch (SQLException e) {
					PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("Error when creating statement in database: " + sb.getName()).create());
					PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage()).create());
				}
			} catch (SQLException e) {
				PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("Error while accessing database: " + sb.getName()).create());
				PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage()).create());
			}
		}
		getUUIDS();
	}

	protected static void playerJoin(ProxiedPlayer p) {
		String uuid = p.getUniqueId().toString();
		String username = p.getName();
		String nickname = username;
		if (!playerData.containsKey(uuid)) {
			HashMap<String, Object> player = getPlayer(uuid).join();
			if (player != null) {
				playerData.put(uuid, player);
				if (player.containsKey("nickname")) {
					Object nicknameO = player.get("nickname");
					if (nicknameO != null)
						nickname = (String) nicknameO;
				}
			} else
				playerData.put(uuid, new HashMap<String, Object>());
		}
		HashMap<String, Object> settings = new HashMap<String, Object>();
		settings.put("ip", p.getAddress().getAddress().getHostAddress());
		settings.put("online", true);
		settings.put("username", username);
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		settings.put("lastjoinleave", timestamp);
		uuids.put(uuid, username);
		String name = getName(uuid);
		if (name == null)
			settings.put("firstjoin", timestamp);
		setPlayerSettings(uuid, settings);
		if (Config.enableJoinLeave) {
			if (!Config.joinFormat.equals("")) {
				String format = Config.joinFormat;
				format = format.replace("{name}", username);
				format = format.replace("{nickname}", nickname);
				format = format.replace("{uuid}", uuid);
				format = ChatColor.translateAlternateColorCodes('&', format);
				ProxyServer.getInstance().broadcast(new ComponentBuilder(format).create());
			}
		}
	}

	protected static void playerServer(String uuid, String serverName) {
		HashMap<String, Object> settings = new HashMap<String, Object>();
		settings.put("lastserver", serverName);
		setPlayerSettings(uuid, settings);
	}

	protected static void playerLeave(String uuid) {
		HashMap<String, Object> settings = new HashMap<String, Object>();
		Object o = getPlayerSetting(uuid, "lastjoinleave").join();
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
			Object ob = getPlayerSetting(uuid, "playtime").join();
			if (ob instanceof BigInteger || ob instanceof Long) {
				long playTime = 0;
				if (ob instanceof BigInteger)
					playTime = ((BigInteger) ob).longValueExact();
				else
					playTime = (Long) ob;
				playTime = diff >= 0 ? playTime + diff : playTime - diff;
				settings.put("playtime", playTime);
			} else if (ob == null) {
				settings.put("playtime", diff);
			}
		}
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		settings.put("lastjoinleave", timestamp);
		settings.put("online", false);
		setPlayerSettings(uuid, settings);
		playerData.get(uuid).clear();
		playerData.remove(uuid);
	}

	protected static Set<String> getNames() {
		return uuids.values();
	}

	protected static Set<String> getUUIDs() {
		return uuids.keySet();
	}

	protected static String getName(String uuid) {
		for (String u : uuids.keySet()) {
			if (u.equalsIgnoreCase(uuid)) {
				return uuids.get(u);
			}
		}
		return null;
	}

	protected static String getUUID(String name) {
		for (String n : uuids.values()) {
			if (n.equalsIgnoreCase(name)) {
				return uuids.getKey(n);
			}
		}
		return null;
	}

	private static void getUUIDS() {
		if (b instanceof SQLBackend) {
			SQLBackend sb = (SQLBackend) b;
			BasicDataSource data = sb.getDataSource();
			try (Connection c = data.getConnection()) {
				try (Statement st = c.createStatement()) {
					try (ResultSet rs = st.executeQuery("SELECT uuid,username FROM `" + sb.getPrefix() + "Players` ORDER BY `lastjoinleave` DESC;")) {
						while (rs.next()) {
							uuids.put(rs.getString("uuid"), rs.getString("username"));
						}
					} catch (SQLException e) {
						PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("Error when getting player names and uuids from table: " + sb.getPrefix() + "Players in database: " + sb.getName()).create());
						PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage()).create());
					}
				} catch (SQLException e) {
					PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("Error when creating statement in database: " + sb.getName()).create());
					PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage()).create());
				}
			} catch (SQLException e) {
				PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("Error while accessing database: " + sb.getName()).create());
				PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage()).create());
			}
		}
	}

	protected static CompletableFuture<Void> setPlayerSettings(String uuid, HashMap<String, Object> values) {
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
				if (b instanceof SQLBackend) {
					SQLBackend sb = (SQLBackend) b;
					BasicDataSource data = sb.getDataSource();
					try (Connection c = data.getConnection()) {
						String columnList = "";
						String valueList = "";
						String keyList = "";
						for (String key : values.keySet()) {
							columnList += ",`" + key + "`";
							valueList += ",?";
							keyList += ", `" + key + "`=?";
						}
						keyList = keyList.substring(2);
						String statement = "INSERT INTO `" + sb.getPrefix() + "Players` (`uuid`" + columnList + ") VALUES (?" + valueList + ") ON DUPLICATE KEY UPDATE " + keyList + ";";
						try (PreparedStatement ps = c.prepareStatement(statement)) {
							ps.setString(1, uuid);
							int i = 1;
							Collection<Object> valueCol = values.values();
							for (Object value : valueCol) {
								i++;
								ps.setObject(i, value);
								ps.setObject(i + valueCol.size(), value);
							}
							try {
								ps.execute();
							} catch (SQLException e) {
								PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("Error updating player: " + uuid + " from table: " + sb.getPrefix() + "Players in database: " + sb.getName()).create());
								PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage()).create());
							}
						} catch (SQLException e) {
							PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("Error when preparing statement in database: " + sb.getName()).create());
							PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage()).create());
						}
					} catch (SQLException e) {
						PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("Error while accessing database: " + sb.getName()).create());
						PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage()).create());
					}
				}
			}
		});
	}

	protected static CompletableFuture<Void> setPlayerSetting(String uuid, String key, Object value) {
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
			if (b instanceof SQLBackend) {
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
							PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("Error updating player: " + uuid + " from table: " + sb.getPrefix() + "Players in key: " + key + " with value: " + String.valueOf(value) + " in database: " + sb.getName()).create());
							PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage()).create());
						}
					} catch (SQLException e) {
						PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("Error when preparing statement in database: " + sb.getName()).create());
						PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage()).create());
					}
				} catch (SQLException e) {
					PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("Error while accessing database: " + sb.getName()).create());
					PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage()).create());
				}
			}
		});
	}

	public static CompletableFuture<Object> getPlayerSetting(String uuid, String key) {
		return getPlayerSetting(uuid, key, false);
	}

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

	public static CompletableFuture<HashMap<String, Object>> getPlayerSettings(String uuid) {
		return getPlayerSettings(uuid, false);
	}

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
			if (b instanceof SQLBackend) {
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
							PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("Error when getting key: " + key + " from player: " + uuid + " from table: " + sb.getPrefix() + "Players in database: " + sb.getName()).create());
							PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage()).create());
						}
					} catch (SQLException e) {
						PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("Error when preparing statement in database: " + sb.getName()).create());
						PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage()).create());
					}
				} catch (SQLException e) {
					PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("Error while accessing database: " + sb.getName()).create());
					PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage()).create());
				}
			}
			return null;
		});
	}

	private static CompletableFuture<HashMap<String, Object>> getPlayer(String uuid) {
		return CompletableFuture.supplyAsync(() -> {
			if (b instanceof SQLBackend) {
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
							PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("Error when getting player: " + uuid + " from table: " + sb.getPrefix() + "Players in database: " + sb.getName()).create());
							PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage()).create());
						}
					} catch (SQLException e) {
						PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("Error when preparing statement in database: " + sb.getName()).create());
						PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage()).create());
					}
				} catch (SQLException e) {
					PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("Error while accessing database: " + sb.getName()).create());
					PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("SQLError " + e.getErrorCode() + ": (State: " + e.getSQLState() + ") - " + e.getMessage()).create());
				}
			}
			return null;
		});
	}

}
