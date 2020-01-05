package io.github.pseudoresonance.pseudoapi.bukkit;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.pseudoresonance.pseudoapi.bukkit.Chat.Errors;
import io.github.pseudoresonance.pseudoapi.bukkit.language.LanguageManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class HelpSC implements SubCommandExecutor {

	protected PseudoPlugin plugin;
	protected Chat message;

	public HelpSC(PseudoPlugin plugin) {
		this.plugin = plugin;
		this.message = new Chat(plugin);
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
		messages.add(Config.borderColor + "===---" + Config.titleColor + LanguageManager.getLanguage(sender).getMessage("pseudoapi.plugin_help_header", plugin.getPluginName()) + Config.borderColor + "---===");
		if (args.length == 0) {
			for (int i = 0; i <= 9; i++) {
				if (i < commands.size()) {
					CommandDescription cd = commands.get(i);
					TextComponent cmdMessage = new TextComponent("/" + ChatColor.stripColor(cd.getCommand()));
					Chat.setComponentColors(cmdMessage, Config.commandColorArray);
					cmdMessage.setClickEvent(new ClickEvent(cd.getRunnable() ? Config.clickEvent : ClickEvent.Action.SUGGEST_COMMAND, "/" + ChatColor.stripColor(cd.getCommand())));
					TextComponent hoverMessage = new TextComponent(LanguageManager.getLanguage(sender).getMessage("pseudoapi.click_to_run"));
					Chat.setComponentColors(hoverMessage, Config.descriptionColorArray);
					cmdMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] {hoverMessage}));
					TextComponent message = new TextComponent(" " + ChatColor.stripColor(LanguageManager.getLanguage(sender).getMessage(cd.getDescriptionKey())));
					Chat.setComponentColors(message, Config.descriptionColorArray);
					messages.add(new BaseComponent[] {cmdMessage, message});
				}
			}
			if (commands.size() > 10) {
				int total = (int) Math.ceil((double) commands.size() / 10);
				messages.add(Config.borderColor + "===---" + Config.titleColor + LanguageManager.getLanguage(sender).getMessage("pseudoapi.page_number", 1, total) + Config.borderColor + "---===");
			} else {
				messages.add(Config.borderColor + "===---" + Config.titleColor + LanguageManager.getLanguage(sender).getMessage("pseudoapi.page_number", 1, 1) + Config.borderColor + "---===");
			}
			Chat.sendMessage(sender, messages);
			return true;
		} else {
			if (isInteger(args[0])) {
				int page = Integer.parseInt(args[0]);
				int total = (int) Math.ceil((double) commands.size() / 10);
				if (page > total) {
					message.sendPluginError(sender, Errors.CUSTOM, LanguageManager.getLanguage(sender).getMessage("pseudoapi.only_pages_available", total));
					return false;
				} else if (page <= 0) {
					message.sendPluginError(sender, Errors.CUSTOM, LanguageManager.getLanguage(sender).getMessage("pseudoapi.select_valid_page"));
					return false;
				} else {
					for (int i = (page - 1) * 10; i <= ((page - 1) * 10) + 9; i++) {
						if (i < commands.size()) {
							CommandDescription cd = commands.get(i);
							TextComponent cmdMessage = new TextComponent("/" + ChatColor.stripColor(cd.getCommand()));
							Chat.setComponentColors(cmdMessage, Config.commandColorArray);
							cmdMessage.setClickEvent(new ClickEvent(cd.getRunnable() ? Config.clickEvent : ClickEvent.Action.SUGGEST_COMMAND, "/" + ChatColor.stripColor(cd.getCommand())));
							TextComponent hoverMessage = new TextComponent(LanguageManager.getLanguage(sender).getMessage("pseudoapi.click_to_run"));
							Chat.setComponentColors(hoverMessage, Config.descriptionColorArray);
							cmdMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] {hoverMessage}));
							TextComponent message = new TextComponent(" " + ChatColor.stripColor(LanguageManager.getLanguage(sender).getMessage(cd.getDescriptionKey())));
							Chat.setComponentColors(message, Config.descriptionColorArray);
							messages.add(new BaseComponent[] {cmdMessage, message});
						}
					}
					messages.add(Config.borderColor + "===---" + Config.titleColor + LanguageManager.getLanguage(sender).getMessage("pseudoapi.page_number", page, total) + Config.borderColor + "---===");
					Chat.sendMessage(sender, messages);
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
