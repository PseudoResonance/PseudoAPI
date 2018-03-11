package io.github.pseudoresonance.pseudoapi.bukkit.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import io.github.pseudoresonance.pseudoapi.bukkit.ConfigOptions;
import io.github.pseudoresonance.pseudoapi.bukkit.Errors;
import io.github.pseudoresonance.pseudoapi.bukkit.Message;
import io.github.pseudoresonance.pseudoapi.bukkit.PluginController;
import io.github.pseudoresonance.pseudoapi.bukkit.PseudoAPI;
import io.github.pseudoresonance.pseudoapi.bukkit.PseudoPlugin;
import net.md_5.bungee.api.ChatColor;

public class PluginsC implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("plugins")) {
			if (ConfigOptions.hidePlugins && ConfigOptions.allowPseudoAPI) {
				if (!(sender instanceof Player)) {
					String pluginlist = "";
					PseudoPlugin[] plugins = PluginController.getPlugins();
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
					List<String> messages = new ArrayList<String>();
					messages.add(ConfigOptions.error + "Please note that all of these plugins are privately developed, but are open source at PseudoResonance's GitHub. Read the license before using!");
					messages.add("Plugins (" + pluginsfound + "): " + pluginlist);
					Message.sendMessage(sender, messages);
				} else {
					if (sender.hasPermission("pseudoapi.plugins")) {
						String pluginlist = "";
						PseudoPlugin[] plugins = PluginController.getPlugins();
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
						List<String> messages = new ArrayList<String>();
						messages.add(ConfigOptions.error + "Please note that all of these plugins are privately developed, but are open source at PseudoResonance's GitHub. Read the license before using!");
						messages.add("Plugins (" + pluginsfound + "): " + pluginlist);
						Message.sendMessage(sender, messages);
					} else {
						PseudoAPI.message.sendPluginError(sender, Errors.NO_PERMISSION, "view plugins!");
						return false;
					}
				}
			} if (!(ConfigOptions.hidePlugins)) {
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
					if (sender.hasPermission("pseudoapi.plugins")) {
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
			} if (ConfigOptions.hidePlugins && !(ConfigOptions.allowPseudoAPI)) {
				if (!(sender instanceof Player)) {
					String pluginlist = "";
					PseudoPlugin[] plugins = PluginController.getPlugins();
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
					List<String> messages = new ArrayList<String>();
					messages.add(ConfigOptions.error + "Please note that all of these plugins are privately developed, but are open source at PseudoResonance's GitHub. Read the license before using!");
					messages.add("Plugins (" + pluginsfound + "): " + pluginlist);
					Message.sendMessage(sender, messages);
				} else {
					if (sender.hasPermission("pseudoapi.allplugins")) {
						String pluginlist = "";
						PseudoPlugin[] plugins = PluginController.getPlugins();
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
						List<String> messages = new ArrayList<String>();
						messages.add(ConfigOptions.error + "Please note that all of these plugins are privately developed, but are open source at PseudoResonance's GitHub. Read the license before using!");
						messages.add("Plugins (" + pluginsfound + "): " + pluginlist);
						Message.sendMessage(sender, messages);
					} else {
						PseudoAPI.message.sendPluginError(sender, Errors.NO_PERMISSION, "view plugins!");
						return false;
					}
				}
			}
		}
		return false;
	}
}
