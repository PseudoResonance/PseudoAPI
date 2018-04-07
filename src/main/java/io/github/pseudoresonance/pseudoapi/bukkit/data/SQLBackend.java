package io.github.pseudoresonance.pseudoapi.bukkit.data;

import org.apache.commons.dbcp2.BasicDataSource;

public interface SQLBackend extends Backend {
	
	public BasicDataSource getDataSource();
	
	public String getPrefix();

}
