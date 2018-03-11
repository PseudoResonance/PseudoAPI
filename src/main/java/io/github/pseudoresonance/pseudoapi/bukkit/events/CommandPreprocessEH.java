package io.github.pseudoresonance.pseudoapi.bukkit.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandPreprocessEH implements Listener {

	@EventHandler
	public void commandPreprocess(PlayerCommandPreprocessEvent e) {
		if (e.getMessage().toLowerCase().equals("/pl") || e.getMessage().toLowerCase().equals("/plugins")) {
			e.getPlayer().chat("/PseudoAPI:plugins");
			e.setCancelled(true);
		}
	}

}
