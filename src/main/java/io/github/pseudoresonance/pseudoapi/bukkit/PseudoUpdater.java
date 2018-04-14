package io.github.pseudoresonance.pseudoapi.bukkit;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.JSONArray;

import io.github.pseudoresonance.pseudoapi.bukkit.Message.Errors;

public class PseudoUpdater {

	private static boolean shouldRestart = false;

	private static ArrayList<File> oldFiles = new ArrayList<File>();

	private static ArrayList<PseudoPlugin> plugins = new ArrayList<PseudoPlugin>();
	private static ArrayList<PseudoPlugin> alreadyUpdated = new ArrayList<PseudoPlugin>();

	private static Download asyncUpdater = null;
	private static int updateTaskID = -1;

	public static void registerPlugin(PseudoPlugin plugin) {
		plugins.add(plugin);
	}

	public static void checkUpdates(CommandSender sender, String pluginName) {
		new Update(sender, pluginName).runTaskAsynchronously(PseudoAPI.plugin);
	}

	public static void checkUpdates(CommandSender sender) {
		new Update(sender).runTaskAsynchronously(PseudoAPI.plugin);
	}

	protected static void checkUpdates(boolean startup) {
		new Update(startup).runTaskAsynchronously(PseudoAPI.plugin);
	}

	private static void downloadFiles(ArrayList<UpdateData> files) {
		if (asyncUpdater == null) {
			asyncUpdater = new Download(files);
			asyncUpdater.runTaskAsynchronously(PseudoAPI.plugin);
		}
	}

	public static void restartCheck() {
		if (shouldRestart)
			if (!ConfigOptions.restartEmpty || Bukkit.getOnlinePlayers().size() == 0)
				restart();
	}

	private static void restart() {
		if (Bukkit.getOnlinePlayers().size() != 0) {
			PseudoAPI.message.broadcastPluginMessage("&cServer will restart in " + ConfigOptions.restartWarning + " seconds to perform updates!");
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(PseudoAPI.plugin, new Runnable() {
				public void run() {
					for (Player p : Bukkit.getOnlinePlayers()) {
						p.kickPlayer("§cServer is restarting for updates!\nPlease come back in a few minutes!");
					}
					if (Bukkit.getServer().getVersion().toLowerCase().contains("spigot")) {
						Bukkit.getServer().spigot().restart();
					} else {
						Bukkit.getServer().shutdown();
					}
				}
			}, ConfigOptions.restartWarning * 20);
		} else {
			PseudoAPI.message.broadcastPluginMessage("&cServer will restart now to perform updates!");
			if (Bukkit.getServer().getVersion().toLowerCase().contains("spigot")) {
				Bukkit.getServer().spigot().restart();
			} else {
				Bukkit.getServer().shutdown();
			}
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
			int testPart = i < test.length ? Integer.parseInt(test[i]) : 0;
			int currentPart = i < current.length ? Integer.parseInt(current[i]) : 0;
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

	private static class Update extends BukkitRunnable {
		
		private final int type;
		private CommandSender sender = null;
		private String pluginName = null;

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

		private Update(CommandSender sender, String pluginName) {
			type = 3;
			this.sender = sender;
			this.pluginName = pluginName;
		}

		@Override
		public void run() {
			switch(type) {
				case 0:
					checkUpdates(true);
					break;
				case 1:
					checkUpdates(false);
					break;
				case 2:
					checkUpdates(sender);
					break;
				case 3:
					checkUpdates(sender, pluginName);
					break;
				default:
					break;
			}
		}

		public static void checkUpdates(CommandSender sender, String pluginName) {
			ArrayList<UpdateData> updateUrls = new ArrayList<UpdateData>();
			Class<JavaPlugin> javaPluginC = JavaPlugin.class;
			Method getFileM = null;
			try {
				getFileM = javaPluginC.getDeclaredMethod("getFile");
				getFileM.setAccessible(true);
			} catch (NoSuchMethodException | SecurityException e1) {
				PseudoAPI.message.sendPluginError(sender, Errors.CUSTOM, "Could not get plugin jar file! Failed to update!");
				e1.printStackTrace();
			}
			boolean pluginFound = false;
			for (PseudoPlugin p : plugins) {
				if (p.getName().equalsIgnoreCase(pluginName)) {
					pluginFound = true;
					if (alreadyUpdated.contains(p)) {
						PseudoAPI.message.sendPluginMessage(sender, p.getName() + " is already waiting to be updated upon server restart!");
						return;
					}
					String urlPart = "https://circleci.com/api/v1.1/project/github/" + p.getAuthors().get(0) + "/" + p.getName();
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
								String test = aUrl.replaceFirst(".*\\/artifacts\\/" + p.getName() + "-", "");
								test = test.substring(0, test.length() - 13);
								version = test;
							}
						}
					}
					if (isNewer(version, p.getVersion())) {
						PseudoAPI.message.sendPluginMessage(sender, p.getName() + " is currently on version: " + p.getVersion() + " and latest update is: " + version + "! Queuing to update!");
						try {
							if (getFileM != null) {
								Object file = getFileM.invoke(p);
								if (file instanceof File) {
									Bukkit.getUpdateFolderFile().mkdir();
									updateUrls.add(new UpdateData((File) file, url, new File(Bukkit.getUpdateFolderFile(), p.getName() + ".jar")));
									if (updateUrls.size() > 0 && ConfigOptions.downloadUpdates) {
										alreadyUpdated.add(p);
										downloadFiles(updateUrls);
									}
								}
							}
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							PseudoAPI.message.sendPluginError(sender, Errors.CUSTOM, "Could not get plugin jar file! Failed to update!");
							e.printStackTrace();
							return;
						}
					} else
						PseudoAPI.message.sendPluginMessage(sender, p.getName() + " is currently on version: " + p.getVersion() + " and latest update is: " + version + "! Already up to date!");
				}
			}
			if (!pluginFound) {
				PseudoAPI.message.sendPluginError(sender, Errors.CUSTOM, "Invalid plugin name: " + pluginName);
			}
		}

		public static void checkUpdates(CommandSender sender) {
			ArrayList<UpdateData> updateUrls = new ArrayList<UpdateData>();
			int updates = 0;
			PseudoAPI.message.sendPluginMessage(sender, "Beginning update check!");
			Class<JavaPlugin> javaPluginC = JavaPlugin.class;
			Method getFileM = null;
			try {
				getFileM = javaPluginC.getDeclaredMethod("getFile");
				getFileM.setAccessible(true);
			} catch (NoSuchMethodException | SecurityException e1) {
				PseudoAPI.message.sendPluginError(sender, Errors.CUSTOM, "Could not get plugin jar file!");
				e1.printStackTrace();
			}
			for (PseudoPlugin p : plugins) {
				if (alreadyUpdated.contains(p)) {
					continue;
				}
				String urlPart = "https://circleci.com/api/v1.1/project/github/" + p.getAuthors().get(0) + "/" + p.getName();
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
							String test = aUrl.replaceFirst(".*\\/artifacts\\/" + p.getName() + "-", "");
							test = test.substring(0, test.length() - 13);
							version = test;
						}
					}
				}
				if (isNewer(version, p.getVersion())) {
					updates++;
					PseudoAPI.message.sendPluginMessage(sender, p.getName() + " is currently on version: " + p.getVersion() + " and latest update is: " + version + "! Queuing to update!");
					try {
						if (getFileM != null) {
							Object file = getFileM.invoke(p);
							if (file instanceof File) {
								Bukkit.getUpdateFolderFile().mkdir();
								updateUrls.add(new UpdateData((File) file, url, new File(Bukkit.getUpdateFolderFile(), p.getName() + ".jar")));
								alreadyUpdated.add(p);
							}
						}
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						PseudoAPI.message.sendPluginError(sender, Errors.CUSTOM, "Could not get plugin jar file! Failed to update!");
						e.printStackTrace();
					}
				}
			}
			if (updates > 0) {
				PseudoAPI.message.sendPluginMessage(sender, "Completed update check! " + updates + " updates found!");
				if (updates > updateUrls.size()) {
					int failedUpdates = updates - updateUrls.size();
					PseudoAPI.message.sendPluginMessage(sender, failedUpdates + " plugins could not be updated due to errors! Please check the console!");
				}
				if (updateUrls.size() > 0 && ConfigOptions.downloadUpdates) {
					downloadFiles(updateUrls);
				}
			} else {
				PseudoAPI.message.sendPluginMessage(sender, "Completed update check! No updates found!");
				if (updateTaskID != -1) {
					Bukkit.getServer().getScheduler().cancelTask(updateTaskID);
				}
				updateTaskID = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(PseudoAPI.plugin, new Runnable() {
					public void run() {
						checkUpdates(false);
					}
				}, ConfigOptions.updateFrequency * 60 * 20);
			}
		}

		protected static void checkUpdates(boolean startup) {
			if (!(ConfigOptions.startupUpdate || !startup)) {
				return;
			}
			ArrayList<UpdateData> updateUrls = new ArrayList<UpdateData>();
			int updates = 0;
			PseudoAPI.message.sendPluginMessage(Bukkit.getConsoleSender(), "Beginning update check!");
			Class<JavaPlugin> javaPluginC = JavaPlugin.class;
			Method getFileM = null;
			try {
				getFileM = javaPluginC.getDeclaredMethod("getFile");
				getFileM.setAccessible(true);
			} catch (NoSuchMethodException | SecurityException e1) {
				PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "Could not get plugin jar file!");
				e1.printStackTrace();
			}
			for (PseudoPlugin p : plugins) {
				if (alreadyUpdated.contains(p)) {
					continue;
				}
				String urlPart = "https://circleci.com/api/v1.1/project/github/" + p.getAuthors().get(0) + "/" + p.getName();
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
							String test = aUrl.replaceFirst(".*\\/artifacts\\/" + p.getName() + "-", "");
							test = test.substring(0, test.length() - 13);
							version = test;
						}
					}
				}
				if (isNewer(version, p.getVersion())) {
					updates++;
					PseudoAPI.message.sendPluginMessage(Bukkit.getConsoleSender(), p.getName() + " is currently on version: " + p.getVersion() + " and latest update is: " + version + "! Queuing to update!");
					try {
						if (getFileM != null) {
							Object file = getFileM.invoke(p);
							if (file instanceof File) {
								Bukkit.getUpdateFolderFile().mkdir();
								updateUrls.add(new UpdateData((File) file, url, new File(Bukkit.getUpdateFolderFile(), p.getName() + ".jar")));
								alreadyUpdated.add(p);
							}
						}
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "Could not get plugin jar file!");
						e.printStackTrace();
					}
				} else
					PseudoAPI.message.sendPluginMessage(Bukkit.getConsoleSender(), p.getName() + " is currently on version: " + p.getVersion() + " and latest update is: " + version + "! Already up to date!");
			}
			if (updates > 0) {
				PseudoAPI.message.sendPluginMessage(Bukkit.getConsoleSender(), "Completed update check! " + updates + " updates found!");
				if (updates > updateUrls.size()) {
					int failedUpdates = updates - updateUrls.size();
					PseudoAPI.message.sendPluginMessage(Bukkit.getConsoleSender(), failedUpdates + " plugins could not be updated due to errors! Please check the console!");
				}
				if (updateUrls.size() > 0 && ConfigOptions.downloadUpdates) {
					downloadFiles(updateUrls);
				}
			} else {
				PseudoAPI.message.sendPluginMessage(Bukkit.getConsoleSender(), "Completed update check! No updates found!");
				if (updateTaskID != -1) {
					Bukkit.getServer().getScheduler().cancelTask(updateTaskID);
				}
				updateTaskID = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(PseudoAPI.plugin, new Runnable() {
					public void run() {
						checkUpdates(false);
					}
				}, ConfigOptions.updateFrequency * 60 * 20);
			}
		}

	}

	private static class Download extends BukkitRunnable {

		private ArrayList<UpdateData> files;

		private Download(ArrayList<UpdateData> files) {
			this.files = files;
		}

		@Override
		public void run() {
			int successfulUpdates = 0;
			for (UpdateData d : files) {
				try {
					Files.copy(new URL(d.getURL()).openStream(), d.getNewFile().toPath(), StandardCopyOption.REPLACE_EXISTING);
					if (!d.getOldFile().getName().equals(d.getNewFile().getName())) {
						oldFiles.add(d.getOldFile());
						PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "Please delete " + d.getOldFile().getName() + " before starting the server again to prevent duplicate plugins!");
					}
					successfulUpdates++;
				} catch (IOException e) {
					PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "Could not download update to: " + d.getNewFile().getAbsolutePath());
					e.printStackTrace();
				} catch (Exception e) {
					PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "Could not download update to: " + d.getNewFile().getAbsolutePath());
					e.printStackTrace();
				}
			}
			if (ConfigOptions.updateRestart && successfulUpdates > 0) {
				shouldRestart = true;
				restartCheck();
				if (!(!ConfigOptions.restartEmpty || Bukkit.getOnlinePlayers().size() == 0))
					PseudoAPI.message.sendPluginMessage(Bukkit.getConsoleSender(), "Waiting for server to empty before restarting!");
			}
		}

	}

	private static class UpdateData {

		private final File oldFile;
		private final String url;
		private final File newFile;

		private UpdateData(File oldFile, String url, File newFile) {
			this.oldFile = oldFile;
			this.url = url;
			this.newFile = newFile;
		}

		public File getOldFile() {
			return this.oldFile;
		}

		public String getURL() {
			return this.url;
		}

		public File getNewFile() {
			return this.newFile;
		}

	}

}