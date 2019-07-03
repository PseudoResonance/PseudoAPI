package io.github.pseudoresonance.pseudoapi.bukkit.tabcompleters;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import io.github.pseudoresonance.pseudoapi.bukkit.Config;
import io.github.pseudoresonance.pseudoapi.bukkit.PluginController;
import io.github.pseudoresonance.pseudoapi.bukkit.PseudoPlugin;

public class PseudoAPITC implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> possible = new ArrayList<String>();
		if (args.length == 1) {
			possible.add("help");
			if (sender.hasPermission("pseudoapi.reload")) {
				possible.add("reload");
			}
			if (sender.hasPermission("pseudoapi.reset")) {
				possible.add("reset");
			}
			if (sender.hasPermission("pseudoapi.metrics")) {
				possible.add("metrics");
			}
			if (sender.hasPermission("pseudoapi.brand")) {
				possible.add("brand");
			}
			if (sender.hasPermission("pseudoapi.backend")) {
				possible.add("backend");
			}
			if (sender.hasPermission("pseudoapi.update")) {
				possible.add("update");
			}
			if (args[0].equalsIgnoreCase("")) {
				return possible;
			} else {
				List<String> checked = new ArrayList<String>();
				for (String check : possible) {
					if (check.toLowerCase().startsWith(args[0].toLowerCase())) {
						checked.add(check);
					}
				}
				return checked;
			}
		} else if (args.length == 2) {
			if (args[0].equalsIgnoreCase("backend")) {
				if (sender.hasPermission("pseudoapi.backend")) {
					possible.add("list");
					possible.add("migrate");
				}
			} else if (args[0].equalsIgnoreCase("update")) {
				if (sender.hasPermission("pseudoapi.update")) {
					for (PseudoPlugin p : PluginController.getPlugins()) {
						possible.add(p.getName());
					}
				}
			}
			if (args[1].equalsIgnoreCase("")) {
				return possible;
			} else {
				List<String> checked = new ArrayList<String>();
				for (String check : possible) {
					if (check.toLowerCase().startsWith(args[1].toLowerCase())) {
						checked.add(check);
					}
				}
				return checked;
			}
		} else if (args.length == 3) {
			if (args[0].equalsIgnoreCase("backend") && args[1].equalsIgnoreCase("migrate")) {
				if (sender.hasPermission("pseudoapi.backend")) {
					possible.addAll(Config.getBackends().keySet());
				}
			}
			if (args[2].equalsIgnoreCase("")) {
				return possible;
			} else {
				List<String> checked = new ArrayList<String>();
				for (String check : possible) {
					if (check.toLowerCase().startsWith(args[2].toLowerCase())) {
						checked.add(check);
					}
				}
				return checked;
			}
		} else if (args.length == 4) {
			if (args[0].equalsIgnoreCase("backend") && args[1].equalsIgnoreCase("migrate")) {
				if (sender.hasPermission("pseudoapi.backend")) {
					possible.addAll(Config.getBackends().keySet());
				}
			}
			if (args[3].equalsIgnoreCase("")) {
				return possible;
			} else {
				List<String> checked = new ArrayList<String>();
				for (String check : possible) {
					if (check.toLowerCase().startsWith(args[3].toLowerCase())) {
						checked.add(check);
					}
				}
				return checked;
			}
		}
		return null;
	}

}
