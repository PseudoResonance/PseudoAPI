package io.github.wolfleader116.wolfapi.bukkit.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import io.github.wolfleader116.wolfapi.bukkit.Errors;
import io.github.wolfleader116.wolfapi.bukkit.SubCommandExecutor;
import io.github.wolfleader116.wolfapi.bukkit.WolfAPI;

public class ReloadSC implements SubCommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		try {
			WolfAPI.plugin.reloadConfig();
		} catch (Exception e) {
			WolfAPI.message.sendPluginError(sender, Errors.GENERIC);
			return false;
		}
		WolfAPI.message.sendPluginMessage(sender, "Plugin config reloaded!");
		return true;
	}

}
