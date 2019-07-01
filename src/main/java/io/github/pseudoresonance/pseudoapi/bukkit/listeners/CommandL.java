package io.github.pseudoresonance.pseudoapi.bukkit.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.RemoteServerCommandEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.server.TabCompleteEvent;

import io.github.pseudoresonance.pseudoapi.bukkit.CommandHandler;

public class CommandL implements Listener {

	@EventHandler
	public void tabComplete(TabCompleteEvent e) {
		String[] split = e.getBuffer().split("\\s+");
		List<String> argsList = new ArrayList<String>();
		for (int i = 1; i < split.length; i++) {
			argsList.add(split[i]);
		}
		if (e.getBuffer().endsWith(" ")) {
			argsList.add("");
		}
		String[] newArgs = argsList.toArray(new String[argsList.size()]);
		String cmdName = split[0];
		if (cmdName.startsWith("/"))
			cmdName = cmdName.substring(1);
		List<String> results = CommandHandler.runCompleter(e.getSender(), cmdName, newArgs);
		if (results != null)
			e.setCompletions(results);
	}

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
	public void serverCommand(ServerCommandEvent e) {
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
	public void remoteServerCommand(RemoteServerCommandEvent e) {
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
