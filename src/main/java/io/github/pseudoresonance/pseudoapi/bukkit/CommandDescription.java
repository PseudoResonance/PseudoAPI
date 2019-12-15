package io.github.pseudoresonance.pseudoapi.bukkit;

public class CommandDescription {

	private String command = "";
	private String description_key = "";
	private String permission = "";
	private boolean runnable = true;

	public CommandDescription(String command, String description_key, String permission) {
		this.command = command;
		this.description_key = description_key;
		this.permission = permission;
	}

	public CommandDescription(String command, String description_key, String permission, boolean runnable) {
		this.command = command;
		this.description_key = description_key;
		this.permission = permission;
		this.runnable = runnable;
	}

	public String getCommand() {
		return command;
	}

	public String getDescriptionKey() {
		return description_key;
	}

	public String getPermission() {
		return permission;
	}

	public boolean getRunnable() {
		return runnable;
	}

}
