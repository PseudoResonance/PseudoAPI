package io.github.wolfleader116.wolfapi.bukkit;

public class CommandDescription {
	
	private String command = "";
	private String description = "";
	private String permission = "";
	private boolean runnable = true;
	
	public CommandDescription(String command, String description, String permission) {
		this.command = command;
		this.description = description;
		this.permission = permission;
	}
	
	public CommandDescription(String command, String description, String permission, boolean runnable) {
		this.command = command;
		this.description = description;
		this.permission = permission;
		this.runnable = runnable;
	}
	
	public String getCommand() {
		return command;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getPermission() {
		return permission;
	}
	
	public boolean getRunnable() {
		return runnable;
	}

}
