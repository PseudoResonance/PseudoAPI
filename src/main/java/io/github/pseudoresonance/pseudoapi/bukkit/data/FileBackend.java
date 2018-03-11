package io.github.pseudoresonance.pseudoapi.bukkit.data;

import java.io.File;

public class FileBackend extends Backend {
	
	private File folder;

	public FileBackend(String name, File folder) {
		super(name);
		this.folder = folder;
	}
	
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
