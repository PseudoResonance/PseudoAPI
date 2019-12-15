package io.github.pseudoresonance.pseudoapi.bukkit.data;

public interface Backend {
	
	/**
	 * Sets up backend
	 */
	public void setup();
	
	/**
	 * Shuts down backend
	 */
	public void stop();
	
	/**
	 * Returns whether or not backend is currently available
	 * 
	 * @return Backend availability
	 */
	public boolean isEnabled();
	
	/**
	 * Returns name of backend
	 * 
	 * @return Backend name
	 */
	public String getName();
	
	/**
	 * Returns whether or not two backends are the same
	 * 
	 * @param obj Backend to compare against
	 * @return Whether or not backends are the same
	 */
	public boolean equals(Backend obj);

}
