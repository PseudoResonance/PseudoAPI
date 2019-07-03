package io.github.pseudoresonance.pseudoapi.bukkit.data;

import org.bukkit.Bukkit;

import io.github.pseudoresonance.pseudoapi.bukkit.Config;
import io.github.pseudoresonance.pseudoapi.bukkit.Message;
import io.github.pseudoresonance.pseudoapi.bukkit.PseudoAPI;
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
			Message.sendConsoleMessage(ChatColor.RED + "No global backend selected! Disabling PseudoAPI!");
			Bukkit.getServer().getPluginManager().disablePlugin(PseudoAPI.plugin);
		}
		if (serverBackend == null) {
			Message.sendConsoleMessage(ChatColor.RED + "No server backend selected! Using global backend!");
			serverBackend = globalBackend;
		}
		PseudoAPI.reloadAll();
	}

}
