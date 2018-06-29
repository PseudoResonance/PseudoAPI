package io.github.pseudoresonance.pseudoapi.bukkit.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import io.github.pseudoresonance.pseudoapi.bukkit.Message;
import io.github.pseudoresonance.pseudoapi.bukkit.Message.Errors;
import io.github.pseudoresonance.pseudoapi.bukkit.PseudoAPI;

public class AllPluginsC implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			String pluginlist = "";
			Plugin[] plugins = Bukkit.getServer().getPluginManager().getPlugins();
			int pluginsfound = plugins.length;
			for(int i = 0; i < plugins.length; i++) {
				if (Bukkit.getServer().getPluginManager().isPluginEnabled(plugins[i])) {
					String add = "";
					if (pluginlist == "") {
						add = ChatColor.GREEN + "[E] " + plugins[i].getName();
					} else {
						add = ChatColor.RESET + ", " + ChatColor.GREEN + "[E] " + plugins[i].getName();
					}
					pluginlist = pluginlist + add;
				} else {
					String add = "";
					if (pluginlist == "") {
						add = ChatColor.RED + "[D] " + plugins[i].getName();
					} else {
						add = ChatColor.RESET + ", " + ChatColor.RED + "[D] " + plugins[i].getName();
					}
					pluginlist = pluginlist + add;
				}
			}
			Message.sendMessage(sender, "Plugins (" + pluginsfound + "): " + pluginlist);
		} else {
			if (sender.hasPermission("pseudoapi.allplugins")) {
				String pluginlist = "";
				Plugin[] plugins = Bukkit.getServer().getPluginManager().getPlugins();
				int pluginsfound = plugins.length;
				for(int i = 0; i < plugins.length; i++) {
					if (Bukkit.getServer().getPluginManager().isPluginEnabled(plugins[i])) {
						String add = "";
						if (pluginlist == "") {
							add = ChatColor.GREEN + plugins[i].getName();
						} else {
							add = ChatColor.RESET + ", " + ChatColor.GREEN + plugins[i].getName();
						}
						pluginlist = pluginlist + add;
					} else {
						String add = "";
						if (pluginlist == "") {
							add = ChatColor.RED + plugins[i].getName();
						} else {
							add = ChatColor.RESET + ", " + ChatColor.RED + plugins[i].getName();
						}
						pluginlist = pluginlist + add;
					}
				}
				Message.sendMessage(sender, "Plugins (" + pluginsfound + "): " + pluginlist);
			} else {
				PseudoAPI.message.sendPluginError(sender, Errors.NO_PERMISSION, "view plugins!");
				return false;
			}
		}
		return false;
	}
}
