package io.github.pseudoresonance.pseudoapi.bukkit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import io.github.pseudoresonance.pseudoapi.bukkit.Chat.Errors;
import io.github.pseudoresonance.pseudoapi.bukkit.language.LanguageManager;

public class PseudoUpdater {

	private static boolean shouldRestart = false;

	private static ArrayList<File> oldFiles = new ArrayList<File>();

	private static ArrayList<PseudoPlugin> plugins = new ArrayList<PseudoPlugin>();
	private static ArrayList<PseudoPlugin> alreadyUpdated = new ArrayList<PseudoPlugin>();

	private static Download asyncUpdater = null;
	private static int updateTaskID = -1;

	private static final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	private static final String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:73.0) Gecko/20100101 Firefox/73.0";

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

	private static void downloadFiles(ArrayList<UpdateData> files, CommandSender sender) {
		if (asyncUpdater == null) {
			asyncUpdater = new Download(files, sender);
			asyncUpdater.runTaskAsynchronously(PseudoAPI.plugin);
		}
	}

	private static void downloadFiles(ArrayList<UpdateData> files) {
		if (asyncUpdater == null) {
			asyncUpdater = new Download(files);
			asyncUpdater.runTaskAsynchronously(PseudoAPI.plugin);
		}
	}

	public static boolean shouldRestart() {
		return shouldRestart;
	}

	public static void restartCheck() {
		if (shouldRestart)
			if (!Config.restartEmpty || Bukkit.getOnlinePlayers().size() == 0)
				restart();
	}

	private static void restart() {
		if (Bukkit.getOnlinePlayers().size() != 0) {
			for (Player p : Bukkit.getOnlinePlayers())
				PseudoAPI.plugin.getChat().sendPluginMessage(p, Config.errorTextColor + LanguageManager.getLanguage(p).getMessage("pseudoapi.server_restarting_for_updates_in", Config.restartWarning));
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(PseudoAPI.plugin, new Runnable() {
				public void run() {
					for (Player p : Bukkit.getOnlinePlayers()) {
						p.kickPlayer(LanguageManager.getLanguage(p).getMessage("pseudoapi.server_restarting_for_updates"));
					}
					if (Bukkit.getServer().getVersion().toLowerCase().contains("spigot") || Bukkit.getServer().getVersion().toLowerCase().contains("paper")) {
						Bukkit.getServer().spigot().restart();
					} else {
						Bukkit.getServer().shutdown();
					}
				}
			}, Config.restartWarning * 20);
		} else {
			for (Player p : Bukkit.getOnlinePlayers())
				PseudoAPI.plugin.getChat().sendPluginMessage(p, Config.errorTextColor + LanguageManager.getLanguage(p).getMessage("pseudoapi.server_restarting_for_updates_now"));
			if (Bukkit.getServer().getVersion().toLowerCase().contains("spigot") || Bukkit.getServer().getVersion().toLowerCase().contains("paper")) {
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
			case 3:
				checkUpdates(sender, pluginName);
				break;
			default:
				break;
			}
		}

		public static void checkUpdates(CommandSender sender) {
			checkUpdates(sender, null);
		}

		public static void checkUpdates(CommandSender sender, String pluginName) {
			ArrayList<UpdateData> updateUrls = new ArrayList<UpdateData>();
			int updates = 0;
			PseudoAPI.plugin.getChat().sendPluginMessage(sender, LanguageManager.getLanguage(sender).getMessage("pseudoapi.beginning_update_check"));
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (!p.getName().equals(sender.getName()) && p.hasPermission("pseudoapi.update.notify")) {
					PseudoAPI.plugin.getChat().sendPluginMessage(p, LanguageManager.getLanguage(p).getMessage("pseudoapi.beginning_update_check"));
				}
			}
			Class<JavaPlugin> javaPluginC = JavaPlugin.class;
			Method getFileM = null;
			try {
				getFileM = javaPluginC.getDeclaredMethod("getFile");
				getFileM.setAccessible(true);
			} catch (NoSuchMethodException | SecurityException e1) {
				PseudoAPI.plugin.getChat().sendPluginError(sender, Errors.CUSTOM, LanguageManager.getLanguage(sender).getMessage("pseudoapi.could_not_get_plugin_jar"));
				e1.printStackTrace();
			}
			for (PseudoPlugin p : plugins) {
				if (pluginName == null || p.getName().equalsIgnoreCase(pluginName)) {
					if (alreadyUpdated.contains(p)) {
						continue;
					}
					String versionURL = "https://nexus.otake.pw/repository/maven-public/io/github/pseudoresonance/" + p.getName() + "/maven-metadata.xml";
					try {
						DocumentBuilder db = dbf.newDocumentBuilder();
						URLConnection con = new URL(versionURL).openConnection();
						con.setRequestProperty("User-Agent", userAgent);
						con.connect();
						try (InputStream is = con.getInputStream()) {
							Document doc = db.parse(is);
							doc.getDocumentElement().normalize();
							String version = doc.getElementsByTagName("latest").item(0).getTextContent();
							if (isNewer(version, p.getVersion())) {
								updates++;
								PseudoAPI.plugin.getChat().sendPluginMessage(sender, LanguageManager.getLanguage(sender).getMessage("pseudoapi.version_queue_update", p.getName(), p.getVersion(), version));
								for (Player pl : Bukkit.getOnlinePlayers()) {
									if (!pl.getName().equals(sender.getName()) && pl.hasPermission("pseudoapi.update.notify")) {
										PseudoAPI.plugin.getChat().sendPluginMessage(pl, LanguageManager.getLanguage(pl).getMessage("pseudoapi.version_queue_update", p.getName(), p.getVersion(), version));
									}
								}
								try {
									if (getFileM != null) {
										Object file = getFileM.invoke(p);
										if (file instanceof File) {
											Bukkit.getUpdateFolderFile().mkdir();
											updateUrls.add(new UpdateData(new File(Bukkit.getUpdateFolderFile(), ((File) file).getName()), "https://ci.otake.pw/job/" + p.getName() + "/lastSuccessfulBuild/artifact/artifacts/" + p.getName() + ".jar"));
											alreadyUpdated.add(p);
										}
									}
								} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
									PseudoAPI.plugin.getChat().sendPluginError(sender, Errors.CUSTOM, LanguageManager.getLanguage(sender).getMessage("pseudoapi.could_not_get_plugin_jar"));
									e.printStackTrace();
								}
							} else {
								PseudoAPI.plugin.getChat().sendPluginMessage(sender, LanguageManager.getLanguage(sender).getMessage("pseudoapi.version_already_updated", p.getName(), p.getVersion(), version));
								for (Player pl : Bukkit.getOnlinePlayers()) {
									if (!pl.getName().equals(sender.getName()) && pl.hasPermission("pseudoapi.update.notify")) {
										PseudoAPI.plugin.getChat().sendPluginMessage(pl, LanguageManager.getLanguage(pl).getMessage("pseudoapi.version_already_updated", p.getName(), p.getVersion(), version));
									}
								}
							}
						}
					} catch (IOException | SAXException | ParserConfigurationException e1) {
						e1.printStackTrace();
					}
				}
			}
			if (updates > 0) {
				PseudoAPI.plugin.getChat().sendPluginMessage(sender, LanguageManager.getLanguage(sender).getMessage("pseudoapi.completed_update_check", updates));
				for (Player p : Bukkit.getOnlinePlayers()) {
					if (!p.getName().equals(sender.getName()) && p.hasPermission("pseudoapi.update.notify")) {
						PseudoAPI.plugin.getChat().sendPluginMessage(p, LanguageManager.getLanguage(p).getMessage("pseudoapi.completed_update_check", updates));
					}
				}
				if (updates > updateUrls.size()) {
					int failedUpdates = updates - updateUrls.size();
					PseudoAPI.plugin.getChat().sendPluginMessage(sender, LanguageManager.getLanguage(sender).getMessage("pseudoapi.plugins_could_not_be_updated", failedUpdates));
					for (Player p : Bukkit.getOnlinePlayers()) {
						if (!p.getName().equals(sender.getName()) && p.hasPermission("pseudoapi.update.notify")) {
							PseudoAPI.plugin.getChat().sendPluginMessage(p, LanguageManager.getLanguage(p).getMessage("pseudoapi.plugins_could_not_be_updated", failedUpdates));
						}
					}
				}
				if (updateUrls.size() > 0 && Config.downloadUpdates) {
					downloadFiles(updateUrls, sender);
				}
			} else {
				PseudoAPI.plugin.getChat().sendPluginMessage(sender, LanguageManager.getLanguage(sender).getMessage("pseudoapi.completed_update_check", "No"));
				for (Player p : Bukkit.getOnlinePlayers()) {
					if (!p.getName().equals(sender.getName()) && p.hasPermission("pseudoapi.update.notify")) {
						PseudoAPI.plugin.getChat().sendPluginMessage(p, LanguageManager.getLanguage(p).getMessage("pseudoapi.completed_update_check", "No"));
					}
				}
				if (updateTaskID != -1) {
					Bukkit.getServer().getScheduler().cancelTask(updateTaskID);
					updateTaskID = -1;
				}
				if (Config.updateFrequency > 0) {
					updateTaskID = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(PseudoAPI.plugin, new Runnable() {
						public void run() {
							updateTaskID = -1;
							checkUpdates(false);
						}
					}, Config.updateFrequency * 60 * 20);
				}
			}
		}

		protected static void checkUpdates(boolean startup) {
			if (!(Config.startupUpdate || !startup)) {
				return;
			}
			ArrayList<UpdateData> updateUrls = new ArrayList<UpdateData>();
			int updates = 0;
			PseudoAPI.plugin.getChat().sendPluginMessage(Bukkit.getConsoleSender(), LanguageManager.getLanguage().getMessage("pseudoapi.beginning_update_check"));
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (p.hasPermission("pseudoapi.update.notify")) {
					PseudoAPI.plugin.getChat().sendPluginMessage(p, LanguageManager.getLanguage(p).getMessage("pseudoapi.beginning_update_check"));
				}
			}
			Class<JavaPlugin> javaPluginC = JavaPlugin.class;
			Method getFileM = null;
			try {
				getFileM = javaPluginC.getDeclaredMethod("getFile");
				getFileM.setAccessible(true);
			} catch (NoSuchMethodException | SecurityException e1) {
				PseudoAPI.plugin.getChat().sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, LanguageManager.getLanguage().getMessage("pseudoapi.could_not_get_plugin_jar"));
				e1.printStackTrace();
			}
			for (PseudoPlugin p : plugins) {
				if (alreadyUpdated.contains(p)) {
					continue;
				}
				String versionURL = "https://nexus.otake.pw/repository/maven-public/io/github/pseudoresonance/" + p.getName() + "/maven-metadata.xml";
				try {
					DocumentBuilder db = dbf.newDocumentBuilder();
					URLConnection con = new URL(versionURL).openConnection();
					con.setRequestProperty("User-Agent", userAgent);
					con.connect();
					try (InputStream is = con.getInputStream()) {
						Document doc = db.parse(is);
						doc.getDocumentElement().normalize();
						String version = doc.getElementsByTagName("latest").item(0).getTextContent();
						if (isNewer(version, p.getVersion())) {
							updates++;
							PseudoAPI.plugin.getChat().sendPluginMessage(Bukkit.getConsoleSender(), LanguageManager.getLanguage().getMessage("pseudoapi.version_queue_update", p.getName(), p.getVersion(), version));
							for (Player pl : Bukkit.getOnlinePlayers()) {
								if (pl.hasPermission("pseudoapi.update.notify")) {
									PseudoAPI.plugin.getChat().sendPluginMessage(pl, LanguageManager.getLanguage(pl).getMessage("pseudoapi.version_queue_update", p.getName(), p.getVersion(), version));
								}
							}
							try {
								if (getFileM != null) {
									Object file = getFileM.invoke(p);
									if (file instanceof File) {
										Bukkit.getUpdateFolderFile().mkdir();
										updateUrls.add(new UpdateData(new File(Bukkit.getUpdateFolderFile(), ((File) file).getName()), "https://ci.otake.pw/job/" + p.getName() + "/lastSuccessfulBuild/artifact/artifacts/" + p.getName() + ".jar"));
										alreadyUpdated.add(p);
									}
								}
							} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
								PseudoAPI.plugin.getChat().sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, LanguageManager.getLanguage().getMessage("pseudoapi.could_not_get_plugin_jar"));
								e.printStackTrace();
							}
						} else {
							PseudoAPI.plugin.getChat().sendPluginMessage(Bukkit.getConsoleSender(), LanguageManager.getLanguage().getMessage("pseudoapi.version_already_updated", p.getName(), p.getVersion(), version));
							for (Player pl : Bukkit.getOnlinePlayers()) {
								if (pl.hasPermission("pseudoapi.update.notify")) {
									PseudoAPI.plugin.getChat().sendPluginMessage(pl, LanguageManager.getLanguage(pl).getMessage("pseudoapi.version_already_updated", p.getName(), p.getVersion(), version));
								}
							}
						}
					}
				} catch (IOException | SAXException | ParserConfigurationException e1) {
					e1.printStackTrace();
				}
			}
			if (updates > 0) {
				PseudoAPI.plugin.getChat().sendPluginMessage(Bukkit.getConsoleSender(), LanguageManager.getLanguage().getMessage("pseudoapi.completed_update_check", updates));
				for (Player p : Bukkit.getOnlinePlayers()) {
					if (p.hasPermission("pseudoapi.update.notify")) {
						PseudoAPI.plugin.getChat().sendPluginMessage(p, LanguageManager.getLanguage(p).getMessage("pseudoapi.completed_update_check", updates));
					}
				}
				if (updates > updateUrls.size()) {
					int failedUpdates = updates - updateUrls.size();
					PseudoAPI.plugin.getChat().sendPluginMessage(Bukkit.getConsoleSender(), LanguageManager.getLanguage().getMessage("pseudoapi.plugins_could_not_be_updated", failedUpdates));
					for (Player p : Bukkit.getOnlinePlayers()) {
						if (p.hasPermission("pseudoapi.update.notify")) {
							PseudoAPI.plugin.getChat().sendPluginMessage(p, LanguageManager.getLanguage(p).getMessage("pseudoapi.plugins_could_not_be_updated", failedUpdates));
						}
					}
				}
				if (updateUrls.size() > 0 && Config.downloadUpdates) {
					downloadFiles(updateUrls);
				}
			} else {
				PseudoAPI.plugin.getChat().sendPluginMessage(Bukkit.getConsoleSender(), LanguageManager.getLanguage().getMessage("pseudoapi.completed_update_check", "No"));
				for (Player p : Bukkit.getOnlinePlayers()) {
					if (p.hasPermission("pseudoapi.update.notify")) {
						PseudoAPI.plugin.getChat().sendPluginMessage(p, LanguageManager.getLanguage(p).getMessage("pseudoapi.completed_update_check", "No"));
					}
				}
				if (updateTaskID != -1) {
					Bukkit.getServer().getScheduler().cancelTask(updateTaskID);
					updateTaskID = -1;
				}
				if (Config.updateFrequency > 0) {
					updateTaskID = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(PseudoAPI.plugin, new Runnable() {
						public void run() {
							updateTaskID = -1;
							checkUpdates(false);
						}
					}, Config.updateFrequency * 60 * 20);
				}
			}
		}

	}

	private static class Download extends BukkitRunnable {

		private ArrayList<UpdateData> files;
		private CommandSender sender = null;

		private Download(ArrayList<UpdateData> files) {
			this.files = files;
		}

		private Download(ArrayList<UpdateData> files, CommandSender sender) {
			this.files = files;
			this.sender = sender;
		}

		@Override
		public void run() {
			int successfulUpdates = 0;
			for (UpdateData d : files) {
				try {
					URLConnection con = new URL(d.getURL()).openConnection();
					con.setRequestProperty("User-Agent", userAgent);
					con.connect();
					try (InputStream is = con.getInputStream()) {
						Files.copy(is, d.getNewFile().toPath(), StandardCopyOption.REPLACE_EXISTING);
						successfulUpdates++;
					} catch (Exception e) {
						PseudoAPI.plugin.getChat().sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, LanguageManager.getLanguage().getMessage("pseudoapi.completed_update_check", d.getNewFile().getAbsolutePath()));
						e.printStackTrace();
					}
				} catch (Exception e) {
					PseudoAPI.plugin.getChat().sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, LanguageManager.getLanguage().getMessage("pseudoapi.completed_update_check", d.getNewFile().getAbsolutePath()));
					e.printStackTrace();
				}
			}
			if (Config.updateRestart && successfulUpdates > 0) {
				shouldRestart = true;
				restartCheck();
				if (!(!Config.restartEmpty || Bukkit.getOnlinePlayers().size() == 0)) {
					PseudoAPI.plugin.getChat().sendPluginMessage(Bukkit.getConsoleSender(), LanguageManager.getLanguage().getMessage("pseudoapi.waiting_for_server_to_empty"));
					if (sender != null) {
						for (Player p : Bukkit.getOnlinePlayers()) {
							if (!p.getName().equals(sender.getName()) && p.hasPermission("pseudoapi.update.notify")) {
								PseudoAPI.plugin.getChat().sendPluginMessage(p, LanguageManager.getLanguage(p).getMessage("pseudoapi.waiting_for_server_to_empty"));
							}
						}
						if (sender instanceof Player) {
							PseudoAPI.plugin.getChat().sendPluginMessage(sender, LanguageManager.getLanguage(sender).getMessage("pseudoapi.waiting_for_server_to_empty"));
						}
					} else {
						for (Player p : Bukkit.getOnlinePlayers()) {
							if (p.hasPermission("pseudoapi.update.notify")) {
								PseudoAPI.plugin.getChat().sendPluginMessage(p, LanguageManager.getLanguage(p).getMessage("pseudoapi.waiting_for_server_to_empty"));
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

		public File getNewFile() {
			return this.newFile;
		}

		public String getURL() {
			return this.url;
		}

	}

}