package io.github.pseudoresonance.pseudoapi.bukkit;

public class CommandDescription {

	private String command = "";
	private String description_key = "";
	private String permission = "";
	private boolean runnable = true;

	/**
	 * Instantiates {@link CommandDescription} with the various settings for use in help messages
	 * 
	 * @param command Command name
	 * @param description_key Localization key pointing to a description of the command
	 * @param permission Permission required to use the command
	 */
	public CommandDescription(String command, String description_key, String permission) {
		this.command = command;
		this.description_key = description_key;
		this.permission = permission;
	}

	/**
	 * Instantiates {@link CommandDescription} with the various settings for use in help messages
	 * 
	 * @param command Command name
	 * @param description_key Localization key pointing to a description of the command
	 * @param permission Permission required to use the command
	 * @param runnable Whether or not the supplied command is immediately runnable. Ex: If it requires additional parameters
	 */
	public CommandDescription(String command, String description_key, String permission, boolean runnable) {
		this.command = command;
		this.description_key = description_key;
		this.permission = permission;
		this.runnable = runnable;
	}

	/**
	 * Returns name of command
	 * 
	 * @return Command name
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * Returns localization key pointing to a description of the command
	 * 
	 * @return Localization key
	 */
	public String getDescriptionKey() {
		return description_key;
	}

	/**
	 * Returns permission required to run the command
	 * 
	 * @return Required permission
	 */
	public String getPermission() {
		return permission;
	}

	/**
	 * Returns whether or not the command is immediately runnable. Ex: If it requires additional parameters
	 * 
	 * @return Whether or not the command can immediately be run
	 */
	public boolean getRunnable() {
		return runnable;
	}

}
