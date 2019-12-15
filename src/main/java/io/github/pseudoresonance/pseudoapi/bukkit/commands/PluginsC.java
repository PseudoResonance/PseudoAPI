package io.github.pseudoresonance.pseudoapi.bukkit.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import io.github.pseudoresonance.pseudoapi.bukkit.Config;
import io.github.pseudoresonance.pseudoapi.bukkit.Chat;
import io.github.pseudoresonance.pseudoapi.bukkit.Chat.Errors;
import io.github.pseudoresonance.pseudoapi.bukkit.language.LanguageManager;
import io.github.pseudoresonance.pseudoapi.bukkit.PluginController;
import io.github.pseudoresonance.pseudoapi.bukkit.PseudoAPI;
import net.md_5.bungee.api.ChatColor;

public class PluginsC implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		boolean isPlayer = sender instanceof Player;
		if (!isPlayer || (sender.hasPermission("pseudoapi.plugins") && (Config.showPseudoAPI || !Config.hidePlugins))) {
			String pluginlist = "";
			Plugin[] plugins = null;
			List<String> messages = new ArrayList<String>();
			if (!isPlayer || (sender.hasPermission("pseudoapi.plugins") && !Config.hidePlugins))
				plugins = Bukkit.getServer().getPluginManager().getPlugins();
			else if (sender.hasPermission("pseudoapi.plugins") && Config.hidePlugins && Config.showPseudoAPI) {
				plugins = PluginController.getPlugins();
				messages.add(ChatColor.RED + LanguageManager.getLanguage(sender).getMessage("pseudoapi.plugins_privately_developed"));
			}
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
			messages.add(LanguageManager.getLanguage(sender).getMessage("pseudoapi.plugins") + " (" + plugins.length + "): " + pluginlist);
			Chat.sendMessage(sender, messages);
			return true;
		} else {
			PseudoAPI.plugin.getChat().sendPluginError(sender, Errors.NO_PERMISSION, LanguageManager.getLanguage(sender).getMessage("pseudoapi.permission_view_plugins"));
			return false;
		}
	}
}
