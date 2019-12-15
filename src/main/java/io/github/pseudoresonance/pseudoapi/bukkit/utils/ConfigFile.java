package io.github.pseudoresonance.pseudoapi.bukkit.utils;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import io.github.pseudoresonance.pseudoapi.bukkit.PseudoPlugin;

public class ConfigFile {

	private final PseudoPlugin PLUGIN;
	private final String FILENAME;
	private final File FOLDER;
	private FileConfiguration config;
	private File configFile;

	/**
	 * Constructs new {@link ConfigFile} with given parameters
	 * 
	 * @param filename Filename of config file
	 * @param instance {@link PseudoPlugin} config file is for
	 */
	public ConfigFile(String filename, PseudoPlugin instance) {
		if (!filename.endsWith(".yml")) {
			filename += ".yml";
		}
		this.FILENAME = filename;
		this.PLUGIN = instance;
		this.FOLDER = this.PLUGIN.getDataFolder();
		this.config = null;
		this.configFile = null;
		reload();
	}

	/**
	 * Constructs new {@link ConfigFile} with given parameters
	 * 
	 * @param folder Folder that config file is stored in
	 * @param filename Filename of config file
	 * @param instance {@link PseudoPlugin} config file is for
	 */
	public ConfigFile(File folder, String filename, PseudoPlugin instance) {
		if (!filename.endsWith(".yml")) {
			filename += ".yml";
		}
		this.FILENAME = filename;
		this.PLUGIN = instance;
		this.FOLDER = folder;
		this.config = null;
		this.configFile = null;
		reload();
	}

	/**
	 * Returns {@link FileConfiguration} from config file
	 * 
	 * @return {@link FileConfiguration} from config file
	 */
	public FileConfiguration getConfig() {
		if (config == null) {
			reload();
		}
		return config;
	}

	/**
	 * Reloads config file from disk
	 */
	public void reload() {
		if (!this.FOLDER.exists()) {
			try {
				if (this.FOLDER.mkdir()) {
					this.PLUGIN.getLogger().log(Level.INFO, "Folder " + this.FOLDER.getName() + " created.");
				} else {
					this.PLUGIN.getLogger().log(Level.WARNING, "Unable to create folder " + this.FOLDER.getName() + ".");
				}
			} catch (Exception e) {

			}
		}
		configFile = new File(this.FOLDER, this.FILENAME);
		if (!configFile.exists()) {
			try {
				configFile.createNewFile();
			} catch (IOException e) {

			}
		}
		config = YamlConfiguration.loadConfiguration(configFile);
	}

	/**
	 * Saves default config file from the jar
	 */
	public void saveDefaultConfig() {
		if (configFile == null) {
			configFile = new File(this.FOLDER, this.FILENAME);
		}
		if (!configFile.exists()) {
			this.PLUGIN.saveResource(this.FILENAME, false);
		}
	}

	/**
	 * Saves config file to disk
	 */
	public void save() {
		if (config == null || configFile == null) {
			return;
		}
		try {
			getConfig().save(configFile);
		} catch (IOException ex) {
			this.PLUGIN.getLogger().log(Level.WARNING, "Could not save config to " + configFile.getName(), ex);
		}
	}

	/**
	 * Sets data at the given path
	 * 
	 * @param path Path to set data to
	 * @param o Data to set
	 */
	public void set(String path, Object o) {
		getConfig().set(path, o);
	}

	/**
	 * Sets a {@link Location} object at the given path
	 * 
	 * @param path Path to set data to
	 * @param l {@link Location} to set
	 */
	public void setLocation(String path, Location l) {
		getConfig().set(path + ".w", l.getWorld().getName());
		getConfig().set(path + ".x", l.getX());
		getConfig().set(path + ".y", l.getY());
		getConfig().set(path + ".z", l.getZ());
		getConfig().set(path + ".yaw", l.getYaw());
		getConfig().set(path + ".pitch", l.getPitch());
		save();
	}

	/**
	 * Gets a {@link Location} from the given path
	 * 
	 * @param path Path to get data from
	 * @return {@link Location} from the given path
	 */
	public Location getLocation(String path) {
		Location l = new Location(Bukkit.getWorld(getConfig().getString(path + ".w")), getConfig().getDouble(path + ".x"), getConfig().getDouble(path + ".y"), getConfig().getDouble(path + ".z"), Float.parseFloat("" + getConfig().getDouble(path + ".yaw")), Float.parseFloat("" + getConfig().getDouble(path + ".pitch")));
		return l;
	}
}