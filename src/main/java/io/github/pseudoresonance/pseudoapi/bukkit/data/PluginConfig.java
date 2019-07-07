package io.github.pseudoresonance.pseudoapi.bukkit.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import io.github.pseudoresonance.pseudoapi.bukkit.Message;
import io.github.pseudoresonance.pseudoapi.bukkit.PseudoPlugin;
import net.md_5.bungee.api.ChatColor;

public abstract class PluginConfig {

	private PseudoPlugin plugin;

	public PluginConfig(PseudoPlugin plugin) {
		this.plugin = plugin;
	}

	public boolean updateConfig() {
		InputStream configin = plugin.getClass().getResourceAsStream("/config.yml");
		BufferedReader configreader = new BufferedReader(new InputStreamReader(configin));
		YamlConfiguration configc = YamlConfiguration.loadConfiguration(configreader);
		int configcj = configc.getInt("Version");
		try {
			configreader.close();
			configin.close();
		} catch (IOException e1) {
			Message.sendConsoleMessage(ChatColor.RED + "Error while updating config!");
			e1.printStackTrace();
			return false;
		}
		if (plugin.getConfig().getInt("Version") != configcj) {
			try {
				String oldFile = "";
				File conf = new File(plugin.getDataFolder(), "config.yml");
				if (new File(plugin.getDataFolder(), "config.yml.old").exists()) {
					for (int i = 1; i > 0; i++) {
						if (!(new File(plugin.getDataFolder(), "config.yml.old" + i).exists())) {
							conf.renameTo(new File(plugin.getDataFolder(), "config.yml.old" + i));
							oldFile = "config.yml.old" + i;
							break;
						}
					}
				} else {
					conf.renameTo(new File(plugin.getDataFolder(), "config.yml.old"));
					oldFile = "config.yml.old";
				}
				plugin.saveDefaultConfig();
				plugin.reloadConfig();
				Message.sendConsoleMessage(ChatColor.GREEN + "Config is up to date! Old config file renamed to " + oldFile + ".");
				return true;
			} catch (Exception e) {
				Message.sendConsoleMessage(ChatColor.RED + "Error while updating config!");
				return false;
			}
		}
		Message.sendConsoleMessage(ChatColor.GREEN + "Config is up to date!");
		return true;
	}

	public abstract void reloadConfig();

	public static String getString(FileConfiguration fc, String key, String def) {
		if (fc.contains(key))
			return fc.getString(key);
		else {
			Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for " + key + "!");
			return def;
		}
	}

	public static boolean getBoolean(FileConfiguration fc, String key, boolean def) {
		if (fc.contains(key))
			return fc.getBoolean(key);
		else {
			Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for " + key + "!");
			return def;
		}
	}

	public static int getInt(FileConfiguration fc, String key, int def) {
		if (fc.contains(key))
			return fc.getInt(key);
		else {
			Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for " + key + "!");
			return def;
		}
	}

	public static double getDouble(FileConfiguration fc, String key, double def) {
		if (fc.contains(key))
			return fc.getDouble(key);
		else {
			Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for " + key + "!");
			return def;
		}
	}

	public static long getLong(FileConfiguration fc, String key, long def) {
		if (fc.contains(key))
			return fc.getLong(key);
		else {
			Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for " + key + "!");
			return def;
		}
	}

	public static Color getColor(FileConfiguration fc, String key, Color def) {
		if (fc.contains(key))
			return fc.getColor(key);
		else {
			Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for " + key + "!");
			return def;
		}
	}

	public static float getFloat(FileConfiguration fc, String key, float def) {
		if (fc.contains(key))
			return (float) fc.get(key);
		else {
			Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for " + key + "!");
			return def;
		}
	}

	public static String getColorCodes(FileConfiguration fc, String key, String def) {
		if (fc.contains(key)) {
			String val = fc.getString(key);
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
					Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for " + key + "!");
					return def;
				}
			}
			return sb.toString();
		} else {
			Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for " + key + "!");
			return def;
		}
	}

	public static Material getMaterial(FileConfiguration fc, String key, Material def) {
		if (fc.contains(key)) {
			Material mat = Material.getMaterial((String) fc.get(key));
			if (mat != null)
				return mat;
			else {
				Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for " + key + "!");
				return def;
			}
		} else {
			Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for " + key + "!");
			return def;
		}
	}

	public static Object getObject(FileConfiguration fc, String key, Object def) {
		if (fc.contains(key))
			return fc.get(key);
		else {
			Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for " + key + "!");
			return def;
		}
	}

}
