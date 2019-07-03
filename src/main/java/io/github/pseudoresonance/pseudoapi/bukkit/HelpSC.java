package io.github.pseudoresonance.pseudoapi.bukkit;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.pseudoresonance.pseudoapi.bukkit.Message.Errors;
import io.github.pseudoresonance.pseudoapi.bukkit.utils.ChatComponent;
import io.github.pseudoresonance.pseudoapi.bukkit.utils.ChatElement;
import io.github.pseudoresonance.pseudoapi.bukkit.utils.ElementBuilder;
import io.github.pseudoresonance.pseudoapi.bukkit.utils.ChatComponent.ComponentType;
import net.md_5.bungee.api.ChatColor;

public class HelpSC implements SubCommandExecutor {

	protected PseudoPlugin plugin;
	protected Message message;

	public HelpSC(PseudoPlugin plugin) {
		this.plugin = plugin;
		this.message = new Message(plugin);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		List<CommandDescription> commands = new ArrayList<CommandDescription>();
		if (sender instanceof Player) {
			for (CommandDescription cd : plugin.commandDescriptions) {
				if (cd.getPermission() == "" || ((Player) sender).hasPermission(cd.getPermission())) {
					commands.add(cd);
				}
			}
		} else {
			for (CommandDescription cd : plugin.commandDescriptions) {
				commands.add(cd);
			}
		}
		List<Object> messages = new ArrayList<Object>();
		messages.add(Config.borderColor + "===---" + Config.titleColor + plugin.getPluginName() + " Help" + Config.borderColor + "---===");
		if (args.length == 0) {
			for (int i = 0; i <= 9; i++) {
				if (i < commands.size()) {
					CommandDescription cd = commands.get(i);
					if (cd.getRunnable()) {
						messages.add(new ElementBuilder(new ChatElement(Config.commandColor + "/" + ChatColor.stripColor(cd.getCommand()), new ChatComponent(Config.clickEvent, "/" + ChatColor.stripColor(cd.getCommand())), new ChatComponent(ComponentType.SHOW_TEXT, Config.descriptionColor + "Click to run the command!")), new ChatElement(" " + Config.descriptionColor + ChatColor.stripColor(cd.getDescription()))).build());
					} else {
						messages.add(new ElementBuilder(new ChatElement(Config.commandColor + "/" + ChatColor.stripColor(cd.getCommand()), new ChatComponent(ComponentType.SUGGEST_COMMAND, "/" + ChatColor.stripColor(cd.getCommand())), new ChatComponent(ComponentType.SHOW_TEXT, Config.descriptionColor + "Click to run the command!")), new ChatElement(" " + Config.descriptionColor + ChatColor.stripColor(cd.getDescription()))).build());
					}
				}
			}
			if (commands.size() > 10) {
				int total = (int) Math.ceil((double) commands.size() / 10);
				messages.add(Config.borderColor + "===---" + Config.titleColor + "Page 1 of " + total + Config.borderColor + "---===");
			} else {
				messages.add(Config.borderColor + "===---" + Config.titleColor + "Page 1 of 1" + Config.borderColor + "---===");
			}
			Message.sendMessage(sender, messages);
			return true;
		} else {
			if (isInteger(args[0])) {
				int page = Integer.parseInt(args[0]);
				int total = (int) Math.ceil((double) commands.size() / 10);
				if (page > total) {
					message.sendPluginError(sender, Errors.CUSTOM, "Only " + total + " pages available!");
					return false;
				} else if (page <= 0) {
					message.sendPluginError(sender, Errors.CUSTOM, "Please select a page.");
					return false;
				} else {
					for (int i = (page - 1) * 10; i <= ((page - 1) * 10) + 9; i++) {
						if (i < commands.size()) {
							CommandDescription cd = commands.get(i);
							if (cd.getRunnable()) {
								messages.add(new ElementBuilder(new ChatElement(Config.commandColor + "/" + ChatColor.stripColor(cd.getCommand()), new ChatComponent(Config.clickEvent, "/" + ChatColor.stripColor(cd.getCommand())), new ChatComponent(ComponentType.SHOW_TEXT, Config.descriptionColor + "Click to run the command!")), new ChatElement(" " + Config.descriptionColor + ChatColor.stripColor(cd.getDescription()))).build());
							} else {
								messages.add(new ElementBuilder(new ChatElement(Config.commandColor + "/" + ChatColor.stripColor(cd.getCommand()), new ChatComponent(ComponentType.SUGGEST_COMMAND, "/" + ChatColor.stripColor(cd.getCommand())), new ChatComponent(ComponentType.SHOW_TEXT, Config.descriptionColor + "Click to run the command!")), new ChatElement(" " + Config.descriptionColor + ChatColor.stripColor(cd.getDescription()))).build());
							}
						}
					}
					if (commands.size() > 10) {
						messages.add(Config.borderColor + "===---" + Config.titleColor + "Page 1 of " + total + Config.borderColor + "---===");
					} else {
						messages.add(Config.borderColor + "===---" + Config.titleColor + "Page 1 of 1" + Config.borderColor + "---===");
					}
					Message.sendMessage(sender, messages);
					return true;
				}
			} else {
				message.sendPluginError(sender, Errors.NOT_A_NUMBER, args[0]);
				return false;
			}
		}
	}

	private static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		} catch (NullPointerException e) {
			return false;
		}
		return true;
	}

}
