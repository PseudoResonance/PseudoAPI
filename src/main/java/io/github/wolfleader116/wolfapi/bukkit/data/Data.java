package io.github.wolfleader116.wolfapi.bukkit.data;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;

import io.github.wolfleader116.wolfapi.bukkit.Message;
import io.github.wolfleader116.wolfapi.bukkit.WolfAPI;
import net.md_5.bungee.api.ChatColor;

public class Data {
	
	private static Map<String, Backend> backends = new HashMap<String, Backend>();
	private static Backend backend;
	
	public static Backend getBackend() {
		return backend;
	}
	
	public static Map<String, Backend> getBackends() {
		return Data.backends;
	}
	
	public static void loadBackends() {
		Set<String> keys = WolfAPI.plugin.getConfig().getConfigurationSection("Backends").getKeys(false);
		for (String key : keys) {
			try {
				String type = WolfAPI.plugin.getConfig().getString("Backends." + key + ".type");
				Backend backend;
				if (type.equalsIgnoreCase("file")) {
					String location = WolfAPI.plugin.getConfig().getString("Backends." + key + ".file");
					backend = new FileBackend(key, WolfAPI.plugin.getDataFolder(), location);
					backends.put(key, backend);
				} else if (type.equalsIgnoreCase("mysql")) {
					String host = WolfAPI.plugin.getConfig().getString("Backends." + key + ".host");
					int port = WolfAPI.plugin.getConfig().getInt("Backends." + key + ".port");
					String username = WolfAPI.plugin.getConfig().getString("Backends." + key + ".username");
					String password = WolfAPI.plugin.getConfig().getString("Backends." + key + ".password");
					String database = WolfAPI.plugin.getConfig().getString("Backends." + key + ".database");
					String prefix = "";
					if (WolfAPI.plugin.getConfig().contains("Backends." + key + ".prefix")) {
						prefix = WolfAPI.plugin.getConfig().getString("Backends." + key + ".prefix");
					}
					backend = new MysqlBackend(key, host, port, username, password, database, prefix);
					backends.put(key, backend);
				} else if (type.equalsIgnoreCase("sqlite")) {
					String location = WolfAPI.plugin.getConfig().getString("Backends." + key + ".database");
					File file = new File(location);
					String username = WolfAPI.plugin.getConfig().getString("Backends." + key + ".username");
					String password = WolfAPI.plugin.getConfig().getString("Backends." + key + ".password");
					String prefix = "";
					if (WolfAPI.plugin.getConfig().contains("Backends." + key + ".prefix")) {
						prefix = WolfAPI.plugin.getConfig().getString("Backends." + key + ".prefix");
					}
					backend = new SqliteBackend(key, file, username, password, prefix);
					backends.put(key, backend);
				} else {
					Message.sendConsoleMessage(ChatColor.RED + "Invalid backend type for backend: " + key + "!");
					type = "file";
				}
			} catch (Exception e) {
				Message.sendConsoleMessage(ChatColor.RED + "Invalid backend configuration for backend: " + key + "!");
			}
		}
		if (backends.size() == 0) {
			Message.sendConsoleMessage(ChatColor.RED + "No backends configured! Disabling WolfAPI!");
			Bukkit.getServer().getPluginManager().disablePlugin(WolfAPI.plugin);
		}
		String backend = WolfAPI.plugin.getConfig().getString("Backend");
		for (String b : backends.keySet()) {
			if (b.equals(backend)) {
				Data.backend = backends.get(b);
			}
		}
		if (Data.backend == null) {
			Message.sendConsoleMessage(ChatColor.RED + "No backend selected! Disabling WolfAPI!");
			Bukkit.getServer().getPluginManager().disablePlugin(WolfAPI.plugin);
		}
		WolfAPI.updateAll();
	}

}
