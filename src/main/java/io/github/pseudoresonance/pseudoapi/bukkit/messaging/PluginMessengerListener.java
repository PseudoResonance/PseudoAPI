package io.github.pseudoresonance.pseudoapi.bukkit.messaging;

import org.bukkit.entity.Player;

public interface PluginMessengerListener {
	
	public void onMessageReceived(Player p, String subchannel, byte[] data);

}
