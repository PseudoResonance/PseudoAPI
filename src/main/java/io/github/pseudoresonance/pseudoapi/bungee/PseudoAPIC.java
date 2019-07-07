package io.github.pseudoresonance.pseudoapi.bungee;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;

public class PseudoAPIC extends Command {

	protected PseudoAPIC() {
		super("pseudoapibungee");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(new ComponentBuilder(PseudoAPI.plugin.getDescription().getName() + " by " + PseudoAPI.plugin.getDescription().getAuthor() + " Version " + PseudoAPI.plugin.getDescription().getVersion()).color(ChatColor.AQUA).create());
		} else if (args.length > 0) {
			if (args[0].equalsIgnoreCase("help")) {
				if (sender.hasPermission("pseudoapibungee.help")) {
					sender.sendMessage(new ComponentBuilder("/pseudoapibungee").color(ChatColor.RED).append(" Shows plugin info").color(ChatColor.AQUA).create());
					sender.sendMessage(new ComponentBuilder("/pseudoapibungee help").color(ChatColor.RED).append(" Shows plugin help").color(ChatColor.AQUA).create());
					sender.sendMessage(new ComponentBuilder("/pseudoapibungee reload").color(ChatColor.RED).append(" Reloads plugin config").color(ChatColor.AQUA).create());
					sender.sendMessage(new ComponentBuilder("/pseudoapibungee reset").color(ChatColor.RED).append(" Resets plugin config").color(ChatColor.AQUA).create());
					sender.sendMessage(new ComponentBuilder("/pseudoapibungee update").color(ChatColor.RED).append(" Updates the plugin").color(ChatColor.AQUA).create());
				} else {
					sender.sendMessage(new ComponentBuilder("You don't have permission to see help!").color(ChatColor.RED).create());
				}
			} else if (args[0].equalsIgnoreCase("reload")) {
				if (sender.hasPermission("pseudoapibungee.reload")) {
					PseudoAPI.getConfig().reloadConfig();
					sender.sendMessage(new ComponentBuilder("Config has been reloaded!").color(ChatColor.AQUA).create());
				} else {
					sender.sendMessage(new ComponentBuilder("You don't have permission to reload the config!").color(ChatColor.RED).create());
				}
			} else if (args[0].equalsIgnoreCase("reset")) {
				if (sender.hasPermission("pseudoapibungee.reset")) {
					boolean result = PseudoAPI.getConfig().resetConfig();
					if (result)
						sender.sendMessage(new ComponentBuilder("Config has been reset!").color(ChatColor.AQUA).create());
					else
						sender.sendMessage(new ComponentBuilder("There was an error while resetting the config!").color(ChatColor.AQUA).create());
				} else {
					sender.sendMessage(new ComponentBuilder("You don't have permission to reset the config!").color(ChatColor.RED).create());
				}
			} else if (args[0].equalsIgnoreCase("update")) {
				if (sender.hasPermission("pseudoapibungee.update")) {
					PseudoUpdater.checkUpdates(sender);
				} else {
					sender.sendMessage(new ComponentBuilder("You don't have permission to update the plugin!").color(ChatColor.RED).create());
				}
			} else {
				sender.sendMessage(new ComponentBuilder("Invalid subcommand!").color(ChatColor.RED).create());
			}
		}
	}

}
