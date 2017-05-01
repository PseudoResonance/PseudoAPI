package io.github.wolfleader116.wolfapi.bukkit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.github.wolfleader116.wolfapi.bukkit.data.Data;
import net.md_5.bungee.api.ChatColor;

public class ConfigOptions implements ConfigOption {
	
	public static String border = "";
	public static String title = "";
	public static String command = "";
	public static String description = "";
	public static String text = "";
	public static String prefix = "";
	public static String error = "";
	public static String errorPrefix = "";
	
	public static String messageFormat = "";
	public static ConsoleFormat consoleFormat = ConsoleFormat.BOTTOM;
	
	public static ComponentType clickEvent = ComponentType.RUN_COMMAND;
	
	public static boolean hidePlugins = true;
	public static boolean allowWolfAPI = true;
	
	public static boolean bungeeEnabled = false;
	
	public static boolean updateConfig() {
		if (WolfAPI.plugin.getConfig().getInt("Version") == 5) {
			Message.sendConsoleMessage(ChatColor.GREEN + "Config is up to date!");
		} else {
			try {
				String oldFile = "";
				File conf = new File(WolfAPI.plugin.getDataFolder(), "config.yml");
				if (new File(WolfAPI.plugin.getDataFolder(), "config.yml.old").exists()) {
					for (int i = 1; i > 0; i++) {
						if (!(new File(WolfAPI.plugin.getDataFolder(), "config.yml.old" + i).exists())) {
							conf.renameTo(new File(WolfAPI.plugin.getDataFolder(), "config.yml.old" + i));
							oldFile = "config.yml.old" + i;
							break;
						}
					}
				} else {
					conf.renameTo(new File(WolfAPI.plugin.getDataFolder(), "config.yml.old"));
					oldFile = "config.yml.old";
				}
				WolfAPI.plugin.saveDefaultConfig();
				WolfAPI.plugin.reloadConfig();
				WolfAPI.getConfigOptions().reloadConfig();
				Message.sendConsoleMessage(ChatColor.GREEN + "Config is up to date! Old config file renamed to " + oldFile + ".");
			} catch (Exception e) {
				Message.sendConsoleMessage(ChatColor.RED + "Error while updating config!");
				return false;
			}
		}
		return true;
	}
	
	public void reloadConfig() {
		try {
			String s = WolfAPI.plugin.getConfig().getString("BungeeEnabled");
			if (s.equalsIgnoreCase("true")) {
				bungeeEnabled = true;
			} else if (s.equalsIgnoreCase("false")) {
				bungeeEnabled = false;
			} else {
				bungeeEnabled = false;
				Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for BungeeEnabled!");
			}
		} catch (Exception e) {
			Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for BungeeEnabled!");
		}
		String mf = WolfAPI.plugin.getConfig().getString("MessageFormat");
		if (mf == null) {
			Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for MessageFormat!");
			messageFormat = "{nickname}> {message}";
		} else {
			messageFormat = mf;
		}
		if (WolfAPI.plugin.getConfig().getString("ConsoleFormat").equalsIgnoreCase("top")) {
			consoleFormat = ConsoleFormat.TOP;
		} else if (WolfAPI.plugin.getConfig().getString("ConsoleFormat").equalsIgnoreCase("bottom")) {
			consoleFormat = ConsoleFormat.BOTTOM;
		} else {
			consoleFormat = ConsoleFormat.BOTTOM;
			Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for ConsoleFormat!");
		}
		try {
			String s = WolfAPI.plugin.getConfig().getString("HidePlugins");
			if (s.equalsIgnoreCase("true")) {
				hidePlugins = true;
			} else if (s.equalsIgnoreCase("false")) {
				hidePlugins = false;
			} else {
				hidePlugins = false;
				Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for HidePlugins!");
			}
		} catch (Exception e) {
			Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for HidePlugins!");
		}
		try {
			String s = WolfAPI.plugin.getConfig().getString("AllowWolfAPI");
			if (s.equalsIgnoreCase("true")) {
				allowWolfAPI = true;
			} else if (s.equalsIgnoreCase("false")) {
				allowWolfAPI = false;
			} else {
				allowWolfAPI = false;
				Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for AllowWolfAPI!");
			}
		} catch (Exception e) {
			Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for AllowWolfAPI!");
		}
		String[] borders = WolfAPI.plugin.getConfig().getString("BorderColor").split(",");
		List<ChatColor> borderl = new ArrayList<ChatColor>();
		for (String s : borders) {
			try {
				if (s.length() == 1) {
					if (ChatColor.getByChar(s.charAt(0)) == null) {
						Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for BorderColor!");
					} else {
						borderl.add(ChatColor.getByChar(s.charAt(0)));
					}
				} else {
					borderl.add(Enum.valueOf(ChatColor.class, s.toUpperCase()));
				}
			} catch (Exception e) {
				Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for BorderColor!");
			}
		}
		border = arrayToString(borderl.toArray(new ChatColor[borderl.size()]));
		String[] titles = WolfAPI.plugin.getConfig().getString("TitleColor").split(",");
		List<ChatColor> titlel = new ArrayList<ChatColor>();
		for (String s : titles) {
			try {
				if (s.length() == 1) {
					if (ChatColor.getByChar(s.charAt(0)) == null) {
						Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for TitleColor!");
					} else {
						titlel.add(ChatColor.getByChar(s.charAt(0)));
					}
				} else {
					titlel.add(Enum.valueOf(ChatColor.class, s.toUpperCase()));
				}
			} catch (Exception e) {
				Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for TitleColor!");
			}
		}
		title = arrayToString(titlel.toArray(new ChatColor[titlel.size()]));
		String[] commands = WolfAPI.plugin.getConfig().getString("CommandColor").split(",");
		List<ChatColor> commandl = new ArrayList<ChatColor>();
		for (String s : commands) {
			try {
				if (s.length() == 1) {
					if (ChatColor.getByChar(s.charAt(0)) == null) {
						Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for CommandColor!");
					} else {
						commandl.add(ChatColor.getByChar(s.charAt(0)));
					}
				} else {
					commandl.add(Enum.valueOf(ChatColor.class, s.toUpperCase()));
				}
			} catch (Exception e) {
				Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for CommandColor!");
			}
		}
		command = arrayToString(commandl.toArray(new ChatColor[commandl.size()]));
		String[] descriptions = WolfAPI.plugin.getConfig().getString("DescriptionColor").split(",");
		List<ChatColor> descriptionl = new ArrayList<ChatColor>();
		for (String s : descriptions) {
			try {
				if (s.length() == 1) {
					if (ChatColor.getByChar(s.charAt(0)) == null) {
						Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for DescriptionColor!");
					} else {
						descriptionl.add(ChatColor.getByChar(s.charAt(0)));
					}
				} else {
					descriptionl.add(Enum.valueOf(ChatColor.class, s.toUpperCase()));
				}
			} catch (Exception e) {
				Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for DescriptionColor!");
			}
		}
		description = arrayToString(descriptionl.toArray(new ChatColor[descriptionl.size()]));
		String[] texts = WolfAPI.plugin.getConfig().getString("TextColor").split(",");
		List<ChatColor> textl = new ArrayList<ChatColor>();
		for (String s : texts) {
			try {
				if (s.length() == 1) {
					if (ChatColor.getByChar(s.charAt(0)) == null) {
						Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for TextColor!");
					} else {
						textl.add(ChatColor.getByChar(s.charAt(0)));
					}
				} else {
					textl.add(Enum.valueOf(ChatColor.class, s.toUpperCase()));
				}
			} catch (Exception e) {
				Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for TextColor!");
			}
		}
		text = arrayToString(textl.toArray(new ChatColor[textl.size()]));
		String[] prefixs = WolfAPI.plugin.getConfig().getString("PrefixColor").split(",");
		List<ChatColor> prefixl = new ArrayList<ChatColor>();
		for (String s : prefixs) {
			try {
				if (s.length() == 1) {
					if (ChatColor.getByChar(s.charAt(0)) == null) {
						Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for PrefixColor!");
					} else {
						prefixl.add(ChatColor.getByChar(s.charAt(0)));
					}
				} else {
					prefixl.add(Enum.valueOf(ChatColor.class, s.toUpperCase()));
				}
			} catch (Exception e) {
				Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for PrefixColor!");
			}
		}
		prefix = arrayToString(prefixl.toArray(new ChatColor[prefixl.size()]));
		String[] errors = WolfAPI.plugin.getConfig().getString("ErrorColor").split(",");
		List<ChatColor> errorl = new ArrayList<ChatColor>();
		for (String s : errors) {
			try {
				if (s.length() == 1) {
					if (ChatColor.getByChar(s.charAt(0)) == null) {
						Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for ErrorColor!");
					} else {
						errorl.add(ChatColor.getByChar(s.charAt(0)));
					}
				} else {
					errorl.add(Enum.valueOf(ChatColor.class, s.toUpperCase()));
				}
			} catch (Exception e) {
				Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for ErrorColor!");
			}
		}
		error = arrayToString(errorl.toArray(new ChatColor[errorl.size()]));
		String[] errorPrefixs = WolfAPI.plugin.getConfig().getString("ErrorPrefixColor").split(",");
		List<ChatColor> errorPrefixl = new ArrayList<ChatColor>();
		for (String s : errorPrefixs) {
			try {
				if (s.length() == 1) {
					if (ChatColor.getByChar(s.charAt(0)) == null) {
						Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for ErrorPrefixColor!");
					} else {
						errorPrefixl.add(ChatColor.getByChar(s.charAt(0)));
					}
				} else {
					errorPrefixl.add(Enum.valueOf(ChatColor.class, s.toUpperCase()));
				}
			} catch (Exception e) {
				Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for ErrorPrefixColor!");
			}
		}
		errorPrefix = arrayToString(errorPrefixl.toArray(new ChatColor[errorPrefixl.size()]));
		if (!(WolfAPI.plugin.getConfig().getString("ClickEvent").equalsIgnoreCase("run")) && !(WolfAPI.plugin.getConfig().getString("ClickEvent").equalsIgnoreCase("suggest"))) {
			clickEvent = ComponentType.RUN_COMMAND;
			Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for ClickEvent!");
		} else if (WolfAPI.plugin.getConfig().getString("ClickEvent").equalsIgnoreCase("run")) {
			clickEvent = ComponentType.RUN_COMMAND;
		} else if (WolfAPI.plugin.getConfig().getString("ClickEvent").equalsIgnoreCase("suggest")) {
			clickEvent = ComponentType.SUGGEST_COMMAND;
		}
		Data.loadBackends();
	}
	
	public static String arrayToString(ChatColor[] cc) {
		StringBuilder s = new StringBuilder();
		for (ChatColor c : cc) {
			s.append(c);
		}
		return s.toString();
	}

}

enum ConsoleFormat {
	BOTTOM,
	TOP;
}