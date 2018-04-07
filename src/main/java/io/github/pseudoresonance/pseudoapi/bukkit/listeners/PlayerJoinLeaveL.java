package io.github.pseudoresonance.pseudoapi.bukkit.listeners;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import io.github.pseudoresonance.pseudoapi.bukkit.Utils;
import io.github.pseudoresonance.pseudoapi.bukkit.playerdata.PlayerDataController;

public class PlayerJoinLeaveL implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		UUID u = p.getUniqueId();
		String uuid = u.toString();
		String name = p.getName();
		PlayerDataController.playerJoin(uuid, name);
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		UUID u = p.getUniqueId();
		String uuid = u.toString();
		String name = p.getName();
		Utils.playerLogout(name);
		PlayerDataController.playerLeave(uuid, name);
	}
	
	public static void playerJoin(Player p) {
		UUID u = p.getUniqueId();
		String uuid = u.toString();
		String name = p.getName();
		PlayerDataController.playerJoin(uuid, name);
	}

}
