package io.github.pseudoresonance.pseudoapi.bungee;

import java.util.HashMap;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerL implements Listener {
	
	private static HashMap<String, String> playerServers = new HashMap<String, String>();

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
		if (Config.enableJoinLeave) {
			if (!Config.serverChangeFormat.equals("")) {
				if (playerServers.containsKey(e.getPlayer().getUniqueId().toString())) {
					String format = Config.serverChangeFormat;
					format = format.replace("{name}", e.getPlayer().getName());
					format = format.replace("{nickname}", e.getPlayer().getDisplayName());
					format = format.replace("{uuid}", e.getPlayer().getUniqueId().toString());
					String serverName = e.getServer().getInfo().getName();
					format = format.replace("{server}", serverName);
					format = format.replace("{server_nick}", Config.getServerName(serverName));
					String lastServerName = playerServers.get(e.getPlayer().getUniqueId().toString());
					format = format.replace("{last_server}", lastServerName);
					format = format.replace("{last_server_nick}", Config.getServerName(lastServerName));
					format = ChatColor.translateAlternateColorCodes('&', format);
					ProxyServer.getInstance().broadcast(new ComponentBuilder(format).create());
				}
			}
		}
		playerServers.put(e.getPlayer().getUniqueId().toString(), e.getServer().getInfo().getName());
		ProxyServer.getInstance().getScheduler().runAsync(PseudoAPI.plugin, new Runnable() {
			public void run() {
				PlayerDataController.playerServer(e.getPlayer().getUniqueId().toString(), Config.getServerName(e.getServer().getInfo().getName()));
			}
		});
	}

	@EventHandler
	public void onPlayerDisconnect(PlayerDisconnectEvent e) {
		playerServers.remove(e.getPlayer().getUniqueId().toString());
		if (Config.enableJoinLeave) {
			if (!Config.leaveFormat.equals("")) {
				String format = Config.leaveFormat;
				format = format.replace("{name}", e.getPlayer().getName());
				format = format.replace("{nickname}", e.getPlayer().getDisplayName());
				format = format.replace("{uuid}", e.getPlayer().getUniqueId().toString());
				format = ChatColor.translateAlternateColorCodes('&', format);
				ProxyServer.getInstance().broadcast(new ComponentBuilder(format).create());
			}
		}
		ProxyServer.getInstance().getScheduler().runAsync(PseudoAPI.plugin, new Runnable() {
			public void run() {
				PlayerDataController.playerLeave(e.getPlayer().getUniqueId().toString());
			}
		});
	}

}
