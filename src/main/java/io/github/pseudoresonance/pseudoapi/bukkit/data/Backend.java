package io.github.pseudoresonance.pseudoapi.bukkit.data;

public interface Backend {
	
	public void setup();
	
	public void stop();
	
	public boolean isEnabled();
	
	public String getName();
	
	public boolean equals(Backend obj);

}
