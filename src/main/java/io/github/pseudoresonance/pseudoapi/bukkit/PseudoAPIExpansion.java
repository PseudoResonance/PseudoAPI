package io.github.pseudoresonance.pseudoapi.bukkit;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import io.github.pseudoresonance.pseudoapi.bukkit.data.Data;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PseudoAPIExpansion extends PlaceholderExpansion {
	
	private Plugin plugin;
	
	public PseudoAPIExpansion(Plugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean canRegister() {
		return true;
	}

	@Override
	public boolean persist() {
		return true;
	}

	@Override
	public String getAuthor() {
		return plugin.getDescription().getAuthors().toString();
	}

	@Override
	public String getIdentifier() {
		return plugin.getName().toLowerCase();
	}

	@Override
	public String getVersion() {
		return plugin.getDescription().getVersion();
	}

	@Override
	public String onPlaceholderRequest(Player player, String identifier) {
		switch (identifier) {
		case "plugins_loaded":
			return String.valueOf(PluginController.getPlugins().length);
		case "global_backend":
			return Data.getGlobalBackend().getName();
		case "server_backend":
			return Data.getServerBackend().getName();
		default:
			return "";
		}
	}

}
