package io.github.pseudoresonance.pseudoapi.bukkit.commands;

import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.pseudoresonance.pseudoapi.bukkit.SubCommandExecutor;
import io.github.pseudoresonance.pseudoapi.bukkit.Config;
import io.github.pseudoresonance.pseudoapi.bukkit.Message.Errors;
import io.github.pseudoresonance.pseudoapi.bukkit.data.Backend;
import io.github.pseudoresonance.pseudoapi.bukkit.data.FileBackend;
import io.github.pseudoresonance.pseudoapi.bukkit.data.MySQLBackend;
import io.github.pseudoresonance.pseudoapi.bukkit.playerdata.PlayerDataController;
import io.github.pseudoresonance.pseudoapi.bukkit.PseudoAPI;

public class BackendSC implements SubCommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) || sender.hasPermission("pseudoapi.backend")) {
			if (args.length == 0) {
				PseudoAPI.message.sendPluginError(sender, Errors.CUSTOM, "Please choose either 'list' or 'migrate <from> <to>'");
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
								backendString += name + ": File";
							} else
								backendString += ", " + name + ": File";
						} else if (b instanceof MySQLBackend) {
							if (first) {
								first = false;
								backendString += name + ": MySQL";
							} else
								backendString += ", " + name + ": MySQL";
						}
					}
					PseudoAPI.message.sendPluginMessage(sender, "Backends: " + backendString);
				} else if (args[0].equalsIgnoreCase("migrate")) {
					if (args.length == 1) {
						PseudoAPI.message.sendPluginError(sender, Errors.CUSTOM, "Please add a backend to migrate from and to");
						return false;
					} else if (args.length == 2) {
						PseudoAPI.message.sendPluginError(sender, Errors.CUSTOM, "Please add a backend to migrate to");
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
							PseudoAPI.message.sendPluginError(sender, Errors.CUSTOM, args[1] + " is an invalid backend! Try using 'backend list'");
							return false;
						}
						if (destination == null) {
							PseudoAPI.message.sendPluginError(sender, Errors.CUSTOM, args[2] + " is an invalid backend! Try using 'backend list'");
							return false;
						}
						PlayerDataController.migrateBackends(origin, destination);
						PseudoAPI.message.sendPluginMessage(sender, "Migrated data from backend: " + origin.getName() + " to: " + destination.getName() + "!");
						return true;
					}
				}
			}
		} else
			PseudoAPI.message.sendPluginError(sender, Errors.NO_PERMISSION, "modify backends!");
		return false;
	}

}
