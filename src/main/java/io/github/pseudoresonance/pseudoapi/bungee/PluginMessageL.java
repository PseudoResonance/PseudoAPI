package io.github.pseudoresonance.pseudoapi.bungee;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
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
		if (e.getTag().equals(PluginMessenger.channelBungeeName)) {
			byte[] data = e.getData();
			ByteArrayDataInput in = ByteStreams.newDataInput(data);
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
			} else if (subChannel.equals("ping")) {
				String uuid = in.readUTF();
				ProxiedPlayer p = ProxyServer.getInstance().getPlayer(UUID.fromString(uuid));
				if (p != null) {
					ArrayList<Object> toSend = new ArrayList<Object>();
					toSend.add(p.getUniqueId().toString());
					toSend.add(p.getPing());
					toSend.add(in.readUTF());
					sendToServer(p, "ping", toSend);
				}
			}
		}
	}

	/**
	 * Send message over plugin messaging channel to Bukkit/Spigot
	 * 
	 * @param p Player to send message with
	 * @param channel Channel to send message on
	 * @param data Data to send
	 */
	public void sendToServer(ProxiedPlayer p, String channel, Collection<Object> data) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF(channel);
		if (data != null) {
			for (Object o : data) {
				if (o instanceof Boolean)
					out.writeBoolean((boolean) o);
				else if (o instanceof Byte)
					out.writeByte((byte) o);
				else if (o instanceof Character)
					out.writeChar((char) o);
				else if (o instanceof Double)
					out.writeDouble((double) o);
				else if (o instanceof Float)
					out.writeFloat((float) o);
				else if (o instanceof Integer)
					out.writeInt((int) o);
				else if (o instanceof Long)
					out.writeLong((long) o);
				else if (o instanceof Short)
					out.writeShort((short) o);
				else
					out.writeUTF(o.toString());
			}
		}
		p.getServer().getInfo().sendData(PluginMessenger.channelServerName, out.toByteArray());
	}

}
