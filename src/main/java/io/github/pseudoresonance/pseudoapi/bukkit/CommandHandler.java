package io.github.pseudoresonance.pseudoapi.bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;

public class CommandHandler {

	private static HashMap<String, CommandExecutor> executors = new HashMap<String, CommandExecutor>();
	private static HashMap<String, TabCompleter> completers = new HashMap<String, TabCompleter>();
	private static HashMap<String, PseudoPlugin> owners = new HashMap<String, PseudoPlugin>();
	private static HashMap<PseudoPlugin, ArrayList<String>> plugins = new HashMap<PseudoPlugin, ArrayList<String>>();
	
	protected static boolean registerCommand(PseudoPlugin plugin, String cmd, CommandExecutor executor) {
		String lower = cmd.toLowerCase();
		if (executors.containsKey(lower))
			return false;
		executors.put(lower, executor);
		ArrayList<String> cmds = plugins.get(plugin);
		if (cmds == null)
			cmds = new ArrayList<String>();
		cmds.add(lower);
		plugins.put(plugin, cmds);
		owners.put(cmd, plugin);
		return true;
	}
	
	protected static boolean registerCommand(PseudoPlugin plugin, String cmd, CommandExecutor executor, TabCompleter completer) {
		String lower = cmd.toLowerCase();
		if (executors.containsKey(lower))
			return false;
		executors.put(lower, executor);
		completers.put(lower, completer);
		ArrayList<String> cmds = plugins.get(plugin);
		if (cmds == null)
			cmds = new ArrayList<String>();
		cmds.add(lower);
		plugins.put(plugin, cmds);
		owners.put(cmd, plugin);
		return true;
	}
	
	protected static void unregisterPlugin(PseudoPlugin plugin) {
		ArrayList<String> cmds = plugins.remove(plugin);
		if (cmds != null) {
			for (String cmd : cmds) {
				executors.remove(cmd);
				owners.remove(cmd);
				completers.remove(cmd);
			}
		}
	}
	
	protected static void unregisterCommand(PseudoPlugin plugin, String cmd) {
		String lower = cmd.toLowerCase();
		ArrayList<String> cmds = plugins.get(plugin);
		if (cmds != null) {
			if (cmds.contains(lower)) {
				executors.remove(lower);
				owners.remove(lower);
				completers.remove(lower);
				cmds.remove(lower);
				if (cmds.size() > 0) {
					plugins.put(plugin, cmds);
				} else
					plugins.remove(plugin);
			}
		}
	}
	
	public static boolean runCommand(CommandSender sender, String cmd, String[] args) {
		CommandExecutor executor = executors.get(cmd.toLowerCase());
		if (executor != null) {
			try {
				Class<PluginCommand> cmdC = PluginCommand.class;
				Constructor<PluginCommand> constructor = cmdC.getDeclaredConstructor(String.class, Plugin.class);
				constructor.setAccessible(true);
				PluginCommand pcmd = constructor.newInstance(cmd, owners.get(cmd.toLowerCase()));
				executor.onCommand(sender, pcmd, cmd, args);
				return true;
			} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public static List<String> runCompleter(CommandSender sender, String cmd, String[] args) {
		TabCompleter completer = completers.get(cmd.toLowerCase());
		if (completer != null) {
			try {
				Class<PluginCommand> cmdC = PluginCommand.class;
				Constructor<PluginCommand> constructor = cmdC.getDeclaredConstructor(String.class, Plugin.class);
				constructor.setAccessible(true);
				PluginCommand pcmd = constructor.newInstance(cmd, owners.get(cmd.toLowerCase()));
				return completer.onTabComplete(sender, pcmd, cmd, args);
			} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}