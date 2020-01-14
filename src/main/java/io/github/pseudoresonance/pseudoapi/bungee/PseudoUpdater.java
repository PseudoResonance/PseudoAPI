package io.github.pseudoresonance.pseudoapi.bungee;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import io.github.pseudoresonance.pseudoapi.bukkit.Config;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;

public class PseudoUpdater {

	private static boolean shouldRestart = false;

	private static ArrayList<File> oldFiles = new ArrayList<File>();

	private static Download asyncUpdater = null;
	private static ScheduledTask updateTask = null;

	private static final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

	protected static void checkUpdates(CommandSender sender) {
		ProxyServer.getInstance().getScheduler().runAsync(PseudoAPI.plugin, new Update(sender));
	}

	protected static void checkUpdates(boolean startup) {
		ProxyServer.getInstance().getScheduler().runAsync(PseudoAPI.plugin, new Update(startup));
	}

	private static void downloadFiles(UpdateData file, CommandSender sender) {
		if (asyncUpdater == null) {
			asyncUpdater = new Download(file, sender);
			ProxyServer.getInstance().getScheduler().runAsync(PseudoAPI.plugin, asyncUpdater);
		}
	}

	private static void downloadFiles(UpdateData file) {
		if (asyncUpdater == null) {
			asyncUpdater = new Download(file);
			ProxyServer.getInstance().getScheduler().runAsync(PseudoAPI.plugin, asyncUpdater);
		}
	}

	protected static void restartCheck() {
		if (shouldRestart)
			if (!Config.restartEmpty || ProxyServer.getInstance().getOnlineCount() == 0)
				restart();
	}

	private static void restart() {
		if (ProxyServer.getInstance().getOnlineCount() != 0) {
			for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
				p.sendMessage(new ComponentBuilder(BungeeLanguageManager.getLanguage(p).getMessage("pseudoapi.server_restarting_for_updates_in", Config.restartWarning)).color(ChatColor.RED).create());
			}
			ProxyServer.getInstance().getScheduler().schedule(PseudoAPI.plugin, new Runnable() {
				public void run() {
					for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
						p.disconnect(new ComponentBuilder(BungeeLanguageManager.getLanguage(p).getMessage("pseudoapi.server_restarting_for_updates")).color(ChatColor.RED).create());
					}
					ProxyServer.getInstance().stop("Performing updates!");
				}
			}, Config.restartWarning, TimeUnit.SECONDS);
		} else {
			for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
				p.sendMessage(new ComponentBuilder(BungeeLanguageManager.getLanguage(p).getMessage("pseudoapi.server_restarting_for_updates_now")).color(ChatColor.RED).create());
			}
			ProxyServer.getInstance().stop("Performing updates!");
		}
	}

	private static boolean isNewer(String testVer, String currentVer) {
		if (currentVer == null)
			return true;
		if (testVer == null)
			return false;
		String[] test = testVer.split("\\.");
		String[] current = currentVer.split("\\.");
		int length = Math.max(test.length, current.length);
		for (int i = 0; i < length; i++) {
			String testStr = test[i].isEmpty() ? "0" : test[i];
			String currentStr = current[i].isEmpty() ? "0" : current[i];
			int testPart = i < test.length ? Integer.parseInt(testStr) : 0;
			int currentPart = i < current.length ? Integer.parseInt(currentStr) : 0;
			if (testPart < currentPart)
				return false;
			if (testPart > currentPart)
				return true;
		}
		return false;
	}

	protected static ArrayList<File> getOldFiles() {
		return oldFiles;
	}

	private static class Update implements Runnable {

		private final int type;
		private CommandSender sender = null;

		private Update(boolean startup) {
			if (startup)
				type = 0;
			else
				type = 1;
		}

		private Update(CommandSender sender) {
			type = 2;
			this.sender = sender;
		}

		@Override
		public void run() {
			switch (type) {
			case 0:
				checkUpdates(true);
				break;
			case 1:
				checkUpdates(false);
				break;
			case 2:
				checkUpdates(sender);
				break;
			default:
				break;
			}
		}

		protected static void checkUpdates(CommandSender sender) {
			UpdateData updateUrl = null;
			boolean update = false;
			sender.sendMessage(new ComponentBuilder(BungeeLanguageManager.getLanguage(sender).getMessage("pseudoapi.beginning_update_check")).color(ChatColor.GREEN).create());
			for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
				if (!p.getName().equals(sender.getName()) && p.hasPermission("pseudoapi.update.notify")) {
					p.sendMessage(new ComponentBuilder(BungeeLanguageManager.getLanguage(p).getMessage("pseudoapi.beginning_update_check")).color(ChatColor.GREEN).create());
				}
			}
			String versionURL = "https://nexus.otake.pw/repository/maven-public/io/github/pseudoresonance/PseudoAPI/maven-metadata.xml";
			try {
				DocumentBuilder db = dbf.newDocumentBuilder();
				URLConnection con = new URL(versionURL).openConnection();
				con.setRequestProperty("User-Agent", io.github.pseudoresonance.pseudoapi.bukkit.PseudoUpdater.userAgent);
				con.connect();
				try (InputStream is = con.getInputStream()) {
					Document doc = db.parse(is);
					doc.getDocumentElement().normalize();
					String version = doc.getElementsByTagName("latest").item(0).getTextContent();
					if (isNewer(version, PseudoAPI.plugin.getDescription().getVersion())) {
						update = true;
						sender.sendMessage(new ComponentBuilder(BungeeLanguageManager.getLanguage(sender).getMessage("pseudoapi.version_queue_update", PseudoAPI.plugin.getDescription().getName(), PseudoAPI.plugin.getDescription().getVersion(), version)).color(ChatColor.GREEN).create());
						for (ProxiedPlayer pl : ProxyServer.getInstance().getPlayers()) {
							if (!pl.getName().equals(sender.getName()) && pl.hasPermission("pseudoapi.update.notify")) {
								pl.sendMessage(new ComponentBuilder(BungeeLanguageManager.getLanguage(pl).getMessage("pseudoapi.version_queue_update", PseudoAPI.plugin.getDescription().getName(), PseudoAPI.plugin.getDescription().getVersion(), version)).color(ChatColor.GREEN).create());
							}
						}
						try {
							File file = PseudoAPI.plugin.getFile();
							if (file instanceof File) {
								PseudoAPI.plugin.getProxy().getPluginsFolder().mkdir();
								updateUrl = new UpdateData(new File(PseudoAPI.plugin.getProxy().getPluginsFolder(), ((File) file).getName()), "https://ci.otake.pw/job/PseudoAPI/lastSuccessfulBuild/artifact/artifacts/PseudoAPI.jar");
							}
						} catch (IllegalArgumentException e) {
							sender.sendMessage(new ComponentBuilder(BungeeLanguageManager.getLanguage(sender).getMessage("pseudoapi.could_not_get_plugin_jar")).color(ChatColor.RED).create());
							e.printStackTrace();
						}
					} else {
						sender.sendMessage(new ComponentBuilder(BungeeLanguageManager.getLanguage(sender).getMessage("pseudoapi.version_already_updated", PseudoAPI.plugin.getDescription().getName(), PseudoAPI.plugin.getDescription().getVersion(), version)).color(ChatColor.GREEN).create());
						for (ProxiedPlayer pl : ProxyServer.getInstance().getPlayers()) {
							if (!pl.getName().equals(sender.getName()) && pl.hasPermission("pseudoapi.update.notify")) {
								pl.sendMessage(new ComponentBuilder(BungeeLanguageManager.getLanguage(pl).getMessage("pseudoapi.version_already_updated", PseudoAPI.plugin.getDescription().getName(), PseudoAPI.plugin.getDescription().getVersion(), version)).color(ChatColor.GREEN).create());
							}
						}
					}
				}
			} catch (IOException | SAXException | ParserConfigurationException e1) {
				e1.printStackTrace();
			}
			if (update) {
				sender.sendMessage(new ComponentBuilder(BungeeLanguageManager.getLanguage(sender).getMessage("pseudoapi.completed_update_check", 1)).color(ChatColor.GREEN).create());
				for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
					if (!p.getName().equals(sender.getName()) && p.hasPermission("pseudoapi.update.notify")) {
						p.sendMessage(new ComponentBuilder(BungeeLanguageManager.getLanguage(p).getMessage("pseudoapi.completed_update_check", 1)).color(ChatColor.GREEN).create());
					}
				}
				if (updateUrl == null) {
					sender.sendMessage(new ComponentBuilder(BungeeLanguageManager.getLanguage(sender).getMessage("pseudoapi.plugins_could_not_be_updated", 1)).color(ChatColor.GREEN).create());
					for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
						if (!p.getName().equals(sender.getName()) && p.hasPermission("pseudoapi.update.notify")) {
							p.sendMessage(new ComponentBuilder(BungeeLanguageManager.getLanguage(p).getMessage("pseudoapi.plugins_could_not_be_updated", 1)).color(ChatColor.GREEN).create());
						}
					}
				}
				if (updateUrl != null && Config.downloadUpdates) {
					downloadFiles(updateUrl, sender);
				}
			} else {
				sender.sendMessage(new ComponentBuilder(BungeeLanguageManager.getLanguage(sender).getMessage("pseudoapi.completed_update_check", "No")).color(ChatColor.GREEN).create());
				for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
					if (!p.getName().equals(sender.getName()) && p.hasPermission("pseudoapi.update.notify")) {
						p.sendMessage(new ComponentBuilder(BungeeLanguageManager.getLanguage(p).getMessage("pseudoapi.completed_update_check", "No")).color(ChatColor.GREEN).create());
					}
				}
				if (updateTask != null) {
					updateTask.cancel();
					updateTask = null;
				}
				if (Config.updateFrequency > 0) {
					updateTask = ProxyServer.getInstance().getScheduler().schedule(PseudoAPI.plugin, new Runnable() {
						public void run() {
							updateTask = null;
							checkUpdates(false);
						}
					}, Config.updateFrequency, TimeUnit.MINUTES);
				}
			}
		}

		protected static void checkUpdates(boolean startup) {
			if (!(Config.startupUpdate || !startup)) {
				return;
			}
			boolean update = false;
			UpdateData updateUrl = null;
			ProxyServer.getInstance().getConsole().sendMessage(new ComponentBuilder(BungeeLanguageManager.getLanguage().getMessage("pseudoapi.beginning_update_check")).color(ChatColor.GREEN).create());
			for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
				if (p.hasPermission("pseudoapi.update.notify")) {
					p.sendMessage(new ComponentBuilder(BungeeLanguageManager.getLanguage(p).getMessage("pseudoapi.beginning_update_check")).color(ChatColor.GREEN).create());
				}
			}
			String versionURL = "https://nexus.otake.pw/repository/maven-public/io/github/pseudoresonance/PseudoAPI/maven-metadata.xml";
			try {
				DocumentBuilder db = dbf.newDocumentBuilder();
				URLConnection con = new URL(versionURL).openConnection();
				con.setRequestProperty("User-Agent", io.github.pseudoresonance.pseudoapi.bukkit.PseudoUpdater.userAgent);
				con.connect();
				try (InputStream is = con.getInputStream()) {
					Document doc = db.parse(is);
					doc.getDocumentElement().normalize();
					String version = doc.getElementsByTagName("latest").item(0).getTextContent();
					if (isNewer(version, PseudoAPI.plugin.getDescription().getVersion())) {
						update = true;
						ProxyServer.getInstance().getConsole().sendMessage(new ComponentBuilder(BungeeLanguageManager.getLanguage().getMessage("pseudoapi.version_queue_update", PseudoAPI.plugin.getDescription().getName(), PseudoAPI.plugin.getDescription().getVersion(), version)).color(ChatColor.GREEN).create());
						for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
							if (p.hasPermission("pseudoapi.update.notify")) {
								p.sendMessage(new ComponentBuilder(BungeeLanguageManager.getLanguage(p).getMessage("pseudoapi.version_queue_update", PseudoAPI.plugin.getDescription().getName(), PseudoAPI.plugin.getDescription().getVersion(), version)).color(ChatColor.GREEN).create());
							}
						}
						try {
							File file = PseudoAPI.plugin.getFile();
							if (file instanceof File) {
								PseudoAPI.plugin.getProxy().getPluginsFolder().mkdir();
								updateUrl = new UpdateData(new File(PseudoAPI.plugin.getProxy().getPluginsFolder(), ((File) file).getName()), "https://ci.otake.pw/job/PseudoAPI/lastSuccessfulBuild/artifact/artifacts/PseudoAPI.jar");
							}
						} catch (IllegalArgumentException e) {
							ProxyServer.getInstance().getConsole().sendMessage(new ComponentBuilder(BungeeLanguageManager.getLanguage().getMessage("pseudoapi.could_not_get_plugin_jar")).color(ChatColor.RED).create());
							e.printStackTrace();
						}
					} else {
						ProxyServer.getInstance().getConsole().sendMessage(new ComponentBuilder(BungeeLanguageManager.getLanguage().getMessage("pseudoapi.version_already_updated", PseudoAPI.plugin.getDescription().getName(), PseudoAPI.plugin.getDescription().getVersion(), version)).color(ChatColor.GREEN).create());
						for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
							if (p.hasPermission("pseudoapi.update.notify")) {
								p.sendMessage(new ComponentBuilder(BungeeLanguageManager.getLanguage(p).getMessage("pseudoapi.version_already_updated", PseudoAPI.plugin.getDescription().getName(), PseudoAPI.plugin.getDescription().getVersion(), version)).color(ChatColor.GREEN).create());
							}
						}
					}
				}
			} catch (IOException | SAXException | ParserConfigurationException e1) {
				e1.printStackTrace();
			}
			if (update) {
				ProxyServer.getInstance().getConsole().sendMessage(new ComponentBuilder(BungeeLanguageManager.getLanguage().getMessage("pseudoapi.completed_update_check", 1)).color(ChatColor.GREEN).create());
				for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
					if (p.hasPermission("pseudoapi.update.notify")) {
						p.sendMessage(new ComponentBuilder(BungeeLanguageManager.getLanguage(p).getMessage("pseudoapi.completed_update_check", 1)).color(ChatColor.GREEN).create());
					}
				}
				if (updateUrl == null) {
					ProxyServer.getInstance().getConsole().sendMessage(new ComponentBuilder(BungeeLanguageManager.getLanguage().getMessage("pseudoapi.plugins_could_not_be_updated", 1)).color(ChatColor.GREEN).create());
					for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
						if (p.hasPermission("pseudoapi.update.notify")) {
							p.sendMessage(new ComponentBuilder(BungeeLanguageManager.getLanguage(p).getMessage("pseudoapi.plugins_could_not_be_updated", 1)).color(ChatColor.GREEN).create());
						}
					}
				}
				if (updateUrl != null && Config.downloadUpdates) {
					downloadFiles(updateUrl);
				}
			} else {
				ProxyServer.getInstance().getConsole().sendMessage(new ComponentBuilder(BungeeLanguageManager.getLanguage().getMessage("pseudoapi.completed_update_check", "No")).color(ChatColor.GREEN).create());
				for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
					if (p.hasPermission("pseudoapi.update.notify")) {
						p.sendMessage(new ComponentBuilder(BungeeLanguageManager.getLanguage(p).getMessage("pseudoapi.completed_update_check", "No")).color(ChatColor.GREEN).create());
					}
				}
				if (updateTask != null) {
					updateTask.cancel();
					updateTask = null;
				}
				if (Config.updateFrequency > 0) {
					updateTask = ProxyServer.getInstance().getScheduler().schedule(PseudoAPI.plugin, new Runnable() {
						public void run() {
							updateTask = null;
							checkUpdates(false);
						}
					}, Config.updateFrequency, TimeUnit.MINUTES);
				}
			}
		}

	}

	private static class Download implements Runnable {

		private UpdateData file;
		private CommandSender sender = null;

		private Download(UpdateData file) {
			this.file = file;
		}

		private Download(UpdateData file, CommandSender sender) {
			this.file = file;
			this.sender = sender;
		}

		@Override
		public void run() {
			boolean successfulUpdate = false;
			try {
				Files.copy(new URL(file.getURL()).openStream(), file.getNewFile().toPath(), StandardCopyOption.REPLACE_EXISTING);
				successfulUpdate = true;
			} catch (Exception e) {
				ProxyServer.getInstance().getConsole().sendMessage(new ComponentBuilder(BungeeLanguageManager.getLanguage().getMessage("pseudoapi.could_not_download_update", file.getNewFile().getAbsolutePath())).color(ChatColor.RED).create());
				e.printStackTrace();
			}
			if (Config.updateRestart && successfulUpdate == true) {
				shouldRestart = true;
				restartCheck();
				if (!(!Config.restartEmpty || ProxyServer.getInstance().getOnlineCount() == 0)) {
					ProxyServer.getInstance().getConsole().sendMessage(new ComponentBuilder(BungeeLanguageManager.getLanguage().getMessage("pseudoapi.waiting_for_server_to_empty")).color(ChatColor.GREEN).create());
					if (sender != null) {
						for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
							if (!p.getName().equals(sender.getName()) && p.hasPermission("pseudoapi.update.notify")) {
								p.sendMessage(new ComponentBuilder(BungeeLanguageManager.getLanguage(p).getMessage("pseudoapi.waiting_for_server_to_empty")).color(ChatColor.GREEN).create());
							}
						}
						if (sender instanceof ProxiedPlayer) {
							sender.sendMessage(new ComponentBuilder(BungeeLanguageManager.getLanguage(sender).getMessage("pseudoapi.waiting_for_server_to_empty")).color(ChatColor.GREEN).create());
						}
					} else {
						for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
							if (p.hasPermission("pseudoapi.update.notify")) {
								p.sendMessage(new ComponentBuilder(BungeeLanguageManager.getLanguage(p).getMessage("pseudoapi.waiting_for_server_to_empty")).color(ChatColor.GREEN).create());
							}
						}
					}
				}
			}
		}

	}

	private static class UpdateData {

		private final File newFile;
		private final String url;

		private UpdateData(File newFile, String url) {
			this.newFile = newFile;
			this.url = url;
		}

		protected File getNewFile() {
			return this.newFile;
		}

		protected String getURL() {
			return this.url;
		}

	}

}