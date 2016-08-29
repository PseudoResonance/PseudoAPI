package io.github.wolfleader116.wolfapi.bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MainCommand implements CommandExecutor {
	
	protected WolfPlugin plugin;
	
	MainCommand(WolfPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 0) {
			List<String> messages = new ArrayList<String>();
			messages.add(WolfAPI.border + "===---" + WolfAPI.title + plugin.getPluginName() + " Info" + WolfAPI.border + "---===");
			messages.add(WolfAPI.description + plugin.getPluginName() + " Version " + plugin.getVersion() + ".");
			String developers = "";
			for (int i = 0; i <= plugin.getAuthors().size() - 1; i++) {
				if (i >= 1) {
					developers = developers + ", ";
				}
				developers = developers + plugin.getAuthors().get(i);
			}
			messages.add(WolfAPI.description + "Was developed by " + developers + ".");
			messages.add(WolfAPI.description + "Use " + WolfAPI.command + "/" + cmd.getName() + " help " + WolfAPI.description + "for commands.");
			return true;
		} else {
			Map<String, SubCommandExecutor> subCommands = plugin.subCommands;
			for (String sc : subCommands.keySet()) {
				if (args[0].equalsIgnoreCase(sc)) {
					String[] newArgs = (String[]) ArrayUtils.removeElement(args, 0);
					subCommands.get(sc).onCommand(sender, cmd, label, newArgs);
					return true;
				}
			}
		}
		return false;
	}

}