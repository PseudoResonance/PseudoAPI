package io.github.pseudoresonance.pseudoapi.bukkit.data;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class MySQLBackend implements SQLBackend {

	private HikariDataSource dataSource;

	private boolean enabled = false;

	private final String name;
	private final String host;
	private final int port;
	private final String username;
	private final String password;
	private final String database;
	private final String prefix;
	private final boolean useSSL;
	private final boolean verifyServerCertificate;
	private final boolean requireSSL;
	private final String url;

	/**
	 * Constructs new {@link MySQLBackend} with the given parameters
	 * 
	 * @param name
	 *            Backend name
	 * @param host
	 *            MySQL server host
	 * @param port
	 *            MySQL server port
	 * @param username
	 *            MySQL username
	 * @param password
	 *            MySQL password
	 * @param database
	 *            MySQL database name
	 * @param prefix
	 *            MySQL table prefix
	 * @param useSSL
	 *            Whether or not to use SSL when connecting to MySQL server
	 * @param verifyServerCertificate
	 *            Whether or not to verify server certificates when connecting to
	 *            MySQL server
	 * @param requireSSL
	 *            Whether or not SSL is required when connecting to MySQL server
	 */
	public MySQLBackend(String name, String host, int port, String username, String password, String database, String prefix, boolean useSSL, boolean verifyServerCertificate, boolean requireSSL) {
		this.name = name;
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		this.database = database;
		this.prefix = prefix;
		this.useSSL = useSSL;
		this.verifyServerCertificate = verifyServerCertificate;
		this.requireSSL = requireSSL;
		String suffix = "?";
		if (useSSL)
			suffix += "useSSL=true";
		else
			suffix += "useSSL=false";
		if (verifyServerCertificate)
			suffix += "&verifyServerCertificate=true";
		else
			suffix += "&verifyServerCertificate=false";
		if (requireSSL)
			suffix += "&requireSSL=true";
		else
			suffix += "&requireSSL=false";
		this.url = "jdbc:mysql://" + host + ":" + port + "/" + database + suffix;
	}

	public DataSource getDataSource() {
		if (enabled) {
			if (dataSource == null) {
				HikariConfig config = new HikariConfig();
				config.setJdbcUrl(this.url);
				config.setUsername(this.username);
				config.setPassword(this.password);
				config.addDataSourceProperty("cachePrepStmts", "true");
				config.addDataSourceProperty("prepStmtCacheSize", "250");
				config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
				dataSource = new HikariDataSource(config);
			}
			return dataSource;
		} else
			return null;
	}

	public void setup() {
		enabled = true;
		getDataSource();
	}

	public void stop() {
		enabled = false;
		if (dataSource != null) {
			dataSource.close();
			dataSource = null;
		}
	}

	public boolean isEnabled() {
		return enabled;
	}

	public String getName() {
		return this.name;
	}

	/**
	 * Returns MySQL server host
	 * 
	 * @return MySQL server host
	 */
	public String getHost() {
		return this.host;
	}

	/**
	 * Returns MySQL server port
	 * 
	 * @return MySQL server port
	 */
	public int getPort() {
		return this.port;
	}

	/**
	 * Returns MySQL username
	 * 
	 * @return MySQL username
	 */
	public String getUsername() {
		return this.username;
	}

	/**
	 * Returns MySQL password
	 * 
	 * @return MySQL password
	 */
	public String getPassword() {
		return this.password;
	}

	/**
	 * Returns MySQL database name
	 * 
	 * @return MySQL database name
	 */
	public String getDatabase() {
		return this.database;
	}

	/**
	 * Returns MySQL table prefix
	 * 
	 * @return MySQL table prefix
	 */
	public String getPrefix() {
		return this.prefix;
	}

	/**
	 * Returns whether or not to use SSL when connecting to MySQL server
	 * 
	 * @return Whether or not to use SSL when connecting to MySQL server
	 */
	public boolean getUseSSL() {
		return this.useSSL;
	}

	/**
	 * Returns whether or not to verify server certificates when connecting to MySQL
	 * server
	 * 
	 * @return Whether or not to verify server certificates when connecting to MySQL
	 *         server
	 */
	public boolean getVerifyServerCertificate() {
		return this.verifyServerCertificate;
	}

	/**
	 * Returns whether or not SSL is required when connecting to MySQL server
	 * 
	 * @return Whether or not SSL is required when connecting to MySQL server
	 */
	public boolean getRequireSSL() {
		return this.requireSSL;
	}

	/**
	 * Returns MySQL connection URL
	 * 
	 * @return MySQL connection URL
	 */
	public String getURL() {
		return this.url;
	}

	public boolean equals(Backend obj) {
		if (obj instanceof MySQLBackend) {
			MySQLBackend b = (MySQLBackend) obj;
			if (b.getHost().equals(this.host) && b.getPort() == this.port && b.getUsername().equals(this.username) && b.getPassword().equals(this.password) && b.getDatabase().equals(this.database) && b.getPrefix().equals(this.prefix)) {
				return true;
			}
		}
		return false;
	}

}
