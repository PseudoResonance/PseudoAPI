package io.github.wolfleader116.wolfapi.bukkit;

public class ElementBuilder {
	
	private ChatElement[] elements;
	
	public ElementBuilder(ChatElement... chatElements) {
		elements = chatElements;
	}
	
	public ChatElement[] build() {
		return elements;
	}

}
