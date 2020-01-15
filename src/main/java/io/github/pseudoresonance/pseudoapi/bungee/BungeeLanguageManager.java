package io.github.pseudoresonance.pseudoapi.bungee;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.lang3.LocaleUtils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.YamlConfiguration;

public class BungeeLanguageManager {
	
	private static final HashMap<String, BungeeLanguage> languages = new HashMap<String, BungeeLanguage>();
	
	private static String defaultLanguage;
	
	public static BungeeLanguage getLanguage() {
		return getLanguage(defaultLanguage);
	}
	
	public static BungeeLanguage getLanguage(CommandSender sender) {
		if (sender instanceof ProxiedPlayer) {
			ProxiedPlayer p = (ProxiedPlayer) sender;
			Object localeO = PlayerDataController.getPlayerSetting(p.getUniqueId().toString(), "locale");
			if (localeO instanceof String) {
				String locale = (String) localeO;
				return getLanguage(locale);
			}
		}
		return getLanguage();
	}
	
	public static BungeeLanguage getLanguage(String lang) {
		BungeeLanguage language = languages.get(lang.toLowerCase());
		if (language == null)
			language = languages.get(defaultLanguage);
		return language;
	}
	
	public static boolean registerLanguage(String lang, BungeeLanguage language) {
		if (!languages.containsKey(lang.toLowerCase())) {
			languages.put(lang.toLowerCase(), language);
			return true;
		}
		return false;
	}
	
	public static void setDefaultLanguage(String lang) {
		defaultLanguage = lang;
		if (getLanguage(lang) == null)
			registerLanguage(lang, new BungeeLanguage(lang));
	}
	
	public static void copyDefaultPluginLanguageFiles(boolean overwrite) {
		URL url = PseudoAPI.class.getProtectionDomain().getCodeSource().getLocation();
		File langDir = new File(PseudoAPI.plugin.getDataFolder(), "localization");
		if (url != null) {
			try (ZipFile jar = new ZipFile(new File(url.toURI()))) {
				Enumeration<? extends ZipEntry> entries = jar.entries();
				while (entries.hasMoreElements()) {
					ZipEntry entry = entries.nextElement();
					if (entry.getName().startsWith("localization/") && entry.getName().endsWith(".lang")) {
						try (InputStream is = jar.getInputStream(entry)) {
							if (is != null) {
								langDir.mkdirs();
								String name = entry.getName().substring(13);
								File dest = new File(langDir, name);
								if (!dest.exists() || overwrite) {
									Files.copy(is, dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
								}
							}
						} catch (IOException e) {
							ProxyServer.getInstance().getConsole().sendMessage(new ComponentBuilder("Error while fetching loacle file: " + entry.getName().substring(13)).color(ChatColor.RED).create());
							e.printStackTrace();
						}
					}
				}
			} catch (IOException | URISyntaxException e) {
				ProxyServer.getInstance().getConsole().sendMessage(new ComponentBuilder("Could not get plugin jar to fetch locale files!").color(ChatColor.RED).create());
				e.printStackTrace();
			}
		}
		for (File f : langDir.listFiles()) {
			if (f.getName().length() > 5) {
				String lang = f.getName().substring(0, f.getName().length() - 5);
				Locale locale = new Locale.Builder().setLanguageTag(lang).build();
				if (!LocaleUtils.isAvailableLocale(locale)) {
					ProxyServer.getInstance().getConsole().sendMessage(new ComponentBuilder("Invalid locale file: " + f.getName() + " Unknown locale!").color(ChatColor.RED).create());
					continue;
				}
				lang = locale.toLanguageTag();
				try {
					Configuration yaml = YamlConfiguration.getProvider(YamlConfiguration.class).load(f);
					BungeeLanguage language = getLanguage(lang);
					if (language == null) {
						language = new BungeeLanguage(lang);
						registerLanguage(lang, language);
					}
					HashMap<String, String> langMap = new HashMap<String, String>();
					for (String namespace : yaml.getKeys()) {
						Configuration cs = yaml.getSection(namespace);
						for (String key : cs.getKeys()) {
							langMap.put(namespace + "." + key, cs.getString(key));
						}
					}
					language.addLanguageMap(langMap);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else
				ProxyServer.getInstance().getConsole().sendMessage(new ComponentBuilder("Invalid locale file: " + f.getName() + " Unknown locale!").color(ChatColor.RED).create());
		}
	}

}
