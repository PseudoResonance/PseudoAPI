package io.github.pseudoresonance.pseudoapi.bungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerL implements Listener {

	@EventHandler
	public void onPlayerConnect(PostLoginEvent e) {
		ProxyServer.getInstance().getScheduler().runAsync(PseudoAPI.plugin, new Runnable() {
			public void run() {
				PlayerDataController.playerJoin(e.getPlayer());
			}
		});
	}

	@EventHandler
	public void onServerConnected(ServerConnectedEvent e) {
		ProxyServer.getInstance().getScheduler().runAsync(PseudoAPI.plugin, new Runnable() {
			public void run() {
				PlayerDataController.playerServer(e.getPlayer().getUniqueId().toString(), Config.getServerName(e.getServer().getInfo().getName()));
			}
		});
	}

	@EventHandler
	public void onPlayerDisconnect(PlayerDisconnectEvent e) {
		ProxyServer.getInstance().getScheduler().runAsync(PseudoAPI.plugin, new Runnable() {
			public void run() {
				PlayerDataController.playerLeave(e.getPlayer().getUniqueId().toString());
			}
		});
	}

}
