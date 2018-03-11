package io.github.pseudoresonance.pseudoapi.bukkit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import io.github.pseudoresonance.pseudoapi.bukkit.Message.Errors;

public class PluginChannelListener implements PluginMessageListener {

	private static HashMap<Player, Object> obj = new HashMap<Player, Object>();

	@Override
	public synchronized void onPluginMessageReceived(String channel, Player player, byte[] message) {
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
		try {
			String subchannel = in.readUTF();
			if (subchannel.equals("PseudoAPI")) {
				String input = in.readUTF();
				obj.put(player, input);

				notifyAll();
			}
		} catch (IOException e) {
			PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "Error while reading data from BungeeCord server!");
		}
	}

	public void sendToBungeeCord(Player p, String channel, String sub, String data) {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		try {
			out.writeUTF(channel);
			out.writeUTF(sub);
			out.writeUTF(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Bukkit.getServer().sendPluginMessage(PseudoAPI.plugin, "BungeeCord", b.toByteArray());
	}

}
