package io.github.wolfleader116.wolfapi.bukkit;

import java.util.ArrayList;
import java.util.List;

public class PluginController {
	
	private static List<WolfPlugin> plugins = new ArrayList<WolfPlugin>();
	
	protected static void pluginLoaded(WolfPlugin plugin) {
		plugins.add(plugin);
	}
	
	public static WolfPlugin[] getPlugins() {
		return plugins.toArray(new WolfPlugin[plugins.size()]);
	}

}
