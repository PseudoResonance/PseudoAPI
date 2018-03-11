package io.github.pseudoresonance.pseudoapi.bukkit;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class Message {
	
	private static ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
	private PseudoPlugin plugin;
	
	public Message(PseudoPlugin plugin) {
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
				} else if (o instanceof ChatElement[]) {
					Message.sendJSONMessage((Player) sender, (ChatElement[]) o);
				} else {
					throw new IllegalArgumentException("Not String or JSON");
				}
			}
		} else {
			if (ConfigOptions.consoleFormat == ConsoleFormat.TOP) {
				for (int i = messages.size() - 1; i >= 0; i--) {
					Object o = messages.get(i);
					if (o instanceof String) {
						console.sendMessage((String) o);
					} else if (o instanceof ChatElement[]) {
						console.sendMessage(ChatElement.toText((ChatElement[]) o));
					} else {
						throw new IllegalArgumentException("Not String or JSON");
					}
				}
			} else {
				for (int i = 0; i < messages.size(); i++) {
					Object o = messages.get(i);
					if (o instanceof String) {
						console.sendMessage((String) o);
					} else if (o instanceof ChatElement[]) {
						console.sendMessage(ChatElement.toText((ChatElement[]) o));
					} else {
						throw new IllegalArgumentException("Not String or JSON");
					}
				}
			}
		}
	}
	
	public void sendPluginMessage(CommandSender sender, String message) {
		String format = ConfigOptions.messageFormat;
		format = format.replace("{message}", ConfigOptions.text + message);
		format = format.replace("{name}", ConfigOptions.prefix + plugin.getPluginName());
		format = format.replace("{nickname}", ConfigOptions.prefix + plugin.getOutputName());
		if (sender instanceof Player) {
			format = format.replace("{player}", ((Player) sender).getName());
			format = format.replace("{playernick}", ((Player) sender).getDisplayName());
			format = ChatColor.translateAlternateColorCodes('&', format);
			sender.sendMessage(format);
		} else {
			format = format.replace("{player}", "Console");
			format = format.replace("{playernick}", "Console");
			format = ChatColor.translateAlternateColorCodes('&', format);
			console.sendMessage(format);
		}
	}
	
	public void sendPluginError(CommandSender sender, Errors error, String message) {
		String format = ConfigOptions.messageFormat;
		switch(error) {
		case GENERIC:
			if (sender instanceof Player) {
				try {
					((Player) sender).spawnParticle(Particle.FIREWORKS_SPARK, ((Player) sender).getEyeLocation(), 5, 0.8, 0.8, 0.8, 0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			format = format.replace("{message}", ConfigOptions.error + "An unknown error has occured! Please contact the developer!");
			break;
		case CUSTOM:
			if (sender instanceof Player) {
				try {
					((Player) sender).spawnParticle(Particle.FIREWORKS_SPARK, ((Player) sender).getEyeLocation(), 5, 0.8, 0.8, 0.8, 0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			format = format.replace("{message}", ConfigOptions.error + message);
			break;
		case NEVER_JOINED:
			if (sender instanceof Player) {
				try {
					((Player) sender).spawnParticle(Particle.FIREWORKS_SPARK, ((Player) sender).getEyeLocation(), 5, 0.8, 0.8, 0.8, 0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			format = format.replace("{message}", ConfigOptions.error + message + " has never joined!");
			break;
		case NOT_A_NUMBER:
			if (sender instanceof Player) {
				try {
					((Player) sender).spawnParticle(Particle.FIREWORKS_SPARK, ((Player) sender).getEyeLocation(), 5, 0.8, 0.8, 0.8, 0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			format = format.replace("{message}", ConfigOptions.error + message + " is not a number!");
			break;
		case NOT_LOADED:
			if (sender instanceof Player) {
				try {
					((Player) sender).spawnParticle(Particle.FIREWORKS_SPARK, ((Player) sender).getEyeLocation(), 5, 0.8, 0.8, 0.8, 0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			format = format.replace("{message}", ConfigOptions.error + "Plugin " + message + " is not loaded!");
			break;
		case NOT_ONLINE:
			if (sender instanceof Player) {
				try {
					((Player) sender).spawnParticle(Particle.FIREWORKS_SPARK, ((Player) sender).getEyeLocation(), 5, 0.8, 0.8, 0.8, 0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			format = format.replace("{message}", ConfigOptions.error + message + " is not online!");
			break;
		case NO_PERMISSION:
			if (sender instanceof Player) {
				try {
					((Player) sender).spawnParticle(Particle.FIREWORKS_SPARK, ((Player) sender).getEyeLocation(), 5, 0.8, 0.8, 0.8, 0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			format = format.replace("{message}", ConfigOptions.error + "You do not have permission to " + message);
			break;
		default:
			if (sender instanceof Player) {
				try {
					((Player) sender).spawnParticle(Particle.FIREWORKS_SPARK, ((Player) sender).getEyeLocation(), 5, 0.8, 0.8, 0.8, 0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			format = format.replace("{message}", ConfigOptions.error + "An unknown error has occured! Please contact the developer!");
			break;
		}
		format = format.replace("{name}", ConfigOptions.errorPrefix + plugin.getPluginName());
		format = format.replace("{nickname}", ConfigOptions.errorPrefix + plugin.getOutputName());
		if (sender instanceof Player) {
			format = format.replace("{player}", ((Player) sender).getName());
			format = format.replace("{playernick}", ((Player) sender).getDisplayName());
			format = ChatColor.translateAlternateColorCodes('&', format);
			sender.sendMessage(format);
		} else {
			format = format.replace("{player}", "Console");
			format = format.replace("{playernick}", "Console");
			format = ChatColor.translateAlternateColorCodes('&', format);
			console.sendMessage(format);
		}
	}
	
	public void sendPluginError(CommandSender sender, Errors error) {
		String format = ConfigOptions.messageFormat;
		switch(error) {
		case GENERIC:
			if (sender instanceof Player) {
				try {
					((Player) sender).spawnParticle(Particle.FIREWORKS_SPARK, ((Player) sender).getEyeLocation(), 5, 0.8, 0.8, 0.8, 0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			format = format.replace("{message}", ConfigOptions.error + "An unknown error has occured! Please contact the developer!");
			break;
		default:
			throw new IllegalArgumentException("Only error generic allowed");
		}
		format = format.replace("{name}", ConfigOptions.errorPrefix + plugin.getPluginName());
		format = format.replace("{nickname}", ConfigOptions.errorPrefix + plugin.getOutputName());
		if (sender instanceof Player) {
			format = format.replace("{player}", ((Player) sender).getName());
			format = format.replace("{playernick}", ((Player) sender).getDisplayName());
			format = ChatColor.translateAlternateColorCodes('&', format);
			sender.sendMessage(format);
		} else {
			format = format.replace("{player}", "Console");
			format = format.replace("{playernick}", "Console");
			format = ChatColor.translateAlternateColorCodes('&', format);
			console.sendMessage(format);
		}
	}
	
	public static void sendJSONMessage(Player p, ChatElement... elements) throws NullPointerException {
		if (p != null) {
			String end = "[\"\"";
			if (elements.length == 0) {
				end = end + "]";
			} else {
				for (ChatElement e : elements) {
					end = end + e.build();
				}
				end = end + "]";
			}
			Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "tellraw " + p.getName() + " " + end);
		} else {
			throw new NullPointerException("Player not online");
		}
	}
	
	public static void broadcastJSONMessage(ChatElement... elements) {
		String end = "[\"\"";
		if (elements.length == 0) {
			end = end + "]";
		} else {
			for (ChatElement e : elements) {
				end = end + e.build();
			}
			end = end + "]";
		}
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "tellraw " + p.getName() + " " + end);
		}
	}
	
	public static void sendJSONMessage(Player p, List<ChatElement> elements) throws NullPointerException {
		if (p != null) {
			String end = "[\"\"";
			if (elements.size() == 0) {
				end = end + "]";
			} else {
				for (ChatElement e : elements) {
					end = end + e.build();
				}
				end = end + "]";
			}
			Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "tellraw " + p.getName() + " " + end);
		} else {
			throw new NullPointerException("Player not online");
		}
	}
	
	public static void broadcastJSONMessage(List<ChatElement> elements) {
		String end = "[\"\"";
		if (elements.size() == 0) {
			end = end + "]";
		} else {
			for (ChatElement e : elements) {
				end = end + e.build();
			}
			end = end + "]";
		}
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "tellraw " + p.getName() + " " + end);
		}
	}

}
