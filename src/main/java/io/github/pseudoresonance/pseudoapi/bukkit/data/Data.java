package io.github.pseudoresonance.pseudoapi.bukkit.data;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.Bukkit;

import io.github.pseudoresonance.pseudoapi.bukkit.Message;
import io.github.pseudoresonance.pseudoapi.bukkit.PseudoAPI;
import net.md_5.bungee.api.ChatColor;

public class Data {
	
	private static HashMap<String, Backend> backends = new HashMap<String, Backend>();
	private static Backend backend;
	
	public static Backend getBackend() {
		return backend;
	}
	
	public static HashMap<String, Backend> getBackends() {
		return Data.backends;
	}
	
	public static void loadBackends() {
		if (backend != null)
			if (backend instanceof SQLBackend)
				((SQLBackend) backend).stop();
		Set<String> keys = PseudoAPI.plugin.getConfig().getConfigurationSection("Backends").getKeys(false);
		for (String key : keys) {
			try {
				String type = PseudoAPI.plugin.getConfig().getString("Backends." + key + ".type");
				Backend backend;
				if (type.equalsIgnoreCase("file")) {
					String location = PseudoAPI.plugin.getConfig().getString("Backends." + key + ".directory");
					Path pDir = Paths.get(location);
					File dir;
					if (pDir.isAbsolute()) {
						dir = pDir.toFile();
					} else {
						dir = new File(PseudoAPI.plugin.getDataFolder(), location);
					}
					backend = new FileBackend(key, dir);
					backends.put(key, backend);
				} else if (type.equalsIgnoreCase("mysql")) {
					String host = PseudoAPI.plugin.getConfig().getString("Backends." + key + ".host");
					int port = PseudoAPI.plugin.getConfig().getInt("Backends." + key + ".port");
					String username = PseudoAPI.plugin.getConfig().getString("Backends." + key + ".username");
					String password = PseudoAPI.plugin.getConfig().getString("Backends." + key + ".password");
					String database = PseudoAPI.plugin.getConfig().getString("Backends." + key + ".database");
					boolean useSSL = PseudoAPI.plugin.getConfig().getBoolean("Backends." + key + ".useSSL");
					boolean verifyServerCertificate = PseudoAPI.plugin.getConfig().getBoolean("Backends." + key + ".verifyServerCertificate");
					boolean requireSSL = PseudoAPI.plugin.getConfig().getBoolean("Backends." + key + ".requireSSL");
					String prefix = "";
					if (PseudoAPI.plugin.getConfig().contains("Backends." + key + ".prefix")) {
						prefix = PseudoAPI.plugin.getConfig().getString("Backends." + key + ".prefix");
					}
					backend = new MySQLBackend(key, host, port, username, password, database, prefix, useSSL, verifyServerCertificate, requireSSL);
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
			Message.sendConsoleMessage(ChatColor.RED + "No backends configured! Disabling PseudoAPI!");
			Bukkit.getServer().getPluginManager().disablePlugin(PseudoAPI.plugin);
		}
		String backend = PseudoAPI.plugin.getConfig().getString("Backend");
		for (String b : backends.keySet()) {
			if (b.equals(backend)) {
				Backend back = backends.get(b);
				Data.backend = back;
				if (back instanceof SQLBackend)
					((SQLBackend) back).setup();
			}
		}
		if (Data.backend == null) {
			Message.sendConsoleMessage(ChatColor.RED + "No backend selected! Disabling PseudoAPI!");
			Bukkit.getServer().getPluginManager().disablePlugin(PseudoAPI.plugin);
		}
		PseudoAPI.updateAll();
	}

}
