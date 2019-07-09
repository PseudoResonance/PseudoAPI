package io.github.pseudoresonance.pseudoapi.bukkit;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import io.github.pseudoresonance.pseudoapi.bukkit.data.Backend;
import io.github.pseudoresonance.pseudoapi.bukkit.data.Data;
import io.github.pseudoresonance.pseudoapi.bukkit.data.FileBackend;
import io.github.pseudoresonance.pseudoapi.bukkit.data.MySQLBackend;
import io.github.pseudoresonance.pseudoapi.bukkit.data.PluginConfig;
import io.github.pseudoresonance.pseudoapi.bukkit.utils.ChatComponent.ComponentType;
import net.md_5.bungee.api.ChatColor;

public class Config extends PluginConfig {

	public static boolean hidePlugins = true;
	public static boolean showPseudoAPI = true;

	public static String globalBackend = "globalfile";
	public static String serverBackend = "file";
	private static HashMap<String, Backend> backends = new HashMap<String, Backend>();

	public static boolean bungeeEnabled = false;

	public static String borderColor = "&3";
	public static String titleColor = "&6";
	public static String commandColor = "&c";
	public static String descriptionColor = "&b";
	public static String textColor = "&a";
	public static String errorTextColor = "&c";
	public static String messageFormat = "&9&l{nickname}> &a{message}";
	public static String errorMessageFormat = "&9&l{nickname}> &c{message}";
	public static ComponentType clickEvent = ComponentType.RUN_COMMAND;
	public static ConsoleFormat consoleFormat = ConsoleFormat.BOTTOM;

	public static boolean startupUpdate = true;
	public static int startupDelay = 60;
	public static int updateFrequency = 60;
	public static boolean downloadUpdates = true;
	public static boolean updateRestart = true;
	public static boolean restartEmpty = true;
	public static int restartWarning = 60;

	public void reloadConfig() {
		FileConfiguration fc = PseudoAPI.plugin.getConfig();
		hidePlugins = PluginConfig.getBoolean(fc, "HidePlugins", hidePlugins);
		showPseudoAPI = PluginConfig.getBoolean(fc, "ShowPseudoAPI", showPseudoAPI);

		globalBackend = PluginConfig.getString(fc, "GlobalBackend", globalBackend);
		serverBackend = PluginConfig.getString(fc, "ServerBackend", serverBackend);

		Data.stopBackends();
		Set<String> keys = PseudoAPI.plugin.getConfig().getConfigurationSection("Backends").getKeys(false);
		for (String key : keys) {
			try {
				String type = fc.getString("Backends." + key + ".type");
				if (type.equalsIgnoreCase("file")) {
					String location = PseudoAPI.plugin.getConfig().getString("Backends." + key + ".directory");
					Path path = Paths.get(location);
					File dir;
					if (path.isAbsolute()) {
						dir = path.toFile();
					} else {
						dir = new File(PseudoAPI.plugin.getDataFolder(), location);
					}
					FileBackend backend = new FileBackend(key, dir);
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
					MySQLBackend backend = new MySQLBackend(key, host, port, username, password, database, prefix, useSSL, verifyServerCertificate, requireSSL);
					backends.put(key, backend);
				} else {
					Message.sendConsoleMessage(ChatColor.RED + "Invalid backend type for backend: " + key + "!");
				}
			} catch (Exception e) {
				Message.sendConsoleMessage(ChatColor.RED + "Invalid backend configuration for backend: " + key + "!");
			}
		}
		if (backends.size() == 0) {
			Message.sendConsoleMessage(ChatColor.RED + "No backends configured! Disabling PseudoAPI!");
			Bukkit.getServer().getPluginManager().disablePlugin(PseudoAPI.plugin);
		}

		bungeeEnabled = PluginConfig.getBoolean(fc, "BungeeEnabled", bungeeEnabled);

		borderColor = PluginConfig.getColorCodes(fc, "BorderColor", borderColor);
		titleColor = PluginConfig.getColorCodes(fc, "TitleColor", titleColor);
		commandColor = PluginConfig.getColorCodes(fc, "CommandColor", commandColor);
		descriptionColor = PluginConfig.getColorCodes(fc, "DescriptionColor", descriptionColor);
		textColor = PluginConfig.getColorCodes(fc, "TextColor", textColor);
		errorTextColor = PluginConfig.getColorCodes(fc, "ErrorTextColor", errorTextColor);
		messageFormat = PluginConfig.getString(fc, "MessageFormat", messageFormat);
		errorMessageFormat = PluginConfig.getString(fc, "ErrorMessageFormat", errorMessageFormat);
		String clickEvent = PluginConfig.getString(fc, "ClickEvent", Config.clickEvent.toString());
		if (clickEvent.equalsIgnoreCase("suggest") || clickEvent.equalsIgnoreCase("suggest_command"))
			Config.clickEvent = ComponentType.SUGGEST_COMMAND;
		else if (clickEvent.equalsIgnoreCase("run") || clickEvent.equalsIgnoreCase("run_command"))
			Config.clickEvent = ComponentType.RUN_COMMAND;
		else {
			Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for ClickEvent!");
		}
		String consoleFormat = PluginConfig.getString(fc, "ConsoleFormat", Config.consoleFormat.toString());
		if (consoleFormat.equalsIgnoreCase("bottom"))
			Config.consoleFormat = ConsoleFormat.BOTTOM;
		else if (consoleFormat.equalsIgnoreCase("top"))
			Config.consoleFormat = ConsoleFormat.TOP;
		else {
			Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for ConsoleFormat!");
		}

		startupUpdate = PluginConfig.getBoolean(fc, "StartupUpdate", startupUpdate);
		startupDelay = PluginConfig.getInt(fc, "StartupDelay", startupDelay);
		updateFrequency = PluginConfig.getInt(fc, "UpdateFrequency", updateFrequency);
		downloadUpdates = PluginConfig.getBoolean(fc, "DownloadUpdates", downloadUpdates);
		updateRestart = PluginConfig.getBoolean(fc, "UpdateRestart", updateRestart);
		restartEmpty = PluginConfig.getBoolean(fc, "RestartEmpty", restartEmpty);
		restartWarning = PluginConfig.getInt(fc, "RestartWarning", restartWarning);

		Data.loadBackends();
	}

	public Config(PseudoPlugin plugin) {
		super(plugin);
	}

	public static HashMap<String, Backend> getBackends() {
		return backends;
	}

	public enum ConsoleFormat {
		BOTTOM, TOP;
	}

}
