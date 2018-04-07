package io.github.pseudoresonance.pseudoapi.bukkit.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.pseudoresonance.pseudoapi.bukkit.Message.Errors;
import net.md_5.bungee.api.ChatColor;
import io.github.pseudoresonance.pseudoapi.bukkit.PseudoAPI;
import io.github.pseudoresonance.pseudoapi.bukkit.SubCommandExecutor;
import io.github.pseudoresonance.pseudoapi.bukkit.Utils;

public class BrandSC implements SubCommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) || (sender.hasPermission("pseudoapi.brand"))) {
			if (args.length > 0) {
				Player p = Bukkit.getServer().getPlayer(args[0]);
				if (p != null) {
					PseudoAPI.message.sendPluginMessage(sender, "Player: " + p.getName() + "'s Client Brand: " + ChatColor.RED + Utils.getBrand(p.getName()));
					return true;
				} else
					PseudoAPI.message.sendPluginError(sender, Errors.NOT_ONLINE, args[0]);
			} else
				PseudoAPI.message.sendPluginError(sender, Errors.CUSTOM, "Please specify a player!");
		} else
			PseudoAPI.message.sendPluginError(sender, Errors.NO_PERMISSION, "view user brand!");
		return false;
	}

}
