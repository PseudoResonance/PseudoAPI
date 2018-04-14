package io.github.pseudoresonance.pseudoapi.bukkit.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.pseudoresonance.pseudoapi.bukkit.SubCommandExecutor;
import io.github.pseudoresonance.pseudoapi.bukkit.Message.Errors;
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
			PseudoAPI.message.sendPluginError(sender, Errors.NO_PERMISSION, "update plugins!");
			return false;
		}
	}

}
