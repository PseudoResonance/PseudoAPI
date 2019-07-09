package io.github.pseudoresonance.pseudoapi.bungee;

import java.util.UUID;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import io.github.pseudoresonance.pseudoapi.bukkit.messaging.PluginMessenger;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PluginMessageL implements Listener {

	@EventHandler
	public void onPluginMessage(PluginMessageEvent e) {
		if (e.getTag().equals(PluginMessenger.channelName)) {
			ByteArrayDataInput in = ByteStreams.newDataInput(e.getData());
			String subChannel = in.readUTF();
			if (subChannel.equals("displayname")) {
				String uuid = in.readUTF();
				ProxiedPlayer p = ProxyServer.getInstance().getPlayer(UUID.fromString(uuid));
				if (p != null) {
					String nickname = in.readUTF();
					p.setDisplayName(nickname);
				}
			} else if (subChannel.equals("resetdisplayname")) {
				String uuid = in.readUTF();
				ProxiedPlayer p = ProxyServer.getInstance().getPlayer(UUID.fromString(uuid));
				if (p != null) {
					p.setDisplayName(p.getName());
				}
			}
		}
	}

}
