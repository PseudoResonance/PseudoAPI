package io.github.wolfleader116.wolfapi.bukkit;

public class CommandDescription {
	
	private String command = "";
	private String description = "";
	private String permission = "";
	
	public CommandDescription(String command, String description, String permission) {
		this.command = command;
		this.description = description;
		this.permission = permission;
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

}
