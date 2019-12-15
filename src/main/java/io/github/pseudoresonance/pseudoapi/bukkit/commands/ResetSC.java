package io.github.pseudoresonance.pseudoapi.bukkit.commands;

import java.io.File;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.pseudoresonance.pseudoapi.bukkit.SubCommandExecutor;
import io.github.pseudoresonance.pseudoapi.bukkit.Chat.Errors;
import io.github.pseudoresonance.pseudoapi.bukkit.data.Data;
import io.github.pseudoresonance.pseudoapi.bukkit.language.LanguageManager;
import io.github.pseudoresonance.pseudoapi.bukkit.playerdata.PlayerDataController;
import io.github.pseudoresonance.pseudoapi.bukkit.PseudoAPI;

public class ResetSC implements SubCommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			if (sender.hasPermission("pseudoapi.reset")) {
				try {
					File conf = new File(PseudoAPI.plugin.getDataFolder(), "config.yml");
					conf.delete();
					PseudoAPI.plugin.saveDefaultConfig();
					PseudoAPI.plugin.reloadConfig();
				} catch (Exception e) {
					PseudoAPI.plugin.getChat().sendPluginError(sender, Errors.GENERIC);
					return false;
				}
				PseudoAPI.getPluginConfig().reloadConfig();
				Data.loadBackends();
				PlayerDataController.update();
				PseudoAPI.plugin.getChat().sendPluginMessage(sender, LanguageManager.getLanguage(sender).getMessage("pseudoapi.config_reset"));
				return true;
			} else {
				PseudoAPI.plugin.getChat().sendPluginError(sender, Errors.NO_PERMISSION, LanguageManager.getLanguage(sender).getMessage("pseudoapi.permission_reset_config"));
				return false;
			}
		} else {
			try {
				File conf = new File(PseudoAPI.plugin.getDataFolder(), "config.yml");
				conf.delete();
				PseudoAPI.plugin.saveDefaultConfig();
				PseudoAPI.plugin.reloadConfig();
			} catch (Exception e) {
				PseudoAPI.plugin.getChat().sendPluginError(sender, Errors.GENERIC);
				return false;
			}
			PseudoAPI.getPluginConfig().reloadConfig();
			Data.loadBackends();
			PlayerDataController.update();
			PseudoAPI.plugin.getChat().sendPluginMessage(sender, LanguageManager.getLanguage(sender).getMessage("pseudoapi.config_reset"));
			return true;
		}
	}

}
