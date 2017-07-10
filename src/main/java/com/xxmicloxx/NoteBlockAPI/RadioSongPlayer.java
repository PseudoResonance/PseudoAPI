package com.xxmicloxx.NoteBlockAPI;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import io.github.wolfleader116.wolfapi.bukkit.WolfAPI;

public class RadioSongPlayer extends SongPlayer {

	public RadioSongPlayer(Song song) {
		super(song);
	}

	public RadioSongPlayer(Song song, boolean resourcePack) {
		super(song, resourcePack);
	}

	@Override
	public void playTick(Player p, int tick) {
		byte playerVolume = WolfAPI.getPlayerVolume(p);

		for (Layer l : song.getLayerHashMap().values()) {
			Note note = l.getNote(tick);
			if (note == null) {
				continue;
			}
			if (Instrument.isCustomInstrument(note.getInstrument())) {
				if (song.getCustomInstruments()[note.getInstrument() - Instrument.getCustomInstrumentFirstIndex()].getSound() != null) {
					p.playSound(getLocation(p.getEyeLocation()), song.getCustomInstruments()[note.getInstrument() - Instrument.getCustomInstrumentFirstIndex()].getSound(), (l.getVolume() * (int) volume * (int) playerVolume) / 1000000f, NotePitch.getPitch(note.getKey()));
				} else {
					p.playSound(getLocation(p.getEyeLocation()), song.getCustomInstruments()[note.getInstrument() - Instrument.getCustomInstrumentFirstIndex()].getSoundfile(), (l.getVolume() * (int) volume * (int) playerVolume) / 1000000f, NotePitch.getPitch(note.getKey()));
				}

			} else {
				if (resourcePack) {
					p.playSound(getLocation(p.getEyeLocation()), "gb" + NotePitch.getFile(note.getKey()), (l.getVolume() * (int) volume * (int) playerVolume) / 1000000f, NotePitch.getPitch(note.getKey()));
				} else {
					p.playSound(getLocation(p.getEyeLocation()), Instrument.getInstrument(note.getInstrument()), (l.getVolume() * (int) volume * (int) playerVolume) / 1000000f, NotePitch.getPitch(note.getKey()));
				}
			}
		}
	}

	private Location getLocation(Location l) {
		Location ret = l;
		ret.add(l.getDirection());
		return ret;
	}
}
