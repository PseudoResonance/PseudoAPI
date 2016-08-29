package io.github.wolfleader116.wolfapi.bukkit;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;

public class Message {
	
	private static ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
	private WolfPlugin plugin;
	
	public Message(WolfPlugin plugin) {
		this.plugin = plugin;
	}
	
	public static void sendConsoleMessage(String message) {
		console.sendMessage(message);
	}
	
	public static void sendMessage(CommandSender sender, String message) {
		if (sender instanceof Player) {
			sender.sendMessage(message);
		} else {
			console.sendMessage(message);
		}
	}
	
	public static void sendMessage(CommandSender sender, List<? extends Object> messages) {
		if (sender instanceof Player) {
			for (int i = 0; i < messages.size(); i++) {
				Object o = messages.get(i);
				if (o instanceof String) {
					sender.sendMessage((String) o);
				} else if (o instanceof BaseComponent[]) {
					Message.sendJSONMessage((Player) sender, (BaseComponent[]) o);
				} else {
					throw new IllegalArgumentException("Not String or JSON");
				}
			}
		} else {
			for (int i = messages.size() - 1; i >= 0; i--) {
				Object o = messages.get(i);
				if (o instanceof String) {
					console.sendMessage((String) o);
				} else if (o instanceof BaseComponent[]) {
					console.sendMessage(((BaseComponent) o).toLegacyText());
				} else {
					throw new IllegalArgumentException("Not String or JSON");
				}
			}
		}
	}
	
	public void sendPluginMessage(CommandSender sender, String message) {
		String format = WolfAPI.plugin.getConfig().getString("MessageFormat");
		format = format.replaceAll("{message}", message);
		format = format.replaceAll("{name}", plugin.getPluginName());
		format = format.replaceAll("{nickname}", plugin.getOutputName());
		if (sender instanceof Player) {
			format = format.replaceAll("{player}", ((Player) sender).getName());
			format = format.replaceAll("{playernick}", ((Player) sender).getDisplayName());
			format = ChatColor.translateAlternateColorCodes('&', format);
			sender.sendMessage(format);
		} else {
			format = format.replaceAll("{player}", "Console");
			format = format.replaceAll("{playernick}", "Console");
			format = ChatColor.translateAlternateColorCodes('&', format);
			console.sendMessage(format);
		}
	}
	
	public void sendPluginError(CommandSender sender, Errors error, String message) {
		String format = WolfAPI.plugin.getConfig().getString("MessageFormat");
		switch(error) {
		case GENERIC:
			if (sender instanceof Player) {
				try {
					ParticleEffect.FIREWORKS_SPARK.sendToPlayer((Player) sender, ((Player) sender).getLocation(), (float) 0.8, (float) 0.8, (float) 0.8, (float) 0, 5);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			format = format.replaceAll("{message}", "An unknown error has occured! Please contact the developer!");
			break;
		case CUSTOM:
			if (sender instanceof Player) {
				try {
					ParticleEffect.FIREWORKS_SPARK.sendToPlayer((Player) sender, ((Player) sender).getLocation(), (float) 0.8, (float) 0.8, (float) 0.8, (float) 0, 5);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			format = format.replaceAll("{message}", message);
			break;
		case NEVER_JOINED:
			if (sender instanceof Player) {
				try {
					ParticleEffect.FIREWORKS_SPARK.sendToPlayer((Player) sender, ((Player) sender).getLocation(), (float) 0.8, (float) 0.8, (float) 0.8, (float) 0, 5);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			format = format.replaceAll("{message}", message + " has never joined!");
			break;
		case NOT_A_NUMBER:
			if (sender instanceof Player) {
				try {
					ParticleEffect.FIREWORKS_SPARK.sendToPlayer((Player) sender, ((Player) sender).getLocation(), (float) 0.8, (float) 0.8, (float) 0.8, (float) 0, 5);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			format = format.replaceAll("{message}", message + " is not a number!");
			break;
		case NOT_LOADED:
			if (sender instanceof Player) {
				try {
					ParticleEffect.FIREWORKS_SPARK.sendToPlayer((Player) sender, ((Player) sender).getLocation(), (float) 0.8, (float) 0.8, (float) 0.8, (float) 0, 5);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			format = format.replaceAll("{message}", "Plugin " + message + " is not loaded!");
			break;
		case NOT_ONLINE:
			if (sender instanceof Player) {
				try {
					ParticleEffect.FIREWORKS_SPARK.sendToPlayer((Player) sender, ((Player) sender).getLocation(), (float) 0.8, (float) 0.8, (float) 0.8, (float) 0, 5);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			format = format.replaceAll("{message}", message + " is not online!");
			break;
		case NO_PERMISSION:
			if (sender instanceof Player) {
				try {
					ParticleEffect.FIREWORKS_SPARK.sendToPlayer((Player) sender, ((Player) sender).getLocation(), (float) 0.8, (float) 0.8, (float) 0.8, (float) 0, 5);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			format = format.replaceAll("{message}", "You do not have permission to " + message);
			break;
		default:
			if (sender instanceof Player) {
				try {
					ParticleEffect.FIREWORKS_SPARK.sendToPlayer((Player) sender, ((Player) sender).getLocation(), (float) 0.8, (float) 0.8, (float) 0.8, (float) 0, 5);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			format = format.replaceAll("{message}", "An unknown error has occured! Please contact the developer!");
			break;
		}
		format = format.replaceAll("{name}", plugin.getPluginName());
		format = format.replaceAll("{nickname}", plugin.getOutputName());
		if (sender instanceof Player) {
			format = format.replaceAll("{player}", ((Player) sender).getName());
			format = format.replaceAll("{playernick}", ((Player) sender).getDisplayName());
			format = ChatColor.translateAlternateColorCodes('&', format);
			sender.sendMessage(format);
		} else {
			format = format.replaceAll("{player}", "Console");
			format = format.replaceAll("{playernick}", "Console");
			format = ChatColor.translateAlternateColorCodes('&', format);
			console.sendMessage(format);
		}
	}
	
	public void sendPluginError(CommandSender sender, Errors error) {
		String format = WolfAPI.plugin.getConfig().getString("MessageFormat");
		switch(error) {
		case GENERIC:
			if (sender instanceof Player) {
				try {
					ParticleEffect.FIREWORKS_SPARK.sendToPlayer((Player) sender, ((Player) sender).getLocation(), (float) 0.8, (float) 0.8, (float) 0.8, (float) 0, 5);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			format = format.replaceAll("{message}", "An unknown error has occured! Please contact the developer!");
			break;
		default:
			throw new IllegalArgumentException("Only error generic allowed");
		}
		format = format.replaceAll("{name}", plugin.getPluginName());
		format = format.replaceAll("{nickname}", plugin.getOutputName());
		if (sender instanceof Player) {
			format = format.replaceAll("{player}", ((Player) sender).getName());
			format = format.replaceAll("{playernick}", ((Player) sender).getDisplayName());
			format = ChatColor.translateAlternateColorCodes('&', format);
			sender.sendMessage(format);
		} else {
			format = format.replaceAll("{player}", "Console");
			format = format.replaceAll("{playernick}", "Console");
			format = ChatColor.translateAlternateColorCodes('&', format);
			console.sendMessage(format);
		}
	}
	
	public static void sendJSONMessage(Player p, BaseComponent[] json) throws NullPointerException {
		if (p != null) {
			Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "tellraw " + p.getName() + " " + BaseComponent.toLegacyText(json));
		} else {
			throw new NullPointerException();
		}
	}
	
	public static void broadcastJSONMessage(BaseComponent[] json) {
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "tellraw " + p.getName() + BaseComponent.toLegacyText(json));
		}
	}

}
