package io.github.pseudoresonance.pseudoapi.bukkit.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.pseudoresonance.pseudoapi.bukkit.SubCommandExecutor;
import io.github.pseudoresonance.pseudoapi.bukkit.language.LanguageManager;
import io.github.pseudoresonance.pseudoapi.bukkit.Chat.Errors;
import io.github.pseudoresonance.pseudoapi.bukkit.PseudoAPI;
import io.github.pseudoresonance.pseudoapi.bukkit.PseudoUpdater;

public class UpdateSC implements SubCommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) || sender.hasPermission("pseudoapi.update")) {
			if (args.length == 0) {
				PseudoUpdater.checkUpdates(sender);
				return true;
			} else {
				PseudoUpdater.checkUpdates(sender, args[0]);
				return true;
			}
		} else {
			PseudoAPI.plugin.getChat().sendPluginError(sender, Errors.NO_PERMISSION, LanguageManager.getLanguage(sender).getMessage("pseudoapi.permission_update_plugins"));
			return false;
		}
	}

}
