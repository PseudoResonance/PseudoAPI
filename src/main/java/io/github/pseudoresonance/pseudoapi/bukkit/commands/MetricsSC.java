package io.github.pseudoresonance.pseudoapi.bukkit.commands;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.pseudoresonance.pseudoapi.bukkit.Message.Errors;
import io.github.pseudoresonance.pseudoapi.bukkit.ConfigOptions;
import io.github.pseudoresonance.pseudoapi.bukkit.Message;
import io.github.pseudoresonance.pseudoapi.bukkit.PseudoAPI;
import io.github.pseudoresonance.pseudoapi.bukkit.SubCommandExecutor;
import io.github.pseudoresonance.pseudoapi.bukkit.Utils;

public class MetricsSC implements SubCommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) || (sender.hasPermission("pseudoapi.metrics"))) {
			List<Object> messages = new ArrayList<Object>();
			OperatingSystemMXBean osb = ManagementFactory.getOperatingSystemMXBean();
			RuntimeMXBean rsb = ManagementFactory.getRuntimeMXBean();
			messages.add(ConfigOptions.border + "===---" + ConfigOptions.title + "Server Metrics" + ConfigOptions.border + "---===");
			if (!(sender instanceof Player) || (sender.hasPermission("pseudoapi.metrics.tps"))) {
				messages.add(ConfigOptions.description + "TPS: " + ConfigOptions.command + Utils.getTps());
				messages.add(ConfigOptions.description + "Uptime: " + ConfigOptions.command + Utils.millisToHumanFormat(rsb.getUptime()));
			}
			if (!(sender instanceof Player) || (sender.hasPermission("pseudoapi.metrics.java"))) {
				messages.add(ConfigOptions.border + "---" + ConfigOptions.title + "Java & OS" + ConfigOptions.border + "---");
				messages.add(ConfigOptions.description + "Java Version: " + ConfigOptions.command + System.getProperty("java.version"));
				messages.add(ConfigOptions.description + "Java Vendor: " + ConfigOptions.command + System.getProperty("java.vendor"));
				messages.add(ConfigOptions.description + "OS: " + ConfigOptions.command + osb.getName() + " " + osb.getVersion() + " " + osb.getArch());
				messages.add(ConfigOptions.description + "System Time: " + ConfigOptions.command + LocalDateTime.now().format(DateTimeFormatter.ofPattern(ConfigOptions.timeFormat)));
				messages.add(ConfigOptions.description + "System Date: " + ConfigOptions.command + LocalDateTime.now().format(DateTimeFormatter.ofPattern(ConfigOptions.dateFormat)));
			}
			if (!(sender instanceof Player) || (sender.hasPermission("pseudoapi.metrics.cpu"))) {
				messages.add(ConfigOptions.border + "---" + ConfigOptions.title + "CPU" + ConfigOptions.border + "---");
				messages.add(ConfigOptions.description + "CPU Cores: " + ConfigOptions.command + osb.getAvailableProcessors());
				double load = osb.getSystemLoadAverage();
				messages.add(ConfigOptions.description + "CPU Load: " + ConfigOptions.command + (load >= 0 ? load + "%" : "Unknown"));
			}
			if (!(sender instanceof Player) || (sender.hasPermission("pseudoapi.metrics.memory"))) {
				messages.add(ConfigOptions.border + "---" + ConfigOptions.title + "Memory" + ConfigOptions.border + "---");
				messages.add(ConfigOptions.description + "Used Memory: " + ConfigOptions.command + bytesToHumanFormat(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory(), false));
				messages.add(ConfigOptions.description + "Free Memory: " + ConfigOptions.command + bytesToHumanFormat(Runtime.getRuntime().freeMemory(), false));
				messages.add(ConfigOptions.description + "Total Memory: " + ConfigOptions.command + bytesToHumanFormat(Runtime.getRuntime().totalMemory(), false));
				messages.add(ConfigOptions.description + "Max Memory: " + ConfigOptions.command + (Runtime.getRuntime().maxMemory() == Long.MAX_VALUE ? "Unlimited" : bytesToHumanFormat(Runtime.getRuntime().maxMemory(), false)));
			}
			Message.sendMessage(sender, messages);
			return true;
		} else {
			PseudoAPI.message.sendPluginError(sender, Errors.NO_PERMISSION, "view server metrics!");
		}
		return false;
	}

	private static String bytesToHumanFormat(long bytes, boolean si) {
		int unit = si ? 1000 : 1024;
		if (bytes < unit)
			return bytes + " B";
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}

}
