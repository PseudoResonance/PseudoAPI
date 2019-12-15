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
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import io.github.pseudoresonance.pseudoapi.bukkit.PseudoAPI;
import io.github.pseudoresonance.pseudoapi.bukkit.PseudoPlugin;
import io.github.pseudoresonance.pseudoapi.bukkit.playerdata.PlayerDataController;
import io.github.pseudoresonance.pseudoapi.bukkit.Chat.Errors;

public class LanguageManager {
	
	private static final HashMap<String, Language> languages = new HashMap<String, Language>();
	
	private static String defaultLanguage;
	
	public static Language getLanguage() {
		return getLanguage(defaultLanguage);
	}
	
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
	
	public static Language getLanguage(String lang) {
		Language language = languages.get(lang.toLowerCase());
		if (language == null)
			language = languages.get(defaultLanguage);
		return language;
	}
	
	public static boolean registerLanguage(String lang, Language language) {
		if (!languages.containsKey(lang.toLowerCase())) {
			languages.put(lang.toLowerCase(), language);
			return true;
		}
		return false;
	}
	
	public static void setDefaultLanguage(String lang) {
		defaultLanguage = lang;
		if (getLanguage(lang) == null)
			registerLanguage(lang, new Language(lang));
	}
	
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
		for (File f : langDir.listFiles()) {
			String lang = f.getName().substring(0, f.getName().length() - 5);
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
					langMap.put(namespace + "." + key, cs.getString(key));
				}
			}
			language.addLanguageMap(langMap);
		}
	}

}
