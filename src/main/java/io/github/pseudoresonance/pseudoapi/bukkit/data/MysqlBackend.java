package io.github.pseudoresonance.pseudoapi.bukkit.data;

public class MysqlBackend extends Backend {
	
	private String host = "127.0.0.1";
	private int port = 3306;
	private String username = "username";
	private String password = "password";
	private String database = "PseudoAPI";
	private String prefix = "PseudoAPI_";

	public MysqlBackend(String name, String host, int port, String username, String password, String database, String prefix) {
		super(name);
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		this.database = database;
		this.prefix = prefix;
	}
	
	public String getHost() {
		return this.host;
	}
	
	public int getPort() {
		return this.port;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public String getDatabase() {
		return this.database;
	}
	
	public String getPrefix() {
		return this.prefix;
	}
	
	public boolean equals(Backend obj) {
		if (obj instanceof MysqlBackend) {
			MysqlBackend b = (MysqlBackend) obj;
			if (b.getHost().equals(this.host) && b.getPort() == this.port && b.getUsername().equals(this.username) && b.getPassword().equals(this.password) && b.getDatabase().equals(this.database) && b.getPrefix().equals(this.prefix)) {
				return true;
			}
		}
		return false;
	}

}
