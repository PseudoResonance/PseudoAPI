package io.github.pseudoresonance.pseudoapi.bukkit;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;

import io.github.pseudoresonance.pseudoapi.bukkit.data.Data;
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
	public static boolean allowPseudoAPI = true;
	
	public static boolean bungeeEnabled = false;

	public static boolean startupUpdate = true;
	public static int startupDelay = 60;
	public static int updateFrequency = 360;
	public static boolean downloadUpdates = true;
	public static boolean updateRestart = true;
	public static boolean restartEmpty = true;
	public static int restartWarning = 60;
	
	public static boolean updateConfig() {
		boolean error = false;
		InputStream configin = PseudoAPI.plugin.getClass().getResourceAsStream("/config.yml"); 
		BufferedReader configreader = new BufferedReader(new InputStreamReader(configin));
		YamlConfiguration configc = YamlConfiguration.loadConfiguration(configreader);
		int configcj = configc.getInt("Version");
		try {
			configreader.close();
			configin.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if (PseudoAPI.plugin.getConfig().getInt("Version") != configcj) {
			try {
				String oldFile = "";
				File conf = new File(PseudoAPI.plugin.getDataFolder(), "config.yml");
				if (new File(PseudoAPI.plugin.getDataFolder(), "config.yml.old").exists()) {
					for (int i = 1; i > 0; i++) {
						if (!(new File(PseudoAPI.plugin.getDataFolder(), "config.yml.old" + i).exists())) {
							conf.renameTo(new File(PseudoAPI.plugin.getDataFolder(), "config.yml.old" + i));
							oldFile = "config.yml.old" + i;
							break;
						}
					}
				} else {
					conf.renameTo(new File(PseudoAPI.plugin.getDataFolder(), "config.yml.old"));
					oldFile = "config.yml.old";
				}
				PseudoAPI.plugin.saveDefaultConfig();
				PseudoAPI.plugin.reloadConfig();
				PseudoAPI.getConfigOptions().reloadConfig();
				Message.sendConsoleMessage(ChatColor.GREEN + "Config is up to date! Old config file renamed to " + oldFile + ".");
			} catch (Exception e) {
				Message.sendConsoleMessage(ChatColor.RED + "Error while updating config!");
				error = true;
			}
		}
		if (!error) {
			Message.sendConsoleMessage(ChatColor.GREEN + "Config is up to date!");
		} else {
			return false;
		}
		return true;
	}
	
	public void reloadConfig() {
		try {
			String s = PseudoAPI.plugin.getConfig().getString("BungeeEnabled");
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
		String mf = PseudoAPI.plugin.getConfig().getString("MessageFormat");
		if (mf == null) {
			Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for MessageFormat!");
			messageFormat = "{nickname}> {message}";
		} else {
			messageFormat = mf;
		}
		if (PseudoAPI.plugin.getConfig().getString("ConsoleFormat").equalsIgnoreCase("top")) {
			consoleFormat = ConsoleFormat.TOP;
		} else if (PseudoAPI.plugin.getConfig().getString("ConsoleFormat").equalsIgnoreCase("bottom")) {
			consoleFormat = ConsoleFormat.BOTTOM;
		} else {
			consoleFormat = ConsoleFormat.BOTTOM;
			Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for ConsoleFormat!");
		}
		try {
			String s = PseudoAPI.plugin.getConfig().getString("HidePlugins");
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
			String s = PseudoAPI.plugin.getConfig().getString("AllowPseudoAPI");
			if (s.equalsIgnoreCase("true")) {
				allowPseudoAPI = true;
			} else if (s.equalsIgnoreCase("false")) {
				allowPseudoAPI = false;
			} else {
				allowPseudoAPI = false;
				Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for AllowPseudoAPI!");
			}
		} catch (Exception e) {
			Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for AllowPseudoAPI!");
		}
		String[] borders = PseudoAPI.plugin.getConfig().getString("BorderColor").split(",");
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
		String[] titles = PseudoAPI.plugin.getConfig().getString("TitleColor").split(",");
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
		String[] commands = PseudoAPI.plugin.getConfig().getString("CommandColor").split(",");
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
		String[] descriptions = PseudoAPI.plugin.getConfig().getString("DescriptionColor").split(",");
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
		String[] texts = PseudoAPI.plugin.getConfig().getString("TextColor").split(",");
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
		String[] prefixs = PseudoAPI.plugin.getConfig().getString("PrefixColor").split(",");
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
		String[] errors = PseudoAPI.plugin.getConfig().getString("ErrorColor").split(",");
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
		String[] errorPrefixs = PseudoAPI.plugin.getConfig().getString("ErrorPrefixColor").split(",");
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
		if (!(PseudoAPI.plugin.getConfig().getString("ClickEvent").equalsIgnoreCase("run")) && !(PseudoAPI.plugin.getConfig().getString("ClickEvent").equalsIgnoreCase("suggest"))) {
			clickEvent = ComponentType.RUN_COMMAND;
			Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for ClickEvent!");
		} else if (PseudoAPI.plugin.getConfig().getString("ClickEvent").equalsIgnoreCase("run")) {
			clickEvent = ComponentType.RUN_COMMAND;
		} else if (PseudoAPI.plugin.getConfig().getString("ClickEvent").equalsIgnoreCase("suggest")) {
			clickEvent = ComponentType.SUGGEST_COMMAND;
		}
		try {
			String s = PseudoAPI.plugin.getConfig().getString("StartupUpdate");
			if (s.equalsIgnoreCase("true")) {
				startupUpdate = true;
			} else if (s.equalsIgnoreCase("false")) {
				startupUpdate = false;
			} else {
				startupUpdate = false;
				Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for StartupUpdate!");
			}
		} catch (Exception e) {
			Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for StartupUpdate!");
		}
		String startupD = PseudoAPI.plugin.getConfig().getString("StartupDelay");
		if (isInteger(startupD)) {
			startupDelay = Integer.valueOf(startupD);
		} else {
			startupDelay = 60;
			Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for StartupDelay!");
		}
		String updateF = PseudoAPI.plugin.getConfig().getString("UpdateFrequency");
		if (isInteger(updateF)) {
			updateFrequency = Integer.valueOf(updateF);
		} else {
			updateFrequency = 360;
			Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for UpdateFrequency!");
		}
		try {
			String s = PseudoAPI.plugin.getConfig().getString("DownloadUpdates");
			if (s.equalsIgnoreCase("true")) {
				downloadUpdates = true;
			} else if (s.equalsIgnoreCase("false")) {
				downloadUpdates = false;
			} else {
				downloadUpdates = false;
				Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for DownloadUpdates!");
			}
		} catch (Exception e) {
			Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for DownloadUpdates!");
		}
		try {
			String s = PseudoAPI.plugin.getConfig().getString("UpdateRestart");
			if (s.equalsIgnoreCase("true")) {
				updateRestart = true;
			} else if (s.equalsIgnoreCase("false")) {
				updateRestart = false;
			} else {
				updateRestart = false;
				Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for UpdateRestart!");
			}
		} catch (Exception e) {
			Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for UpdateRestart!");
		}
		try {
			String s = PseudoAPI.plugin.getConfig().getString("RestartEmpty");
			if (s.equalsIgnoreCase("true")) {
				restartEmpty = true;
			} else if (s.equalsIgnoreCase("false")) {
				restartEmpty = false;
			} else {
				restartEmpty = false;
				Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for RestartEmpty!");
			}
		} catch (Exception e) {
			Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for RestartEmpty!");
		}
		String restartWarn = PseudoAPI.plugin.getConfig().getString("RestartWarning");
		if (isInteger(restartWarn)) {
			restartWarning = Integer.valueOf(restartWarn);
		} else {
			restartWarning = 60;
			Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for RestartWarning!");
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
	
	private static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    } catch(NullPointerException e) {
	        return false;
	    }
	    return true;
	}

}

enum ConsoleFormat {
	BOTTOM,
	TOP;
}