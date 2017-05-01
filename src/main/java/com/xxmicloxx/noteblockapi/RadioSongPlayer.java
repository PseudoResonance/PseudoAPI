package com.xxmicloxx.noteblockapi;

import org.bukkit.entity.Player;

import io.github.wolfleader116.wolfapi.bukkit.Message;
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
            if (Instrument.getBukkitInstrument(note.getInstrument()) == org.bukkit.Instrument.PIANO) {
            	if (p.getName().equalsIgnoreCase("WolfLeader116")) {
                	Message.sendConsoleMessage("PLAYED SOUND: " + NotePitch.getNote(note.getKey()) + ":" + note.getKey());
            	}
                p.playSound(p.getEyeLocation(), "p" + NotePitch.getNote(note.getKey()), (l.getVolume() * (int) volume * (int) playerVolume) / 1000000f, 1f);
            } else {
            	p.playSound(p.getEyeLocation(), Instrument.getInstrument(note.getInstrument()), (l.getVolume() * (int) volume * (int) playerVolume) / 1000000f, NotePitch.getPitch(note.getKey()));
            }
        }
    }
}