package io.github.pseudoresonance.pseudoapi.bukkit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public interface SubCommandExecutor extends CommandExecutor {
	
	public abstract boolean onCommand(CommandSender sender, Command cmd, String label, String[] args);

}
