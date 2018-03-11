package io.github.pseudoresonance.pseudoapi.bukkit;

public class ChatComponent {
	
	public ComponentType type;
	public String value;
	
	public ChatComponent(ComponentType type, String value) {
		this.type = type;
		this.value = value;
	}
	
	public ComponentType type() {
		return this.type;
	}
	
	public String value() {
		return this.value;
	}
	
	public void type(ComponentType type) {
		this.type = type;
	}
	
	public void value(String value) {
		this.value = value;
	}

}