package io.github.pseudoresonance.pseudoapi.bukkit.language;

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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.lang3.LocaleUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import io.github.pseudoresonance.pseudoapi.bukkit.PseudoAPI;
import io.github.pseudoresonance.pseudoapi.bukkit.PseudoPlugin;
import io.github.pseudoresonance.pseudoapi.bukkit.playerdata.PlayerDataController;
import net.md_5.bungee.api.ChatColor;
import io.github.pseudoresonance.pseudoapi.bukkit.Chat.Errors;

public class LanguageManager {
	
	private static final ConcurrentHashMap<String, Language> languages = new ConcurrentHashMap<String, Language>();
	
	private static String defaultLanguage;
	
	/**
	 * Returns the default language
	 * 
	 * @return Default language
	 */
	public static Language getLanguage() {
		return getLanguage(defaultLanguage);
	}
	
	/**
	 * Returns a list of all supported languages
	 * 
	 * @return Set of supported languages
	 */
	public static Set<String> getLanguageList() {
		return languages.keySet();
	}
	
	/**
	 * Returns the set language of the {@link CommandSender}
	 * 
	 * @param sender Sender whose language to check
	 * @return Set language
	 */
	public static Language getLanguage(CommandSender sender) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			Object localeO = PlayerDataController.getPlayerSetting(p.getUniqueId().toString(), "locale");
			if (localeO instanceof String) {
				String locale = (String) localeO;
				return getLanguage(locale);
			}
		}
		return getLanguage();
	}
	
	/**
	 * Returns the given language
	 * 
	 * @param lang Name of language to get
	 * @return Language
	 */
	public static Language getLanguage(String lang) {
		Language language = languages.get(lang.toLowerCase());
		if (language == null)
			language = languages.get(defaultLanguage);
		return language;
	}
	
	/**
	 * Register language with given name
	 * 
	 * @param lang Language name
	 * @param language {@link Language}
	 * @return Whether or not registration was successful
	 */
	public static boolean registerLanguage(String lang, Language language) {
		if (!languages.containsKey(lang.toLowerCase())) {
			languages.put(lang.toLowerCase(), language);
			return true;
		}
		return false;
	}
	
	/**
	 * Sets default language
	 * 
	 * @param lang Default language to set
	 */
	public static void setDefaultLanguage(String lang) {
		defaultLanguage = lang;
		if (getLanguage(lang) == null)
			registerLanguage(lang, new Language(lang));
	}
	
	/**
	 * Copies default plugin language files to plugin folder
	 * 
	 * @param plugin Plugin whose language files to copy
	 * @param overwrite Whether or not to overwrite old language files
	 */
	public static void copyDefaultPluginLanguageFiles(PseudoPlugin plugin, boolean overwrite) {
		URL url = plugin.getClass().getProtectionDomain().getCodeSource().getLocation();
		File langDir = new File(plugin.getDataFolder(), "localization");
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
							plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "Error while fetching loacle file: " + entry.getName().substring(13));
							e.printStackTrace();
						}
					}
				}
			} catch (IOException | URISyntaxException e) {
				plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "Could not get plugin jar to fetch locale files!");
				e.printStackTrace();
			}
		}
		if (langDir.exists()) {
			for (File f : langDir.listFiles()) {
				if (f.getName().length() > 5) {
					String lang = f.getName().substring(0, f.getName().length() - 5);
					Locale locale = new Locale.Builder().setLanguageTag(lang).build();
					if (!LocaleUtils.isAvailableLocale(locale)) {
						plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "Invalid locale file: " + f.getName() + " Unknown locale!");
						continue;
					}
					lang = locale.toLanguageTag();
					YamlConfiguration yaml = YamlConfiguration.loadConfiguration(f);
					Language language = getLanguage(lang);
					if (language == null) {
						language = new Language(lang);
						registerLanguage(lang, language);
					}
					HashMap<String, String> langMap = new HashMap<String, String>();
					for (String namespace : yaml.getKeys(false)) {
						if (namespace.equals("date")) {
							if (!plugin.getName().equals(PseudoAPI.plugin.getName())) {
								continue;
							}
						}
						ConfigurationSection cs = yaml.getConfigurationSection(namespace);
						for (String key : cs.getKeys(false)) {
							langMap.put(namespace + "." + key, ChatColor.translateAlternateColorCodes('&', cs.getString(key)));
						}
					}
					language.addLanguageMap(langMap);
				} else
					plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "Invalid locale file: " + f.getName() + " Unknown locale!");
			}
		}
	}

}
