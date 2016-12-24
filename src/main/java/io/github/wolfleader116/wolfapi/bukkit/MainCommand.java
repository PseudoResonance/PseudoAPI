package io.github.wolfleader116.wolfapi.bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MainCommand implements CommandExecutor {
	
	protected WolfPlugin plugin;
	
	public MainCommand(WolfPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 0) {
			List<Object> messages = new ArrayList<Object>();
			messages.add(ConfigOptions.border + "===---" + ConfigOptions.title + plugin.getPluginName() + " Info" + ConfigOptions.border + "---===");
			messages.add(ConfigOptions.description + plugin.getPluginName() + " Version " + plugin.getVersion() + ".");
			String developers = "";
			for (int i = 0; i <= plugin.getAuthors().size() - 1; i++) {
				if (i >= 1) {
					developers = developers + ", ";
				}
				developers = developers + plugin.getAuthors().get(i);
			}
			messages.add(ConfigOptions.description + "Was developed by " + developers + ".");
			messages.add(new ElementBuilder(new ChatElement(ConfigOptions.description + "Use "), new ChatElement(ConfigOptions.command + "/" + cmd.getName() + " help", new ChatComponent(ConfigOptions.clickEvent, "/" + plugin.getOutputName() + ":" + cmd.getName() + " help"), new ChatComponent(ComponentType.SHOW_TEXT, ConfigOptions.description + "Click to run the command!")), new ChatElement(ConfigOptions.description + " for commands.")).build());
			Message.sendMessage(sender, messages);
			return true;
		} else {
			Map<String, SubCommandExecutor> subCommands = plugin.subCommands;
			for (String sc : subCommands.keySet()) {
				if (args[0].equalsIgnoreCase(sc)) {
					List<String> argsList = new ArrayList<String>();
					for (String s : args) {
						argsList.add(s);
					}
					argsList.remove(0);
					String[] newArgs = argsList.toArray(new String[argsList.size()]);
					subCommands.get(sc).onCommand(sender, cmd, label, newArgs);
					return true;
				}
			}
		}
		return false;
	}

}