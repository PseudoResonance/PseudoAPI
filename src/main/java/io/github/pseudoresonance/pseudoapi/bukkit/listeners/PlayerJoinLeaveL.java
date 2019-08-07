package io.github.pseudoresonance.pseudoapi.bukkit.listeners;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import io.github.pseudoresonance.pseudoapi.bukkit.Config;
import io.github.pseudoresonance.pseudoapi.bukkit.PseudoAPI;
import io.github.pseudoresonance.pseudoapi.bukkit.PseudoUpdater;
import io.github.pseudoresonance.pseudoapi.bukkit.playerdata.PlayerDataController;
import io.github.pseudoresonance.pseudoapi.bukkit.playerdata.ServerPlayerDataController;

public class PlayerJoinLeaveL implements Listener {

	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent e) {
		Player p = e.getPlayer();
		UUID u = p.getUniqueId();
		String uuid = u.toString();
		String name = p.getName();
		PlayerDataController.playerJoin(uuid, name);
		ServerPlayerDataController.playerJoin(uuid, name);
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		UUID u = p.getUniqueId();
		String uuid = u.toString();
		String name = p.getName();
		PlayerDataController.playerLeave(uuid, name);
		ServerPlayerDataController.playerLeave(uuid, name);
		if (Config.restartEmpty && PseudoUpdater.shouldRestart()) {
			try {
				Bukkit.getScheduler().scheduleSyncDelayedTask(PseudoAPI.plugin, new Runnable() {
					@Override
					public void run() {
						PseudoUpdater.restartCheck();
					}
				}, 5);
			} catch (IllegalAccessError ex) {}
		}
	}
	
	public static void playerJoin(Player p) {
		UUID u = p.getUniqueId();
		String uuid = u.toString();
		String name = p.getName();
		PlayerDataController.playerJoin(uuid, name);
		ServerPlayerDataController.playerJoin(uuid, name);
	}

}
