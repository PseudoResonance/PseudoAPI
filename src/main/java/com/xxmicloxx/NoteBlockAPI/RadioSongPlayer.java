package com.xxmicloxx.NoteBlockAPI;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import io.github.wolfleader116.wolfapi.bukkit.WolfAPI;

public class RadioSongPlayer extends SongPlayer {

	public RadioSongPlayer(Song song) {
		super(song);
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
					p.playSound(getLocation(p.getEyeLocation()), song.getCustomInstruments()[note.getInstrument() - Instrument.getCustomInstrumentFirstIndex()].getSound(), (l.getVolume() * (int) volume * (int) playerVolume) / 1000000f, NotePitch.getPitch(note.getKey() - 33));
				} else {
					p.playSound(getLocation(p.getEyeLocation()), song.getCustomInstruments()[note.getInstrument() - Instrument.getCustomInstrumentFirstIndex()].getSoundfile(), (l.getVolume() * (int) volume * (int) playerVolume) / 1000000f, NotePitch.getPitch(note.getKey() - 33));
				}

			} else {
				p.playSound(getLocation(p.getEyeLocation()), Instrument.getInstrument(note.getInstrument()), (l.getVolume() * (int) volume * (int) playerVolume) / 1000000f, NotePitch.getPitch(note.getKey() - 33));
			}
		}
	}

	private Location getLocation(Location l) {
		Location ret = l;
		ret.add(l.getDirection());
		return ret;
	}
}
