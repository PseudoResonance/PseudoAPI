package io.github.wolfleader116.wolfapi.bukkit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
	private String output = "";
	protected Map<String, SubCommandExecutor> subCommands = new HashMap<String, SubCommandExecutor>();
	protected List<CommandDescription> commandDescriptions = new ArrayList<CommandDescription>();
	public Message message;

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
		message = new Message(this);
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
		return output;
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

	public void saveJarResource(String name, boolean overwrite) {
		File targetFile = new File(this.getDataFolder() + File.pathSeparator + name);
		if ((targetFile.exists() && overwrite) || !targetFile.exists()) {
			if (targetFile.exists()) {
				targetFile.delete();
			}
			try {
				InputStream initialStream = this.getResource(name);
				byte[] buffer;
				buffer = new byte[initialStream.available()];
				initialStream.read(buffer);
				OutputStream outStream = new FileOutputStream(targetFile);
				outStream.write(buffer);
				initialStream.close();
				outStream.close();
			} catch (IOException e) {
				Message.sendConsoleMessage("Plugin: " + this.name + " failed to save resource: " + name);
				e.printStackTrace();
			}
		}
	}

}
