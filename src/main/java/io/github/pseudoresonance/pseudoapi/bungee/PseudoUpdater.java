package io.github.pseudoresonance.pseudoapi.bungee;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;

import io.github.pseudoresonance.pseudoapi.bukkit.utils.JsonReader;
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
				p.sendMessage(new ComponentBuilder("Server will restart in " + Config.restartWarning + " seconds to perform updates!").color(ChatColor.RED).create());
			}
			ProxyServer.getInstance().getScheduler().schedule(PseudoAPI.plugin, new Runnable() {
				public void run() {
					for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
						p.disconnect(new ComponentBuilder("Server is restarting for updates!\nPlease come back in a few minutes!").color(ChatColor.RED).create());
					}
					ProxyServer.getInstance().stop("Performing updates!");
				}
			}, Config.restartWarning, TimeUnit.SECONDS);
		} else {
			for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
				p.sendMessage(new ComponentBuilder("Server will restart now to perform updates!").color(ChatColor.RED).create());
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
			sender.sendMessage(new ComponentBuilder("Beginning update check!").color(ChatColor.GREEN).create());
			for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
				if (!p.getName().equals(sender.getName()) && p.hasPermission("pseudoapi.update.notify")) {
					p.sendMessage(new ComponentBuilder("Beginning update check!").color(ChatColor.GREEN).create());
				}
			}
			String urlPart = "https://circleci.com/api/v1.1/project/github/" + PseudoAPI.plugin.getDescription().getAuthor() + "/" + PseudoAPI.plugin.getDescription().getName();
			String buildCheck = urlPart + "?limit=1&filter=completed";
			JSONArray buildJson = JsonReader.readJsonFromUrl(buildCheck);
			int build = buildJson.getJSONObject(0).getInt("build_num");
			String artifactCheck = urlPart + "/" + build + "/artifacts";
			JSONArray artifactJson = JsonReader.readJsonFromUrl(artifactCheck);
			String url = "";
			String version = "";
			for (int i = 0; i < artifactJson.length(); i++) {
				String aUrl = artifactJson.getJSONObject(i).getString("url");
				if (aUrl.endsWith(".jar")) {
					if (!aUrl.endsWith("-SNAPSHOT.jar")) {
						url = aUrl;
					} else {
						String test = aUrl.replaceFirst(".*\\/artifacts\\/" + PseudoAPI.plugin.getDescription().getName() + "-", "");
						test = test.substring(0, test.length() - 13);
						version = test;
					}
				}
			}
			if (isNewer(version, PseudoAPI.plugin.getDescription().getVersion())) {
				update = true;
				sender.sendMessage(new ComponentBuilder(PseudoAPI.plugin.getDescription().getName() + " is currently on version: " + PseudoAPI.plugin.getDescription().getVersion() + " and latest update is: " + version + "! Queuing to update!").color(ChatColor.GREEN).create());
				for (ProxiedPlayer pl : ProxyServer.getInstance().getPlayers()) {
					if (!pl.getName().equals(sender.getName()) && pl.hasPermission("pseudoapi.update.notify")) {
						pl.sendMessage(new ComponentBuilder(PseudoAPI.plugin.getDescription().getName() + " is currently on version: " + PseudoAPI.plugin.getDescription().getVersion() + " and latest update is: " + version + "! Queuing to update!").color(ChatColor.GREEN).create());
					}
				}
				try {
					File file = PseudoAPI.plugin.getFile();
					if (file instanceof File) {
						PseudoAPI.plugin.getProxy().getPluginsFolder().mkdir();
						updateUrl = new UpdateData(new File(PseudoAPI.plugin.getProxy().getPluginsFolder(), ((File) file).getName()), url);
					}
				} catch (IllegalArgumentException e) {
					sender.sendMessage(new ComponentBuilder("Could not get plugin jar file! Failed to update!").color(ChatColor.RED).create());
					e.printStackTrace();
				}
			} else {
				sender.sendMessage(new ComponentBuilder(PseudoAPI.plugin.getDescription().getName() + " is currently on version: " + PseudoAPI.plugin.getDescription().getVersion() + " and latest update is: " + version + "! Already up to date!").color(ChatColor.GREEN).create());
				for (ProxiedPlayer pl : ProxyServer.getInstance().getPlayers()) {
					if (!pl.getName().equals(sender.getName()) && pl.hasPermission("pseudoapi.update.notify")) {
						pl.sendMessage(new ComponentBuilder(PseudoAPI.plugin.getDescription().getName() + " is currently on version: " + PseudoAPI.plugin.getDescription().getVersion() + " and latest update is: " + version + "! Already up to date!").color(ChatColor.GREEN).create());
					}
				}
			}
			if (update) {
				sender.sendMessage(new ComponentBuilder("Completed update check!").color(ChatColor.GREEN).create());
				for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
					if (!p.getName().equals(sender.getName()) && p.hasPermission("pseudoapi.update.notify")) {
						p.sendMessage(new ComponentBuilder("Completed update check!").color(ChatColor.GREEN).create());
					}
				}
				if (updateUrl == null) {
					sender.sendMessage(new ComponentBuilder("Plugin could not be updated due to errors! Please check the console!").color(ChatColor.GREEN).create());
					for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
						if (!p.getName().equals(sender.getName()) && p.hasPermission("pseudoapi.update.notify")) {
							p.sendMessage(new ComponentBuilder("Plugin could not be updated due to errors! Please check the console!").color(ChatColor.GREEN).create());
						}
					}
				}
				if (updateUrl != null && Config.downloadUpdates) {
					downloadFiles(updateUrl, sender);
				}
			} else {
				sender.sendMessage(new ComponentBuilder("Completed update check! No update found!").color(ChatColor.GREEN).create());
				for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
					if (!p.getName().equals(sender.getName()) && p.hasPermission("pseudoapi.update.notify")) {
						p.sendMessage(new ComponentBuilder("Completed update check! No update found!").color(ChatColor.GREEN).create());
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
			ProxyServer.getInstance().getConsole().sendMessage(new ComponentBuilder("Beginning update check!").color(ChatColor.GREEN).create());
			for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
				if (p.hasPermission("pseudoapi.update.notify")) {
					p.sendMessage(new ComponentBuilder("Beginning update check!").color(ChatColor.GREEN).create());
				}
			}
			String urlPart = "https://circleci.com/api/v1.1/project/github/" + PseudoAPI.plugin.getDescription().getAuthor() + "/" + PseudoAPI.plugin.getDescription().getName();
			String buildCheck = urlPart + "?limit=1&filter=completed";
			JSONArray buildJson = JsonReader.readJsonFromUrl(buildCheck);
			int build = buildJson.getJSONObject(0).getInt("build_num");
			String artifactCheck = urlPart + "/" + build + "/artifacts";
			JSONArray artifactJson = JsonReader.readJsonFromUrl(artifactCheck);
			String url = "";
			String version = "";
			for (int i = 0; i < artifactJson.length(); i++) {
				String aUrl = artifactJson.getJSONObject(i).getString("url");
				if (aUrl.endsWith(".jar")) {
					if (!aUrl.endsWith("-SNAPSHOT.jar")) {
						url = aUrl;
					} else {
						String test = aUrl.replaceFirst(".*\\/artifacts\\/" + PseudoAPI.plugin.getDescription().getName() + "-", "");
						test = test.substring(0, test.length() - 13);
						version = test;
					}
				}
			}
			if (isNewer(version, PseudoAPI.plugin.getDescription().getVersion())) {
				update = true;
				ProxyServer.getInstance().getConsole().sendMessage(new ComponentBuilder(PseudoAPI.plugin.getDescription().getName() + " is currently on version: " + PseudoAPI.plugin.getDescription().getVersion() + " and latest update is: " + version + "! Queuing to update!").color(ChatColor.GREEN).create());
				for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
					if (p.hasPermission("pseudoapi.update.notify")) {
						p.sendMessage(new ComponentBuilder(PseudoAPI.plugin.getDescription().getName() + " is currently on version: " + PseudoAPI.plugin.getDescription().getVersion() + " and latest update is: " + version + "! Queuing to update!").color(ChatColor.GREEN).create());
					}
				}
				try {
					File file = PseudoAPI.plugin.getFile();
					if (file instanceof File) {
						PseudoAPI.plugin.getProxy().getPluginsFolder().mkdir();
						updateUrl = new UpdateData(new File(PseudoAPI.plugin.getProxy().getPluginsFolder(), ((File) file).getName()), url);
					}
				} catch (IllegalArgumentException e) {
					ProxyServer.getInstance().getConsole().sendMessage(new ComponentBuilder("Could not get plugin jar file!").color(ChatColor.RED).create());
					e.printStackTrace();
				}
			} else {
				ProxyServer.getInstance().getConsole().sendMessage(new ComponentBuilder(PseudoAPI.plugin.getDescription().getName() + " is currently on version: " + PseudoAPI.plugin.getDescription().getVersion() + " and latest update is: " + version + "! Already up to date!").color(ChatColor.GREEN).create());
				for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
					if (p.hasPermission("pseudoapi.update.notify")) {
						p.sendMessage(new ComponentBuilder(PseudoAPI.plugin.getDescription().getName() + " is currently on version: " + PseudoAPI.plugin.getDescription().getVersion() + " and latest update is: " + version + "! Already up to date!").color(ChatColor.GREEN).create());
					}
				}
			}
			if (update) {
				ProxyServer.getInstance().getConsole().sendMessage(new ComponentBuilder("Completed update check!").color(ChatColor.GREEN).create());
				for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
					if (p.hasPermission("pseudoapi.update.notify")) {
						p.sendMessage(new ComponentBuilder("Completed update check!").color(ChatColor.GREEN).create());
					}
				}
				if (updateUrl == null) {
					ProxyServer.getInstance().getConsole().sendMessage(new ComponentBuilder("Plugin could not be updated due to errors! Please check the console!").color(ChatColor.GREEN).create());
					for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
						if (p.hasPermission("pseudoapi.update.notify")) {
							p.sendMessage(new ComponentBuilder("Plugin could not be updated due to errors! Please check the console!").color(ChatColor.GREEN).create());
						}
					}
				}
				if (updateUrl != null && Config.downloadUpdates) {
					downloadFiles(updateUrl);
				}
			} else {
				ProxyServer.getInstance().getConsole().sendMessage(new ComponentBuilder("Completed update check! No update found!").color(ChatColor.GREEN).create());
				for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
					if (p.hasPermission("pseudoapi.update.notify")) {
						p.sendMessage(new ComponentBuilder("Completed update check! No update found!").color(ChatColor.GREEN).create());
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
				ProxyServer.getInstance().getConsole().sendMessage(new ComponentBuilder("Could not download update to: " + file.getNewFile().getAbsolutePath()).color(ChatColor.RED).create());
				e.printStackTrace();
			}
			if (Config.updateRestart && successfulUpdate == true) {
				shouldRestart = true;
				restartCheck();
				if (!(!Config.restartEmpty || ProxyServer.getInstance().getOnlineCount() == 0)) {
					ProxyServer.getInstance().getConsole().sendMessage(new ComponentBuilder("Waiting for server to empty before restarting!").color(ChatColor.GREEN).create());
					if (sender != null) {
						for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
							if (!p.getName().equals(sender.getName()) && p.hasPermission("pseudoapi.update.notify")) {
								p.sendMessage(new ComponentBuilder("Waiting for server to empty before restarting!").color(ChatColor.GREEN).create());
							}
						}
						if (sender instanceof ProxiedPlayer) {
							sender.sendMessage(new ComponentBuilder("Waiting for server to empty before restarting!").color(ChatColor.GREEN).create());
						}
					} else {
						for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
							if (p.hasPermission("pseudoapi.update.notify")) {
								p.sendMessage(new ComponentBuilder("Waiting for server to empty before restarting!").color(ChatColor.GREEN).create());
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