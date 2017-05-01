package io.github.wolfleader116.wolfapi.bukkit.data;

import java.io.File;

public class FileBackend extends Backend {
	
	private File folder;
	private String file;

	public FileBackend(String name, File folder, String file) {
		super(name);
		this.folder = folder;
		this.file = file;
	}
	
	public File getFolder() {
		return this.folder;
	}
	
	public String getFile() {
		return this.file;
	}
	
	public boolean equals(Backend obj) {
		if (obj instanceof FileBackend) {
			FileBackend b = (FileBackend) obj;
			if (b.getFolder().equals(this.folder) && b.getFile().equals(this.file)) {
				return true;
			}
		}
		return false;
	}

}
