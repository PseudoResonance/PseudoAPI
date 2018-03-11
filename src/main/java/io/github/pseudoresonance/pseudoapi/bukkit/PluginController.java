package io.github.pseudoresonance.pseudoapi.bukkit;

import java.util.ArrayList;
import java.util.List;

public class PluginController {
	
	private static List<PseudoPlugin> plugins = new ArrayList<PseudoPlugin>();
	
	protected static void pluginLoaded(PseudoPlugin plugin) {
		plugins.add(plugin);
	}
	
	public static PseudoPlugin[] getPlugins() {
		return plugins.toArray(new PseudoPlugin[plugins.size()]);
	}

}
