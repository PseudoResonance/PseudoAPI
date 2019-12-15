package io.github.pseudoresonance.pseudoapi.bukkit.commands;

import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.pseudoresonance.pseudoapi.bukkit.SubCommandExecutor;
import io.github.pseudoresonance.pseudoapi.bukkit.Config;
import io.github.pseudoresonance.pseudoapi.bukkit.Chat.Errors;
import io.github.pseudoresonance.pseudoapi.bukkit.data.Backend;
import io.github.pseudoresonance.pseudoapi.bukkit.data.FileBackend;
import io.github.pseudoresonance.pseudoapi.bukkit.data.MySQLBackend;
import io.github.pseudoresonance.pseudoapi.bukkit.language.LanguageManager;
import io.github.pseudoresonance.pseudoapi.bukkit.playerdata.PlayerDataController;
import io.github.pseudoresonance.pseudoapi.bukkit.PseudoAPI;

public class BackendSC implements SubCommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) || sender.hasPermission("pseudoapi.backend")) {
			if (args.length == 0) {
				PseudoAPI.plugin.getChat().sendPluginError(sender, Errors.INVALID_SUBCOMMAND, "'list', 'migrate <from> <to>'");
				return false;
			} else {
				if (args[0].equalsIgnoreCase("list")) {
					String backendString = "";
					HashMap<String, Backend> backends = Config.getBackends();
					boolean first = true;
					for (String name : backends.keySet()) {
						Backend b = backends.get(name);
						if (b instanceof FileBackend) {
							if (first) {
								first = false;
								backendString += name + ": " + LanguageManager.getLanguage(sender).getMessage("pseudoapi.file");
							} else
								backendString += ", " + name + ": " + LanguageManager.getLanguage(sender).getMessage("pseudoapi.file");
						} else if (b instanceof MySQLBackend) {
							if (first) {
								first = false;
								backendString += name + ": " + LanguageManager.getLanguage(sender).getMessage("pseudoapi.mysql");
							} else
								backendString += ", " + name + ": " + LanguageManager.getLanguage(sender).getMessage("pseudoapi.mysql");
						}
					}
					PseudoAPI.plugin.getChat().sendPluginMessage(sender, LanguageManager.getLanguage(sender).getMessage("pseudoapi.backends") + ": " + backendString);
				} else if (args[0].equalsIgnoreCase("migrate")) {
					if (args.length == 1) {
						PseudoAPI.plugin.getChat().sendPluginError(sender, Errors.CUSTOM, LanguageManager.getLanguage(sender).getMessage("pseudoapi.add_backends_to_migrate"));
						return false;
					} else if (args.length == 2) {
						PseudoAPI.plugin.getChat().sendPluginError(sender, Errors.CUSTOM, LanguageManager.getLanguage(sender).getMessage("pseudoapi.add_backends_to_migrate"));
						return false;
					} else if (args.length >= 3) {
						HashMap<String, Backend> backends = Config.getBackends();
						Backend origin = null;
						Backend destination = null;
						for (String name : backends.keySet()) {
							if (name.equalsIgnoreCase(args[1]))
								origin = backends.get(name);
							else if (name.equalsIgnoreCase(args[2]))
								destination = backends.get(name);
						}
						if (origin == null) {
							PseudoAPI.plugin.getChat().sendPluginError(sender, Errors.CUSTOM, LanguageManager.getLanguage(sender).getMessage("pseudoapi.is_invalid_backend", args[1]));
							return false;
						}
						if (destination == null) {
							PseudoAPI.plugin.getChat().sendPluginError(sender, Errors.CUSTOM, LanguageManager.getLanguage(sender).getMessage("pseudoapi.is_invalid_backend", args[2]));
							return false;
						}
						String originName = origin.getName();
						String destinationName = destination.getName();
						PlayerDataController.migrateBackends(origin, destination).thenRunAsync(() -> {
							PseudoAPI.plugin.doSync(() -> {
								PseudoAPI.plugin.getChat().sendPluginMessage(sender, LanguageManager.getLanguage(sender).getMessage("pseudoapi.migrated_from_to", originName, destinationName));
							});
						});
						return true;
					}
				}
			}
		} else
			PseudoAPI.plugin.getChat().sendPluginError(sender, Errors.NO_PERMISSION, LanguageManager.getLanguage(sender).getMessage("pseudoapi.modify_backends"));
		return false;
	}

}
