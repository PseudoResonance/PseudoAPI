package io.github.pseudoresonance.pseudoapi.bukkit.commands;

import java.io.File;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.pseudoresonance.pseudoapi.bukkit.SubCommandExecutor;
import io.github.pseudoresonance.pseudoapi.bukkit.Message.Errors;
import io.github.pseudoresonance.pseudoapi.bukkit.data.Data;
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
					PseudoAPI.message.sendPluginError(sender, Errors.GENERIC);
					return false;
				}
				PseudoAPI.getConfigOptions().reloadConfig();
				Data.loadBackends();
				PlayerDataController.update();
				PseudoAPI.message.sendPluginMessage(sender, "Plugin config reset!");
				return true;
			} else {
				PseudoAPI.message.sendPluginError(sender, Errors.NO_PERMISSION, "reset the config!");
				return false;
			}
		} else {
			try {
				File conf = new File(PseudoAPI.plugin.getDataFolder(), "config.yml");
				conf.delete();
				PseudoAPI.plugin.saveDefaultConfig();
				PseudoAPI.plugin.reloadConfig();
			} catch (Exception e) {
				PseudoAPI.message.sendPluginError(sender, Errors.GENERIC);
				return false;
			}
			PseudoAPI.getConfigOptions().reloadConfig();
			Data.loadBackends();
			PlayerDataController.update();
			PseudoAPI.message.sendPluginMessage(sender, "Plugin config reset!");
			return true;
		}
	}

}
