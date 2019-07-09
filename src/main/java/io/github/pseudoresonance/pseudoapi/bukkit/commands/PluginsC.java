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
import io.github.pseudoresonance.pseudoapi.bukkit.Message;
import io.github.pseudoresonance.pseudoapi.bukkit.Message.Errors;
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
				messages.add("&cPlease note that all of these plugins are privately developed, but are open source at PseudoResonance's GitHub.");
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
			messages.add("Plugins (" + plugins.length + "): " + pluginlist);
			Message.sendMessage(sender, messages);
			return true;
		} else {
			PseudoAPI.message.sendPluginError(sender, Errors.NO_PERMISSION, "view plugins!");
			return false;
		}
	}
}
