package io.github.pseudoresonance.pseudoapi.bukkit.events;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import io.github.pseudoresonance.pseudoapi.bukkit.DataController;

public class PlayerJoinEH implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		UUID u = p.getUniqueId();
		String uuid = u.toString();
		String name = p.getName();
		DataController.addUUID(uuid, name);
	}
	
	public static void playerJoin(Player p) {
		UUID u = p.getUniqueId();
		String uuid = u.toString();
		String name = p.getName();
		DataController.addUUID(uuid, name);
	}

}
