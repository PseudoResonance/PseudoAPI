package io.github.pseudoresonance.pseudoapi.bukkit.messaging;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import io.github.pseudoresonance.pseudoapi.bukkit.PseudoAPI;
import io.github.pseudoresonance.pseudoapi.bukkit.PseudoPlugin;

public class PluginMessenger implements PluginMessageListener {

	public static final String channelBungeeName = "pseudoapi:bungee";
	public static final String channelServerName = "pseudoapi:servers";
	
	private static final ArrayList<PluginMessengerListener> listeners = new ArrayList<PluginMessengerListener>();
	private static final HashMap<PseudoPlugin, ArrayList<PluginMessengerListener>> pluginListeners = new HashMap<PseudoPlugin, ArrayList<PluginMessengerListener>>();

	public static void enable() {
		Bukkit.getMessenger().registerOutgoingPluginChannel(PseudoAPI.plugin, channelBungeeName);
		Bukkit.getMessenger().registerIncomingPluginChannel(PseudoAPI.plugin, channelServerName, new PluginMessenger());
	}
	
	public static void registerListener(PseudoPlugin plugin, PluginMessengerListener listener) {
		ArrayList<PluginMessengerListener> listenerList = null;
		if (pluginListeners.containsKey(plugin)) {
			listenerList = pluginListeners.get(plugin);
		} else {
			listenerList = new ArrayList<PluginMessengerListener>();
		}
		listenerList.add(listener);
		pluginListeners.put(plugin, listenerList);
		listeners.add(listener);
	}
	
	public static void unregisterPluginListeners(PseudoPlugin plugin) {
		if (pluginListeners.containsKey(plugin)) {
			ArrayList<PluginMessengerListener> listenerList = pluginListeners.get(plugin);
			for (PluginMessengerListener l : listenerList) {
				listeners.remove(l);
			}
			pluginListeners.remove(plugin);
		}
	}

	public void onPluginMessageReceived(String channel, Player p, byte[] message) {
		if (channel.equals(channelServerName)) {
			ByteArrayDataInput in = ByteStreams.newDataInput(message);
			String subchannel = in.readUTF();
			for (PluginMessengerListener listener : listeners) {
				listener.onMessageReceived(p, subchannel, message);
			}
		}
	}

	public static void sendToBungee(Player p, String channel, Collection<Object> data) {
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
				else if (o instanceof String)
					out.writeUTF((String) o);
			}
		}
		p.sendPluginMessage(PseudoAPI.plugin, channelBungeeName, out.toByteArray());
	}

}
