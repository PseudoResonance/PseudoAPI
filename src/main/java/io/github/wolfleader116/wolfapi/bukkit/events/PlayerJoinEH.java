package io.github.wolfleader116.wolfapi.bukkit.events;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import io.github.wolfleader116.wolfapi.bukkit.DataController;

public class PlayerJoinEH implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		UUID u = p.getUniqueId();
		String uuid = u.toString();
		String name = p.getName();
		DataController.addUUID(uuid, name);
	}

}
