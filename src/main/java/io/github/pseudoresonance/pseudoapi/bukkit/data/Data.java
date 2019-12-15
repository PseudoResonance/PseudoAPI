package io.github.pseudoresonance.pseudoapi.bukkit.data;

import org.bukkit.Bukkit;

import io.github.pseudoresonance.pseudoapi.bukkit.Config;
import io.github.pseudoresonance.pseudoapi.bukkit.Chat;
import io.github.pseudoresonance.pseudoapi.bukkit.PseudoAPI;
import io.github.pseudoresonance.pseudoapi.bukkit.language.LanguageManager;
import net.md_5.bungee.api.ChatColor;

public class Data {

	private static Backend globalBackend = null;
	private static Backend serverBackend = null;
	
	public static Backend getGlobalBackend() {
		return globalBackend;
	}
	
	public static Backend getServerBackend() {
		return serverBackend;
	}
	
	public static void stopBackends() {
		if (globalBackend != null)
			globalBackend.stop();
		if (serverBackend != null)
			serverBackend.stop();
	}
	
	public static void loadBackends() {
		for (String b : Config.getBackends().keySet()) {
			if (b.equals(Config.globalBackend)) {
				globalBackend = Config.getBackends().get(b);
				globalBackend.setup();
			}
			if (b.equals(Config.serverBackend)) {
				serverBackend = Config.getBackends().get(b);
				serverBackend.setup();
			}
		}
		if (globalBackend == null) {
			Chat.sendConsoleMessage(ChatColor.RED + LanguageManager.getLanguage().getMessage("pseudoapi.no_global_backend"));
			Bukkit.getServer().getPluginManager().disablePlugin(PseudoAPI.plugin);
		}
		if (serverBackend == null) {
			Chat.sendConsoleMessage(ChatColor.RED + LanguageManager.getLanguage().getMessage("pseudoapi.no_server_backend"));
			serverBackend = globalBackend;
		}
		PseudoAPI.reloadAll();
	}

}
