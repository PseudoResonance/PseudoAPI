package io.github.pseudoresonance.pseudoapi.bukkit.listeners;

import java.io.UnsupportedEncodingException;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import io.github.pseudoresonance.pseudoapi.bukkit.Utils;

public class ClientBrandL implements PluginMessageListener {
	
	@Override
	public void onPluginMessageReceived(String channel, Player p, byte[] msg) {
		try {
			Utils.playerLogin(p.getName(), new String(msg, "UTF-8").substring(1));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

}
