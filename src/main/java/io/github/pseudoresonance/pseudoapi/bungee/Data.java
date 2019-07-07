package io.github.pseudoresonance.pseudoapi.bungee;

import io.github.pseudoresonance.pseudoapi.bukkit.data.Backend;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class Data {

	private static Backend backend = null;
	
	protected static Backend getBackend() {
		return backend;
	}
	
	protected static void stopBackends() {
		if (backend != null)
			backend.stop();
	}
	
	protected static void loadBackends() {
		for (String b : Config.getBackends().keySet()) {
			if (b.equals(Config.backend)) {
				backend = Config.getBackends().get(b);
				backend.setup();
			}
		}
		if (backend == null) {
			PseudoAPI.plugin.getProxy().getConsole().sendMessage(new ComponentBuilder("No backend selected! Disabling PseudoAPI!").color(ChatColor.RED).create());
			PseudoAPI.plugin.onDisable();
		}
	}

}
