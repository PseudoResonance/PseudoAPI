package io.github.pseudoresonance.pseudoapi.bungee;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.HashMap;

import io.github.pseudoresonance.pseudoapi.bukkit.data.Backend;
import io.github.pseudoresonance.pseudoapi.bukkit.data.MySQLBackend;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class Config {
	
	private Plugin plugin;
	
	private static HashMap<String, String> serverList = new HashMap<String, String>();
	
	protected static String backend = "file";
	private static HashMap<String, Backend> backends = new HashMap<String, Backend>();
	
	public static boolean enableJoinLeave = true;
	public static String joinFormat = "&e{nickname} &ejoined the network";
	public static String leaveFormat = "&e{nickname} &eleft the network";
	public static String serverChangeFormat = "&e{nickname} &ehas moved to &c{server_nick}";

	protected static boolean startupUpdate = true;
	protected static int startupDelay = 60;
	protected static int updateFrequency = 60;
	protected static boolean downloadUpdates = true;
	protected static boolean updateRestart = true;
	protected static boolean restartEmpty = true;
	protected static int restartWarning = 60;

	protected void reloadConfig() {
		try {
			File confF = new File(plugin.getDataFolder(), "config.yml");
			Configuration conf = ConfigurationProvider.getProvider(YamlConfiguration.class).load(confF);
			
			for (String key : conf.getSection("ServerList").getKeys()) {
				String name = getString(conf, "ServerList." + key, key);
				serverList.put(key, name);
			}
			
			backend = getString(conf, "Backend", backend);

			Data.stopBackends();
			for (String key : conf.getSection("Backends").getKeys()) {
				try {
					String type = conf.getString("Backends." + key + ".type");
					if (type.equalsIgnoreCase("mysql")) {
						String host = conf.getString("Backends." + key + ".host");
						int port = conf.getInt("Backends." + key + ".port");
						String username = conf.getString("Backends." + key + ".username");
						String password = conf.getString("Backends." + key + ".password");
						String database = conf.getString("Backends." + key + ".database");
						boolean useSSL = conf.getBoolean("Backends." + key + ".useSSL");
						boolean verifyServerCertificate = conf.getBoolean("Backends." + key + ".verifyServerCertificate");
						boolean requireSSL = conf.getBoolean("Backends." + key + ".requireSSL");
						String prefix = "";
						if (conf.contains("Backends." + key + ".prefix")) {
							prefix = conf.getString("Backends." + key + ".prefix");
						}
						MySQLBackend backend = new MySQLBackend(key, host, port, username, password, database, prefix, useSSL, verifyServerCertificate, requireSSL);
						backends.put(key, backend);
					} else {
						plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("Invalid backend type for backend: " + key + "!").color(ChatColor.RED).create());
					}
				} catch (Exception e) {
					plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("Invalid backend configuration for backend: " + key + "!").color(ChatColor.RED).create());
				}
			}
			if (backends.size() == 0) {
				plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("No backends configured! Disabling PseudoAPI!").color(ChatColor.RED).create());
				plugin.onDisable();
			}
			
			enableJoinLeave = getBoolean(conf, "EnableJoinLeave", enableJoinLeave);
			joinFormat = getString(conf, "JoinFormat", joinFormat);
			leaveFormat = getString(conf, "LeaveFormat", leaveFormat);
			serverChangeFormat = getString(conf, "ServerChangeFormat", serverChangeFormat);
			
			startupUpdate = getBoolean(conf, "StartupUpdate", startupUpdate);
			startupDelay = getInt(conf, "StartupDelay", startupDelay);
			updateFrequency = getInt(conf, "UpdateFrequency", updateFrequency);
			downloadUpdates = getBoolean(conf, "DownloadUpdates", downloadUpdates);
			updateRestart = getBoolean(conf, "UpdateRestart", updateRestart);
			restartEmpty = getBoolean(conf, "RestartEmpty", restartEmpty);
			restartWarning = getInt(conf, "RestartWarning", restartWarning);
			
			Data.loadBackends();
		} catch (IOException e) {
			plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("Error while reading config!").color(ChatColor.RED).create());
			e.printStackTrace();
		}
	}
	
	protected boolean resetConfig() {
		if (!plugin.getDataFolder().exists())
			plugin.getDataFolder().mkdirs();
		File confF = new File(plugin.getDataFolder(), "config.yml");
		try {
			String oldFile = "";
			if (new File(plugin.getDataFolder(), "config.yml.old").exists()) {
				for (int i = 1; i > 0; i++) {
					if (!(new File(plugin.getDataFolder(), "config.yml.old" + i).exists())) {
						confF.renameTo(new File(plugin.getDataFolder(), "config.yml.old" + i));
						oldFile = "config.yml.old" + i;
						break;
					}
				}
			} else {
				confF.renameTo(new File(plugin.getDataFolder(), "config.yml.old"));
				oldFile = "config.yml.old";
			}
			try {
				if (!confF.exists()) {
					try (InputStream configin = plugin.getClass().getResourceAsStream("/bungeeconfig.yml")) {
						Files.copy(configin, confF.toPath());
					}
				}
			} catch (IOException e1) {
				plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("Error while updating config!").color(ChatColor.RED).create());
				e1.printStackTrace();
				return false;
			}
			plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("Config is up to date! Old config file renamed to " + oldFile + ".").color(ChatColor.GREEN).create());
			return true;
		} catch (Exception e) {
			plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("Error while updating config!").color(ChatColor.RED).create());
			return false;
		}
	}

	protected boolean updateConfig() {
		if (!plugin.getDataFolder().exists())
			plugin.getDataFolder().mkdirs();
		File confF = new File(plugin.getDataFolder(), "config.yml");
		InputStream configin = plugin.getResourceAsStream("bungeeconfig.yml"); 
		BufferedReader configreader = new BufferedReader(new InputStreamReader(configin));
		Configuration configc = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configreader);
		int configcj = configc.getInt("Version");
		try {
			if (!confF.exists()) {
				Files.copy(configin, confF.toPath());
			}
		} catch (IOException e1) {
			plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("Error while updating config!").color(ChatColor.RED).create());
			e1.printStackTrace();
			return false;
		}
		try {
			configreader.close();
			configin.close();
		} catch (IOException e1) {
			plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("Error while updating config!").color(ChatColor.RED).create());
			e1.printStackTrace();
			return false;
		}
		try {
			Configuration conf = ConfigurationProvider.getProvider(YamlConfiguration.class).load(confF);
			if (conf.getInt("Version") != configcj) {
				try {
					String oldFile = "";
					if (new File(plugin.getDataFolder(), "config.yml.old").exists()) {
						for (int i = 1; i > 0; i++) {
							if (!(new File(plugin.getDataFolder(), "config.yml.old" + i).exists())) {
								confF.renameTo(new File(plugin.getDataFolder(), "config.yml.old" + i));
								oldFile = "config.yml.old" + i;
								break;
							}
						}
					} else {
						confF.renameTo(new File(plugin.getDataFolder(), "config.yml.old"));
						oldFile = "config.yml.old";
					}
					try {
						try (InputStream configis = plugin.getClass().getResourceAsStream("/bungeeconfig.yml")) {
							Files.copy(configis, confF.toPath());
						}
						plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("Config is up to date! Old config file renamed to " + oldFile + ".").color(ChatColor.GREEN).create());
						return true;
					} catch (IOException e1) {
						plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("Error while updating config!").color(ChatColor.RED).create());
						e1.printStackTrace();
						return false;
					}
				} catch (Exception e) {
					plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("Error while updating config!").color(ChatColor.RED).create());
					return false;
				}
			}
		} catch (IOException e1) {
			plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("Error while updating config!").color(ChatColor.RED).create());
			e1.printStackTrace();
			return false;
		}
		plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("Config is up to date!").color(ChatColor.GREEN).create());
		return true;
	}
	
	protected static String getString(Configuration conf, String key, String def) {
		if (conf.contains(key))
			return conf.getString(key);
		else {
			ProxyServer.getInstance().getConsole().sendMessage(new ComponentBuilder("Invalid config option for " + key + "!").color(ChatColor.RED).create());
			return def;
		}
	}
	
	protected static boolean getBoolean(Configuration conf, String key, boolean def) {
		if (conf.contains(key))
			return conf.getBoolean(key);
		else {
			ProxyServer.getInstance().getConsole().sendMessage(new ComponentBuilder("Invalid config option for " + key + "!").color(ChatColor.RED).create());
			return def;
		}
	}
	
	protected static int getInt(Configuration conf, String key, int def) {
		if (conf.contains(key))
			return conf.getInt(key);
		else {
			ProxyServer.getInstance().getConsole().sendMessage(new ComponentBuilder("Invalid config option for " + key + "!").color(ChatColor.RED).create());
			return def;
		}
	}
	
	protected static double getDouble(Configuration conf, String key, double def) {
		if (conf.contains(key))
			return conf.getDouble(key);
		else {
			ProxyServer.getInstance().getConsole().sendMessage(new ComponentBuilder("Invalid config option for " + key + "!").color(ChatColor.RED).create());
			return def;
		}
	}
	
	protected static long getLong(Configuration conf, String key, long def) {
		if (conf.contains(key))
			return conf.getLong(key);
		else {
			ProxyServer.getInstance().getConsole().sendMessage(new ComponentBuilder("Invalid config option for " + key + "!").color(ChatColor.RED).create());
			return def;
		}
	}
	
	protected static char getColor(Configuration conf, String key, char def) {
		if (conf.contains(key))
			return conf.getChar(key);
		else {
			ProxyServer.getInstance().getConsole().sendMessage(new ComponentBuilder("Invalid config option for " + key + "!").color(ChatColor.RED).create());
			return def;
		}
	}
	
	protected static float getFloat(Configuration conf, String key, float def) {
		if (conf.contains(key))
			return conf.getFloat(key);
		else {
			ProxyServer.getInstance().getConsole().sendMessage(new ComponentBuilder("Invalid config option for " + key + "!").color(ChatColor.RED).create());
			return def;
		}
	}
	
	protected static byte getByte(Configuration conf, String key, byte def) {
		if (conf.contains(key))
			return conf.getByte(key);
		else {
			ProxyServer.getInstance().getConsole().sendMessage(new ComponentBuilder("Invalid config option for " + key + "!").color(ChatColor.RED).create());
			return def;
		}
	}
	
	protected static short getShort(Configuration conf, String key, short def) {
		if (conf.contains(key))
			return conf.getShort(key);
		else {
			ProxyServer.getInstance().getConsole().sendMessage(new ComponentBuilder("Invalid config option for " + key + "!").color(ChatColor.RED).create());
			return def;
		}
	}
	
	protected static String getColorCodes(Configuration conf, String key, String def) {
		if (conf.contains(key)) {
			String val = conf.getString(key);
			String[] colors = val.split(",");
			StringBuilder sb = new StringBuilder();
			for (String s : colors) {
				try {
					if (s.length() == 1) {
						sb.append(ChatColor.getByChar(s.charAt(0)));
					} else if (s.length() == 2 && (s.charAt(0) == '&' || s.charAt(0) == '§')) {
						sb.append(ChatColor.getByChar(s.charAt(1)));
					} else {
						sb.append(Enum.valueOf(ChatColor.class, s.toUpperCase()));
					}
				} catch (Exception e) {
					ProxyServer.getInstance().getConsole().sendMessage(new ComponentBuilder("Invalid config option for " + key + "!").color(ChatColor.RED).create());
					return def;
				}
			}
			return sb.toString();
		} else {
			ProxyServer.getInstance().getConsole().sendMessage(new ComponentBuilder("Invalid config option for " + key + "!").color(ChatColor.RED).create());
			return def;
		}
	}
	
	protected static Object getObject(Configuration conf, String key, Object def) {
		if (conf.contains(key))
			return conf.get(key);
		else {
			ProxyServer.getInstance().getConsole().sendMessage(new ComponentBuilder("Invalid config option for " + key + "!").color(ChatColor.RED).create());
			return def;
		}
	}

	protected static HashMap<String, String> getServerList() {
		return serverList;
	}
	
	protected static String getServerName(String server) {
		return serverList.containsKey(server) ? serverList.get(server) : server;
	}

	protected static HashMap<String, Backend> getBackends() {
		return backends;
	}
	
	protected Config(Plugin plugin) {
		this.plugin = plugin;
	}

	public enum ConsoleFormat {
		BOTTOM, TOP;
	}

}
