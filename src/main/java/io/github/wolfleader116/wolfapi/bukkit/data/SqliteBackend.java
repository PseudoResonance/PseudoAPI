package io.github.wolfleader116.wolfapi.bukkit.data;

import java.io.File;

public class SqliteBackend extends Backend {
	
	private File location;
	private String username = "username";
	private String password = "password";
	private String prefix = "Wolf_";

	public SqliteBackend(String name, File location, String username, String password, String prefix) {
		super(name);
		this.location = location;
		this.username = username;
		this.password = password;
		this.prefix = prefix;
	}
	
	public File getLocation() {
		return this.location;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public String getPrefix() {
		return this.prefix;
	}
	
	public boolean equals(Backend obj) {
		if (obj instanceof SqliteBackend) {
			SqliteBackend b = (SqliteBackend) obj;
			if (b.getLocation().equals(this.location) && b.getUsername().equals(this.username) && b.getPassword().equals(this.password) && b.getPrefix().equals(this.prefix)) {
				return true;
			}
		}
		return false;
	}

}
