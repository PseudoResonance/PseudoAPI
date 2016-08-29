package io.github.wolfleader116.wolfapi.bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;

public class WolfPlugin extends JavaPlugin {
	
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
	protected Map<String, SubCommandExecutor> subCommands = new HashMap<String, SubCommandExecutor>();
	protected List<CommandDescription> commandDescriptions = new ArrayList<CommandDescription>();
	
	@Override
	public void onEnable() {
		super.onEnable();
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
		PluginController.pluginLoaded(this);
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
	}
	
	public String getPluginName() {
		return name;
	}
	
	public String getPrefix() {
		return prefix;
	}
	
	public String getOutputName() {
		if (prefix.equals("")) {
			return name;
		} else {
			return prefix;
		}
	}
	
	public String getPluginDescription() {
		return description;
	}
	
	public List<String> getAuthors() {
		return authors;
	}
	
	public String getVersion() {
		return version;
	}
	
	public List<String> getDepend() {
		return depend;
	}
	
	public List<String> getSoftDepend() {
		return softDepend;
	}
	
	public List<String> getLoadBefore() {
		return loadBefore;
	}
	
	public Map<String, Map<String, Object>> getCommands() {
		return commands;
	}
	
	public List<Permission> getPermissions() {
		return permissions;
	}

}
