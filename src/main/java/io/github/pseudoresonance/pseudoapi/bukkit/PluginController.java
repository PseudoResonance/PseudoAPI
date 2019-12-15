package io.github.pseudoresonance.pseudoapi.bukkit;

import java.util.ArrayList;
import java.util.List;

public class PluginController {
	
	private static List<PseudoPlugin> plugins = new ArrayList<PseudoPlugin>();
	
	protected static void pluginLoaded(PseudoPlugin plugin) {
		plugins.add(plugin);
	}
	
	protected static void pluginUnoaded(PseudoPlugin plugin) {
		plugins.remove(plugin);
	}
	
	/**
	 * Returns list of all loaded {@link PseudoPlugin}s
	 * 
	 * @return List of loaded {@link PseudoPlugin}s
	 */
	public static PseudoPlugin[] getPlugins() {
		return plugins.toArray(new PseudoPlugin[plugins.size()]);
	}

}
