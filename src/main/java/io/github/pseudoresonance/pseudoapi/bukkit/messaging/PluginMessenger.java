package io.github.pseudoresonance.pseudoapi.bukkit.messaging;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import io.github.pseudoresonance.pseudoapi.bukkit.PluginChannelListener;
import io.github.pseudoresonance.pseudoapi.bukkit.PseudoAPI;

public class PluginMessenger implements PluginMessageListener {
	
	public static PluginChannelListener pcl;
	private static HashMap<Player, Object> obj = new  HashMap<Player, Object>();
	
	public static void enable() {
		Bukkit.getMessenger().registerOutgoingPluginChannel(PseudoAPI.plugin, "BungeeCord");
		Bukkit.getMessenger().registerIncomingPluginChannel(PseudoAPI.plugin, "Return", pcl = new PluginChannelListener());
	}
	
	public synchronized void onPluginMessageReceived(String channel, Player p, byte[] message) {
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
		try {
			String subchannel = in.readUTF();
			if (subchannel.equals("get")) {
				String input = in.readUTF();
				obj.put(p, input);
				notifyAll();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized Object get(Player p, boolean integer) {
		sendToBungee(p, "get", integer ? "points" : "nickname");
		try {
			wait();
		} catch (InterruptedException e) {}
		return obj.get(p);
	}
	
	public void sendToBungee(Player p, String channel, String sub) {
		ByteArrayOutputStream b = new  ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		try {
			out.writeUTF(channel);
			out.writeUTF(sub);
		} catch (IOException e) {
			e.printStackTrace();
		}
		p.sendPluginMessage(PseudoAPI.plugin, "BungeeCord", b.toByteArray());
	}

}
