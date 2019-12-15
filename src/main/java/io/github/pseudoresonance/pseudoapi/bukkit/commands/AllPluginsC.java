package io.github.pseudoresonance.pseudoapi.bukkit.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import io.github.pseudoresonance.pseudoapi.bukkit.Chat;
import io.github.pseudoresonance.pseudoapi.bukkit.Chat.Errors;
import io.github.pseudoresonance.pseudoapi.bukkit.language.LanguageManager;
import io.github.pseudoresonance.pseudoapi.bukkit.PseudoAPI;

public class AllPluginsC implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		boolean isPlayer = sender instanceof Player;
		if (!isPlayer || sender.hasPermission("pseudoapi.allplugins")) {
			String pluginlist = "";
			Plugin[] plugins = Bukkit.getServer().getPluginManager().getPlugins();
			for(int i = 0; i < plugins.length; i++) {
				if (Bukkit.getServer().getPluginManager().isPluginEnabled(plugins[i])) {
					if (pluginlist == "") {
						if (isPlayer)
							pluginlist += ChatColor.GREEN + plugins[i].getName();
						else
							pluginlist += ChatColor.GREEN + "[E] " + plugins[i].getName();
					} else {
						if (isPlayer)
							pluginlist += ChatColor.RESET + ", " + ChatColor.GREEN + plugins[i].getName();
						else
							pluginlist += ChatColor.RESET + ", " + ChatColor.GREEN + "[E] " + plugins[i].getName();
					}
				} else {
					if (pluginlist == "") {
						if (isPlayer)
							pluginlist += ChatColor.RED + plugins[i].getName();
						else
							pluginlist += ChatColor.RED + "[D] " + plugins[i].getName();
					} else {
						if (isPlayer)
							pluginlist += ChatColor.RESET + ", " + ChatColor.RED + plugins[i].getName();
						else
							pluginlist += ChatColor.RESET + ", " + ChatColor.RED + "[D] " + plugins[i].getName();
					}
				}
			}
			Chat.sendMessage(sender, LanguageManager.getLanguage(sender).getMessage("pseudoapi.plugins") + " (" + plugins.length + "): " + pluginlist);
		} else {
			PseudoAPI.plugin.getChat().sendPluginError(sender, Errors.NO_PERMISSION, LanguageManager.getLanguage(sender).getMessage("pseudoapi.permission_view_plugins"));
			return false;
		}
		return false;
	}
}
