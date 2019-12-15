package io.github.pseudoresonance.pseudoapi.bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import io.github.pseudoresonance.pseudoapi.bukkit.Chat.Errors;
import io.github.pseudoresonance.pseudoapi.bukkit.language.LanguageManager;
import io.github.pseudoresonance.pseudoapi.bukkit.utils.ChatComponent;
import io.github.pseudoresonance.pseudoapi.bukkit.utils.ChatElement;
import io.github.pseudoresonance.pseudoapi.bukkit.utils.ElementBuilder;
import io.github.pseudoresonance.pseudoapi.bukkit.utils.ChatComponent.ComponentType;

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
				end = split[1];
			messages.add(new ElementBuilder(new ChatElement(Config.descriptionColor + split[0]), new ChatElement(Config.commandColor + "/" + cmd.getName() + " help", new ChatComponent(Config.clickEvent, "/" + plugin.getOutputName() + ":" + cmd.getName() + " help"), new ChatComponent(ComponentType.SHOW_TEXT, Config.descriptionColor + LanguageManager.getLanguage(sender).getMessage("pseudoapi.click_to_run"))), new ChatElement(Config.descriptionColor + end)).build());
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