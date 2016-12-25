package io.github.wolfleader116.wolfapi.bukkit.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.wolfleader116.wolfapi.bukkit.ConfigOptions;
import io.github.wolfleader116.wolfapi.bukkit.Errors;
import io.github.wolfleader116.wolfapi.bukkit.SubCommandExecutor;
import io.github.wolfleader116.wolfapi.bukkit.WolfAPI;

public class ReloadSC implements SubCommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			if (sender.hasPermission("wolfapi.reload")) {
				try {
					WolfAPI.plugin.reloadConfig();
				} catch (Exception e) {
					WolfAPI.message.sendPluginError(sender, Errors.GENERIC);
					return false;
				}
				ConfigOptions.reloadConfig();
				WolfAPI.message.sendPluginMessage(sender, "Plugin config reloaded!");
				return true;
			} else {
				WolfAPI.message.sendPluginError(sender, Errors.NO_PERMISSION, "reload the config!");
				return false;
			}
		} else {
			try {
				WolfAPI.plugin.reloadConfig();
			} catch (Exception e) {
				WolfAPI.message.sendPluginError(sender, Errors.GENERIC);
				return false;
			}
			ConfigOptions.reloadConfig();
			WolfAPI.message.sendPluginMessage(sender, "Plugin config reloaded!");
			return true;
		}
	}

}
