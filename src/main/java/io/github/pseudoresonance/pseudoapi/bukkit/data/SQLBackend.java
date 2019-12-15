package io.github.pseudoresonance.pseudoapi.bukkit.data;

import org.apache.commons.dbcp2.BasicDataSource;

public interface SQLBackend extends Backend {
	
	/**
	 * Returns {@link BasicDataSource} of SQL backend for accessing data manually
	 * 
	 * @return {@link BasicDataSource} of SQL backend
	 */
	public BasicDataSource getDataSource();
	
	/**
	 * Returns table prefix to be used in SQL database
	 * 
	 * @return SQL database table prefix
	 */
	public String getPrefix();

}
