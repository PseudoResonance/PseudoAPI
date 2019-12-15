package io.github.pseudoresonance.pseudoapi.bukkit;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import io.github.pseudoresonance.pseudoapi.bukkit.Config.ConsoleFormat;
import io.github.pseudoresonance.pseudoapi.bukkit.language.LanguageManager;
import io.github.pseudoresonance.pseudoapi.bukkit.utils.ChatElement;
import net.md_5.bungee.api.ChatColor;

public class Chat {

	private static ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
	private PseudoPlugin plugin;

	protected Chat(PseudoPlugin plugin) {
		this.plugin = plugin;
	}

	public static void sendConsoleMessage(String message) {
		sendMessage(console, message);
	}

	public static void sendMessage(CommandSender sender, String message) {
		if (sender instanceof Player) {
			sender.sendMessage(message);
		} else {
			console.sendMessage(message);
		}
	}
	public static void sendMessage(List<? extends Object> messages) {
		sendMessage(console, messages);
	}

	public static void sendMessage(CommandSender sender, List<? extends Object> messages) {
		if (sender instanceof Player) {
			for (int i = 0; i < messages.size(); i++) {
				Object o = messages.get(i);
				if (o instanceof String) {
					sender.sendMessage((String) o);
				} else if (o instanceof ChatElement[]) {
					Chat.sendJSONMessage((Player) sender, (ChatElement[]) o);
				} else {
					throw new IllegalArgumentException("Not String or JSON");
				}
			}
		} else {
			if (Config.consoleFormat == ConsoleFormat.TOP) {
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

	public void broadcastPluginMessageWithPerission(String message, String permission) {
		String format = Config.messageFormat;
		format = format.replace("{message}", Config.textColor + message);
		format = format.replace("{name}", plugin.getPluginName());
		format = format.replace("{nickname}", plugin.getOutputName());
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.hasPermission(permission)) {
				String send = ChatColor.translateAlternateColorCodes('&', format);
				send = send.replace("{player}", p.getName());
				send = send.replace("{playernick}", p.getDisplayName());
				p.sendMessage(send);
			}
		}
		format = ChatColor.translateAlternateColorCodes('&', format);
		format = format.replace("{player}", "Console");
		format = format.replace("{playernick}", "Console");
		console.sendMessage(format);
	}

	public void broadcastPluginMessage(String message) {
		String format = Config.messageFormat;
		format = format.replace("{message}", Config.textColor + message);
		format = format.replace("{name}", plugin.getPluginName());
		format = format.replace("{nickname}", plugin.getOutputName());
		for (Player p : Bukkit.getOnlinePlayers()) {
			String send = ChatColor.translateAlternateColorCodes('&', format);
			send = send.replace("{player}", p.getName());
			send = send.replace("{playernick}", p.getDisplayName());
			p.sendMessage(send);
		}
		format = ChatColor.translateAlternateColorCodes('&', format);
		format = format.replace("{player}", "Console");
		format = format.replace("{playernick}", "Console");
		console.sendMessage(format);
	}
	
	public void sendConsolePluginMessage(String message) {
		sendPluginMessage(console, message);
	}

	public void sendPluginMessage(CommandSender sender, String message) {
		String format = Config.messageFormat;
		format = format.replace("{message}", Config.textColor + message);
		format = format.replace("{name}", plugin.getPluginName());
		format = format.replace("{nickname}", plugin.getOutputName());
		if (sender instanceof Player) {
			format = ChatColor.translateAlternateColorCodes('&', format);
			format = format.replace("{player}", ((Player) sender).getName());
			format = format.replace("{playernick}", ((Player) sender).getDisplayName());
			sender.sendMessage(format);
		} else {
			format = ChatColor.translateAlternateColorCodes('&', format);
			format = format.replace("{player}", "Console");
			format = format.replace("{playernick}", "Console");
			console.sendMessage(format);
		}
	}
	
	public void sendConsolePluginError(Errors error, String message) {
		sendPluginError(console, error, message);
	}

	public void sendPluginError(CommandSender sender, Errors error, String message) {
		String format = Config.errorMessageFormat;
		switch (error) {
			case CUSTOM:
				format = format.replace("{message}", Config.errorTextColor + message);
				break;
			case NEVER_JOINED:
				format = format.replace("{message}", Config.errorTextColor + LanguageManager.getLanguage(sender).getMessage("pseudoapi.errors_never_joined", message));
				break;
			case NOT_A_NUMBER:
				format = format.replace("{message}", Config.errorTextColor + LanguageManager.getLanguage(sender).getMessage("pseudoapi.errors_not_a_number", message));
				break;
			case NOT_LOADED:
				format = format.replace("{message}", Config.errorTextColor + LanguageManager.getLanguage(sender).getMessage("pseudoapi.errors_not_loaded", message));
				break;
			case NOT_ONLINE:
				format = format.replace("{message}", Config.errorTextColor + LanguageManager.getLanguage(sender).getMessage("pseudoapi.errors_not_online", message));
				break;
			case NO_PERMISSION:
				format = format.replace("{message}", Config.errorTextColor + LanguageManager.getLanguage(sender).getMessage("pseudoapi.errors_no_permission", message));
				break;
			case INVALID_SUBCOMMAND:
				format = format.replace("{message}", Config.errorTextColor + LanguageManager.getLanguage(sender).getMessage("pseudoapi.errors_invalid_subcommand", message));
				break;
			case GENERIC:
			default:
				format = format.replace("{message}", Config.errorTextColor + LanguageManager.getLanguage(sender).getMessage("pseudoapi.errors_generic"));
				break;
		}
		format = format.replace("{name}", plugin.getPluginName());
		format = format.replace("{nickname}", plugin.getOutputName());
		if (sender instanceof Player) {
			format = ChatColor.translateAlternateColorCodes('&', format);
			format = format.replace("{player}", ((Player) sender).getName());
			format = format.replace("{playernick}", ((Player) sender).getDisplayName());
			sender.sendMessage(format);
			try {
				((Player) sender).spawnParticle(Particle.FIREWORKS_SPARK, ((Player) sender).getEyeLocation(), 5, 0.8, 0.8, 0.8, 0);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			format = ChatColor.translateAlternateColorCodes('&', format);
			format = format.replace("{player}", "Console");
			format = format.replace("{playernick}", "Console");
			console.sendMessage(format);
		}
	}
	
	public void sendConsolePluginError(Errors error) {
		sendPluginError(console, error);
	}

	public void sendPluginError(CommandSender sender, Errors error) {
		String format = Config.errorMessageFormat;
		switch (error) {
			case GENERIC:
				format = format.replace("{message}", Config.errorTextColor + LanguageManager.getLanguage(sender).getMessage("pseudoapi.errors_generic"));
				break;
			default:
				throw new IllegalArgumentException("Only error generic allowed");
		}
		format = format.replace("{name}", plugin.getPluginName());
		format = format.replace("{nickname}", plugin.getOutputName());
		if (sender instanceof Player) {
			format = ChatColor.translateAlternateColorCodes('&', format);
			format = format.replace("{player}", ((Player) sender).getName());
			format = format.replace("{playernick}", ((Player) sender).getDisplayName());
			sender.sendMessage(format);
			try {
				((Player) sender).spawnParticle(Particle.FIREWORKS_SPARK, ((Player) sender).getEyeLocation(), 5, 0.8, 0.8, 0.8, 0);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			format = ChatColor.translateAlternateColorCodes('&', format);
			format = format.replace("{player}", "Console");
			format = format.replace("{playernick}", "Console");
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

	public enum Errors {
		NO_PERMISSION, NOT_A_NUMBER, NOT_ONLINE, NEVER_JOINED, NOT_LOADED, INVALID_SUBCOMMAND, CUSTOM, GENERIC;
	}

}
