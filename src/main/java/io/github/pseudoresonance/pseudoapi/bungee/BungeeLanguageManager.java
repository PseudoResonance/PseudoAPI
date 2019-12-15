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
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import io.github.pseudoresonance.pseudoapi.bukkit.language.Language;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.YamlConfiguration;

public class BungeeLanguageManager {
	
	private static final HashMap<String, Language> languages = new HashMap<String, Language>();
	
	private static String defaultLanguage;
	
	public static Language getLanguage() {
		return getLanguage(defaultLanguage);
	}
	
	public static Language getLanguage(CommandSender sender) {
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
			String lang = f.getName().substring(0, f.getName().length() - 5);
			try {
				Configuration yaml = YamlConfiguration.getProvider(YamlConfiguration.class).load(f);
				Language language = getLanguage(lang);
				if (language == null) {
					language = new Language(lang);
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
		}
	}

}
