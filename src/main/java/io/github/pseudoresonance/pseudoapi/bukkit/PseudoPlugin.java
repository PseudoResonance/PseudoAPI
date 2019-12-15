package io.github.pseudoresonance.pseudoapi.bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.pseudoresonance.pseudoapi.bukkit.language.LanguageManager;
import io.github.pseudoresonance.pseudoapi.bukkit.messaging.PluginMessenger;

public class PseudoPlugin extends JavaPlugin {

	private String name = "";
	private String prefix = "";
	private String description = "";
	private List<String> authors = new ArrayList<String>();
	private String version = "";
	private List<String> depend = new ArrayList<String>();
	private List<String> softDepend = new ArrayList<String>();
	private List<String> loadBefore = new ArrayList<String>();
	private Map<String, Map<String, Object>> commands = new HashMap<String, Map<String, Object>>();
	private List<Permission> permissions = new ArrayList<Permission>();
	private String output = "";
	protected Map<String, SubCommandExecutor> subCommands = new HashMap<String, SubCommandExecutor>();
	protected List<CommandDescription> commandDescriptions = new ArrayList<CommandDescription>();
	private Chat chat;
	
	/**
	 * On enable method that is required to be run by all {@link PseudoPlugin}s in order to properly initialize
	 */
	@Override
	public void onEnable() {
		super.onEnable();
		PluginController.pluginLoaded(this);
		name = this.getDescription().getName();
		prefix = this.getDescription().getPrefix();
		description = this.getDescription().getDescription();
		authors = this.getDescription().getAuthors();
		version = this.getDescription().getVersion();
		depend = this.getDescription().getDepend();
		softDepend = this.getDescription().getSoftDepend();
		loadBefore = this.getDescription().getLoadBefore();
		commands = this.getDescription().getCommands();
		permissions = this.getDescription().getPermissions();
		if (name == null) {
			name = "";
		}
		if (prefix == null) {
			prefix = "";
		}
		if (description == null) {
			description = "";
		}
		if (authors == null) {
			authors = new ArrayList<String>();
		}
		if (version == null) {
			version = "";
		}
		if (depend == null) {
			depend = new ArrayList<String>();
		}
		if (softDepend == null) {
			softDepend = new ArrayList<String>();
		}
		if (loadBefore == null) {
			loadBefore = new ArrayList<String>();
		}
		if (commands == null) {
			commands = new HashMap<String, Map<String, Object>>();
		}
		if (permissions == null) {
			permissions = new ArrayList<Permission>();
		}
		if (prefix.equals("")) {
			output = name;
		} else {
			output = prefix;
		}
		chat = new Chat(this);
		LanguageManager.copyDefaultPluginLanguageFiles(this, false);
	}

	/**
	 * On disable method that is required to be run by all {@link PseudoPlugin}s in order to properly shut down
	 */
	@Override
	public void onDisable() {
		CommandHandler.unregisterPlugin(this);
		PluginMessenger.unregisterPluginListeners(this);
	}
	
	/**
	 * Returns {@link Chat} instance of this plugin
	 * 
	 * @return {@link Chat} instance
	 */
	public Chat getChat() {
		return chat;
	}

	/**
	 * Returns plugin name
	 * 
	 * @return Plugin name
	 */
	public String getPluginName() {
		return name;
	}

	/**
	 * Returns plugin chat prefix
	 * 
	 * @return Chat prefix
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * Returns friendly output name of plugin
	 * 
	 * @return Friendly name
	 */
	public String getOutputName() {
		return output;
	}

	/**
	 * Returns plugin description
	 * 
	 * @return Plugin description
	 */
	public String getPluginDescription() {
		return description;
	}

	/**
	 * Returns list of authors
	 * 
	 * @return Plugin authors
	 */
	public List<String> getAuthors() {
		return authors;
	}

	/**
	 * Returns plugin version
	 * 
	 * @return Plugin version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Returns list of plugin dependencies
	 * 
	 * @return Plugin dependencies
	 */
	public List<String> getDepend() {
		return depend;
	}

	/**
	 * Returns list of soft plugin dependencies
	 * 
	 * @return Soft plugin dependencies
	 */
	public List<String> getSoftDepend() {
		return softDepend;
	}

	/**
	 * Returns list of plugins that depend on this plugin
	 * 
	 * @return Load before list
	 */
	public List<String> getLoadBefore() {
		return loadBefore;
	}

	/**
	 * Returns map of plugin commands
	 * 
	 * @return Plugin commands
	 */
	public Map<String, Map<String, Object>> getCommands() {
		return commands;
	}

	/**
	 * Returns list of declared permissions
	 * 
	 * @return List of permissions
	 */
	public List<Permission> getPermissions() {
		return permissions;
	}
	
	/**
	 * Registers a command that will forcefully override all other plugins
	 * 
	 * @param cmd Command to be overwritten
	 * @param executor Executor for the command
	 */
	public void registerCommandOverride(String cmd, CommandExecutor executor) {
		CommandHandler.registerCommand(this, cmd, executor);
	}
	
	/**
	 * Registers a command that will forcefully override all other plugins
	 * 
	 * @param cmd Command to be overwritten
	 * @param executor Executor for the command
	 * @param completer Tab completer for command
	 */
	public void registerDynamicCommand(String cmd, CommandExecutor executor, TabCompleter completer) {
		CommandHandler.registerCommand(this, cmd, executor, completer);
	}
	
	/**
	 * Unregisters overwritten command
	 * 
	 * @param cmd Command to be unregistered
	 */
	public void unregisterDynamicCommand(String cmd) {
		CommandHandler.unregisterCommand(this, cmd);
	}

}
