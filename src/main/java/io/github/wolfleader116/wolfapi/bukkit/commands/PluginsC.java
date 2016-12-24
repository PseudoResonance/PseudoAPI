package io.github.wolfleader116.wolfapi.bukkit.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import io.github.wolfleader116.wolfapi.bukkit.ConfigOptions;
import io.github.wolfleader116.wolfapi.bukkit.Errors;
import io.github.wolfleader116.wolfapi.bukkit.Message;
import io.github.wolfleader116.wolfapi.bukkit.PluginController;
import io.github.wolfleader116.wolfapi.bukkit.WolfAPI;
import io.github.wolfleader116.wolfapi.bukkit.WolfPlugin;
import net.md_5.bungee.api.ChatColor;

public class PluginsC implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("plugins")) {
			if (ConfigOptions.hidePlugins && ConfigOptions.allowWolfAPI) {
				if (!(sender instanceof Player)) {
					String pluginlist = "";
					WolfPlugin[] plugins = PluginController.getPlugins();
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
					messages.add(ConfigOptions.error + "Please note that all of these plugins are privately developed, but are open source at WolfLeader116's GitHub. Read the license before using!");
					messages.add("Plugins (" + pluginsfound + "): " + pluginlist);
					Message.sendMessage(sender, messages);
				} else {
					String pluginlist = "";
					WolfPlugin[] plugins = PluginController.getPlugins();
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
					messages.add(ConfigOptions.error + "Please note that all of these plugins are privately developed, but are open source at WolfLeader116's GitHub. Read the license before using!");
					messages.add("Plugins (" + pluginsfound + "): " + pluginlist);
					Message.sendMessage(sender, messages);
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
				}
			} if (ConfigOptions.hidePlugins && !(ConfigOptions.allowWolfAPI)) {
				if (!(sender instanceof Player)) {
					String pluginlist = "";
					WolfPlugin[] plugins = PluginController.getPlugins();
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
					messages.add(ConfigOptions.error + "Please note that all of these plugins are privately developed, but are open source at WolfLeader116's GitHub. Read the license before using!");
					messages.add("Plugins (" + pluginsfound + "): " + pluginlist);
					Message.sendMessage(sender, messages);
				} else {
					if (sender.hasPermission("wolfapi.allplugins")) {
						String pluginlist = "";
						WolfPlugin[] plugins = PluginController.getPlugins();
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
						messages.add(ConfigOptions.error + "Please note that all of these plugins are privately developed, but are open source at WolfLeader116's GitHub. Read the license before using!");
						messages.add("Plugins (" + pluginsfound + "): " + pluginlist);
						Message.sendMessage(sender, messages);
					} else {
						WolfAPI.message.sendPluginError(sender, Errors.NO_PERMISSION, " view plugins!");
					}
				}
			}
		}
		return false;
	}
}