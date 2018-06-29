package io.github.pseudoresonance.pseudoapi.bukkit.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.RemoteServerCommandEvent;
import org.bukkit.event.server.ServerCommandEvent;

import io.github.pseudoresonance.pseudoapi.bukkit.CommandHandler;

public class CommandL implements Listener {

	@EventHandler
	public void commandPreprocess(PlayerCommandPreprocessEvent e) {
		String[] split = e.getMessage().split("\\s+");
		List<String> argsList = new ArrayList<String>();
		for (int i = 1; i < split.length; i++) {
			argsList.add(split[i]);
		}
		String[] newArgs = argsList.toArray(new String[argsList.size()]);
		if (CommandHandler.runCommand(e.getPlayer(), split[0].substring(1), newArgs))
			e.setCancelled(true);
	}

	@EventHandler
	public void commandPreprocess(ServerCommandEvent e) {
		String[] split = e.getCommand().split("\\s+");
		List<String> argsList = new ArrayList<String>();
		for (int i = 1; i < split.length; i++) {
			argsList.add(split[i]);
		}
		String[] newArgs = argsList.toArray(new String[argsList.size()]);
		if (CommandHandler.runCommand(Bukkit.getServer().getConsoleSender(), split[0], newArgs))
			e.setCancelled(true);
	}

	@EventHandler
	public void commandPreprocess(RemoteServerCommandEvent e) {
		String[] split = e.getCommand().split("\\s+");
		List<String> argsList = new ArrayList<String>();
		for (int i = 1; i < split.length; i++) {
			argsList.add(split[i]);
		}
		String[] newArgs = argsList.toArray(new String[argsList.size()]);
		if (CommandHandler.runCommand(Bukkit.getServer().getConsoleSender(), split[0], newArgs))
			e.setCancelled(true);
	}

}
