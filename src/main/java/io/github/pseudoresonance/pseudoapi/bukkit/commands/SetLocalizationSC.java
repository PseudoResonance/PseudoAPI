package io.github.pseudoresonance.pseudoapi.bukkit.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.pseudoresonance.pseudoapi.bukkit.SubCommandExecutor;
import io.github.pseudoresonance.pseudoapi.bukkit.Chat.Errors;
import io.github.pseudoresonance.pseudoapi.bukkit.language.LanguageManager;
import io.github.pseudoresonance.pseudoapi.bukkit.playerdata.PlayerDataController;
import io.github.pseudoresonance.pseudoapi.bukkit.PseudoAPI;

public class SetLocalizationSC implements SubCommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) || sender.hasPermission("pseudoapi.setlocalization")) {
			if (args.length == 0) {
				return false;
			} else if (args.length == 1) {
				if (sender instanceof Player) {
					String locale = args[0].toLowerCase();
					if (LanguageManager.getLanguageList().contains(locale)) {
						PlayerDataController.setPlayerSetting(((Player) sender).getUniqueId().toString(), "locale", locale);
						PseudoAPI.plugin.getChat().sendPluginMessage(sender, LanguageManager.getLanguage(sender).getMessage("pseudoapi.set_localization", locale));
						return true;
					} else {
						PseudoAPI.plugin.getChat().sendPluginError(sender, Errors.CUSTOM, LanguageManager.getLanguage(sender).getMessage("pseudoapi.unknown_localization"));
						return false;
					}
				} else {
					PseudoAPI.plugin.getChat().sendPluginError(sender, Errors.CUSTOM, LanguageManager.getLanguage(sender).getMessage("pseudoapi.add_player_to_set"));
					return false;
				}
			} else {
				if (!(sender instanceof Player) || sender.hasPermission("pseudoapi.setlocalization.others")) {
					String uuid = PlayerDataController.getUUID(args[1]);
					if (uuid != null) {
						String name = PlayerDataController.getName(uuid);
						String locale = args[0].toLowerCase();
						if (LanguageManager.getLanguageList().contains(locale)) {
							PlayerDataController.setPlayerSetting(uuid, "locale", locale);
							PseudoAPI.plugin.getChat().sendPluginMessage(sender, LanguageManager.getLanguage(sender).getMessage("pseudoapi.set_localization_others", locale, name));
							return true;
						} else {
							PseudoAPI.plugin.getChat().sendPluginError(sender, Errors.CUSTOM, LanguageManager.getLanguage(sender).getMessage("pseudoapi.unknown_localization"));
							return false;
						}
					} else {
						PseudoAPI.plugin.getChat().sendPluginError(sender, Errors.NEVER_JOINED, args[1]);
						return false;
					}
				} else {
					PseudoAPI.plugin.getChat().sendPluginError(sender, Errors.NO_PERMISSION, LanguageManager.getLanguage(sender).getMessage("pseudoapi.permission_set_localization_others"));
					return false;
				}
			}
		} else {
			PseudoAPI.plugin.getChat().sendPluginError(sender, Errors.NO_PERMISSION, LanguageManager.getLanguage(sender).getMessage("pseudoapi.permission_set_localization"));
			return false;
		}
	}

}
