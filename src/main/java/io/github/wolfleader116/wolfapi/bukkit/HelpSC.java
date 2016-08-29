package io.github.wolfleader116.wolfapi.bukkit;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;

public class HelpSC implements SubCommandExecutor {

	protected WolfPlugin plugin;
	protected Message message;

	HelpSC(WolfPlugin plugin) {
		this.plugin = plugin;
		this.message = new Message(plugin);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		List<CommandDescription> commands = new ArrayList<CommandDescription>();
		if (sender instanceof Player) {
			for (CommandDescription cd : plugin.commandDescriptions) {
				if (((Player) sender).hasPermission(cd.getPermission())) {
					commands.add(cd);
				}
			}
		} else {
			for (CommandDescription cd : plugin.commandDescriptions) {
				commands.add(cd);
			}
		}
		List<Object> messages = new ArrayList<Object>();
		messages.add(WolfAPI.border + "===---" + WolfAPI.title + plugin.getPluginName() + " Help" + WolfAPI.border + "---===");
		if (args.length == 0) {
			for (int i = 0; i <= 9; i++) {
				if (i < commands.size()) {
					CommandDescription cd = commands.get(i);
					messages.add(new ComponentBuilder("/" + cmd.getName().toLowerCase() + " " + ChatColor.stripColor(cd.getCommand())).color(WolfAPI.command).event(new ClickEvent(WolfAPI.clickEvent, "/" + cmd.getName().toLowerCase() + " " + ChatColor.stripColor(cd.getCommand()))).event(new HoverEvent(Action.SHOW_TEXT, new ComponentBuilder("Click to run the Command!").color(WolfAPI.description).create())).append(" " + ChatColor.stripColor(cd.getDescription())).color(WolfAPI.description).create());
				}
			}
			if (commands.size() > 10) {
				int total = (int) Math.ceil(commands.size() / 10);
				messages.add(WolfAPI.border + "===---" + WolfAPI.title + "Page 1 of " + total + WolfAPI.border + "---===");
			} else {
				messages.add(WolfAPI.border + "===---" + WolfAPI.title + "Page 1 of 1" + WolfAPI.border + "---===");
			}
			Message.sendMessage(sender, messages);
			return true;
		} else {
			if (isInteger(args[0])) {
				int page = Integer.parseInt(args[0]);
				int total = (int) Math.ceil(commands.size() / 10);
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
							messages.add(WolfAPI.command + "/" + cmd.getName().toLowerCase() + " " + ChatColor.stripColor(cd.getCommand()) + " " + WolfAPI.description + ChatColor.stripColor(cd.getDescription()));
						}
					}
					if (commands.size() > 10) {
						messages.add(WolfAPI.border + "===---" + WolfAPI.title + "Page 1 of " + total + WolfAPI.border + "---===");
					} else {
						messages.add(WolfAPI.border + "===---" + WolfAPI.title + "Page 1 of 1" + WolfAPI.border + "---===");
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
	    } catch(NumberFormatException e) { 
	        return false; 
	    } catch(NullPointerException e) {
	        return false;
	    }
	    return true;
	}

}
