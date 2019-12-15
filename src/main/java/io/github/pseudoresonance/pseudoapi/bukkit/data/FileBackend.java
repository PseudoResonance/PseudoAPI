package io.github.pseudoresonance.pseudoapi.bukkit.data;

import java.io.File;

public class FileBackend implements Backend {
	
	private boolean enabled = false;
	
	private final String name;
	private final File folder;

	/**
	 * Constructs a new {@link FileBackend} with the given name and directory location
	 * 
	 * @param name Backend name
	 * @param folder Directory of backend files
	 */
	public FileBackend(String name, File folder) {
		this.name = name;
		this.folder = folder;
	}

	public void setup() {
		enabled = true;
	}

	public void stop() {
		enabled = false;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public String getName() {
		return this.name;
	}
	
	/**
	 * Returns directory that files are stored in
	 * 
	 * @return Backend directory
	 */
	public File getFolder() {
		return this.folder;
	}
	
	public boolean equals(Backend obj) {
		if (obj instanceof FileBackend) {
			FileBackend b = (FileBackend) obj;
			if (b.getFolder().equals(this.folder)) {
				return true;
			}
		}
		return false;
	}

}
