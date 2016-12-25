package io.github.wolfleader116.wolfapi.bukkit.commands;

import java.io.File;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.wolfleader116.wolfapi.bukkit.ConfigOptions;
import io.github.wolfleader116.wolfapi.bukkit.Errors;
import io.github.wolfleader116.wolfapi.bukkit.SubCommandExecutor;
import io.github.wolfleader116.wolfapi.bukkit.WolfAPI;

public class ResetSC implements SubCommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			if (sender.hasPermission(cmd.getPermission())) {
				try {
					File conf = new File(WolfAPI.plugin.getDataFolder(), "config.yml");
					conf.delete();
					WolfAPI.plugin.saveDefaultConfig();
					WolfAPI.plugin.reloadConfig();
				} catch (Exception e) {
					WolfAPI.message.sendPluginError(sender, Errors.GENERIC);
					return false;
				}
				ConfigOptions.reloadConfig();
				WolfAPI.message.sendPluginMessage(sender, "Plugin config reset!");
				return true;
			} else {
				WolfAPI.message.sendPluginError(sender, Errors.NO_PERMISSION, " view plugins!");
				return false;
			}
		} else {
			try {
				File conf = new File(WolfAPI.plugin.getDataFolder(), "config.yml");
				conf.delete();
				WolfAPI.plugin.saveDefaultConfig();
				WolfAPI.plugin.reloadConfig();
			} catch (Exception e) {
				WolfAPI.message.sendPluginError(sender, Errors.GENERIC);
				return false;
			}
			ConfigOptions.reloadConfig();
			WolfAPI.message.sendPluginMessage(sender, "Plugin config reset!");
			return true;
		}
	}

}
