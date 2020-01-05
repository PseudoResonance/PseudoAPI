package io.github.pseudoresonance.pseudoapi.bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import io.github.pseudoresonance.pseudoapi.bukkit.Chat.Errors;
import io.github.pseudoresonance.pseudoapi.bukkit.language.LanguageManager;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class MainCommand implements CommandExecutor {

	protected PseudoPlugin plugin;

	public MainCommand(PseudoPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 0) {
			List<Object> messages = new ArrayList<Object>();
			messages.add(Config.borderColor + "===---" + Config.titleColor + LanguageManager.getLanguage(sender).getMessage("pseudoapi.plugin_info_header", plugin.getPluginName()) + Config.borderColor + "---===");
			messages.add(Config.descriptionColor + LanguageManager.getLanguage(sender).getMessage("pseudoapi.plugin_info", plugin.getPluginName(), plugin.getVersion()));
			String developers = "";
			for (int i = 0; i <= plugin.getAuthors().size() - 1; i++) {
				if (i >= 1) {
					developers = developers + ", ";
				}
				developers = developers + plugin.getAuthors().get(i);
			}
			messages.add(Config.descriptionColor + LanguageManager.getLanguage(sender).getMessage("pseudoapi.plugin_developers", developers));
			String[] split = LanguageManager.getLanguage(sender).getUnprocessedMessage("pseudoapi.plugin_help_suggestion").split("\\{\\$1\\$\\}");
			String end = "";
			if (split.length > 1)
				for (int i = 1; i < split.length; i++)
					end += split[i];
			TextComponent first = new TextComponent(split[0]);
			Chat.setComponentColors(first, Config.descriptionColorArray);
			TextComponent command = new TextComponent("/" + cmd.getName() + " help");
			Chat.setComponentColors(command, Config.commandColorArray);
			command.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + plugin.getOutputName() + ":" + cmd.getName() + " help"));
			TextComponent hover = new TextComponent(LanguageManager.getLanguage(sender).getMessage("pseudoapi.click_to_run"));
			Chat.setComponentColors(hover, Config.descriptionColorArray);
			command.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] {hover}));
			TextComponent last = new TextComponent(end);
			Chat.setComponentColors(last, Config.descriptionColorArray);
			messages.add(new BaseComponent[] {first, command, last});
			Chat.sendMessage(sender, messages);
			return true;
		} else {
			Map<String, SubCommandExecutor> subCommands = plugin.subCommands;
			for (String sc : subCommands.keySet()) {
				if (args[0].equalsIgnoreCase(sc)) {
					List<String> argsList = new ArrayList<String>();
					for (String s : args) {
						argsList.add(s);
					}
					argsList.remove(0);
					String[] newArgs = argsList.toArray(new String[argsList.size()]);
					subCommands.get(sc).onCommand(sender, cmd, label, newArgs);
					return true;
				}
			}
			new Chat(plugin).sendPluginError(sender, Errors.CUSTOM, LanguageManager.getLanguage(sender).getMessage("pseudoapi.unknown_subcommand", cmd.getName()));
		}
		return false;
	}

}