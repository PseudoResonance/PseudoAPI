package com.xxmicloxx.NoteBlockAPI;

import org.bukkit.Sound;

import io.github.wolfleader116.wolfapi.bukkit.WolfAPI;

public class CustomInstrument {

	private byte index;
	private String name;
	private String soundfile;
	@SuppressWarnings("unused")
	private byte pitch;
	@SuppressWarnings("unused")
	private byte press;
	private Sound sound;

	public CustomInstrument(byte index, String name, String soundfile, byte pitch, byte press) {
		this.index = index;
		this.name = name;
		this.soundfile = soundfile.replaceAll(".ogg", "");
		if (this.soundfile.equalsIgnoreCase("pling")) {
			switch (WolfAPI.getCompatibility()) {
			case WolfAPI.NoteBlockCompatibility.pre1_9:
				this.sound = Sound.valueOf("NOTE_PLING");
				break;
			case WolfAPI.NoteBlockCompatibility.pre1_12:
			case WolfAPI.NoteBlockCompatibility.post1_12:
				this.sound = Sound.valueOf("BLOCK_NOTE_PLING");
				break;
			}
		}
		this.pitch = pitch;
		this.press = press;
	}

	public byte getIndex() {
		return index;
	}

	public String getName() {
		return name;
	}

	public String getSoundfile() {
		return soundfile;
	}

	public Sound getSound() {
		return sound;
	}
}
