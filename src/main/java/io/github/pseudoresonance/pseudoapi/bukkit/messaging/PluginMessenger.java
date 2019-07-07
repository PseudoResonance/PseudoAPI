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

	private static final String channelName = "pseudoapi:channel";
	
	private static final ArrayList<PluginMessageListener> listeners = new ArrayList<PluginMessageListener>();
	private static final HashMap<PseudoPlugin, ArrayList<PluginMessageListener>> pluginListeners = new HashMap<PseudoPlugin, ArrayList<PluginMessageListener>>();

	public static void enable() {
		Bukkit.getMessenger().registerOutgoingPluginChannel(PseudoAPI.plugin, channelName);
		Bukkit.getMessenger().registerIncomingPluginChannel(PseudoAPI.plugin, channelName, new PluginMessenger());
	}
	
	public static void registerListener(PseudoPlugin plugin, PluginMessageListener listener) {
		ArrayList<PluginMessageListener> listenerList = null;
		if (pluginListeners.containsKey(plugin)) {
			listenerList = pluginListeners.get(plugin);
		} else {
			listenerList = new ArrayList<PluginMessageListener>();
		}
		listenerList.add(listener);
		pluginListeners.put(plugin, listenerList);
		listeners.add(listener);
	}
	
	public static void unregisterPluginListeners(PseudoPlugin plugin) {
		if (pluginListeners.containsKey(plugin)) {
			ArrayList<PluginMessageListener> listenerList = pluginListeners.get(plugin);
			for (PluginMessageListener l : listenerList) {
				listeners.remove(l);
			}
			pluginListeners.remove(plugin);
		}
	}

	public void onPluginMessageReceived(String channel, Player p, byte[] message) {
		if (channel.equals(channelName)) {
			ByteArrayDataInput in = ByteStreams.newDataInput(message);
			String subchannel = in.readUTF();
			for (PluginMessageListener listener : listeners) {
				listener.onPluginMessageReceived(subchannel, p, message);
			}
		}
	}

	public static void sendToBungee(Player p, String channel, Collection<Object> data) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF(channel);
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
		p.sendPluginMessage(PseudoAPI.plugin, channelName, out.toByteArray());
	}

}
