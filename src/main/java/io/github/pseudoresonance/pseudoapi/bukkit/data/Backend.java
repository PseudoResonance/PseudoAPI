package io.github.pseudoresonance.pseudoapi.bukkit.data;

public abstract class Backend {
	
	private String name = "";
	
	public Backend(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public boolean equals(Backend obj) {
		if (obj.getName().equals(this.name)) {
			return true;
		}
		return false;
	}

}
