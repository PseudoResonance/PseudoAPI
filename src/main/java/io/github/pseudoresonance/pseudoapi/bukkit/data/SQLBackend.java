package io.github.pseudoresonance.pseudoapi.bukkit.data;

import javax.sql.DataSource;

public interface SQLBackend extends Backend {
	
	/**
	 * Returns {@link DataSource} of SQL backend for accessing data manually
	 * 
	 * @return {@link DataSource} of SQL backend
	 */
	public DataSource getDataSource();
	
	/**
	 * Returns table prefix to be used in SQL database
	 * 
	 * @return SQL database table prefix
	 */
	public String getPrefix();

}
