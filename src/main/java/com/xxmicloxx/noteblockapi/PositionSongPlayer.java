package com.xxmicloxx.noteblockapi;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import io.github.wolfleader116.wolfapi.bukkit.WolfAPI;

public class PositionSongPlayer extends SongPlayer {

    private Location targetLocation;

    public PositionSongPlayer(Song song) {
        super(song);
    }

    public Location getTargetLocation() {
        return targetLocation;
    }

    public void setTargetLocation(Location targetLocation) {
        this.targetLocation = targetLocation;
    }

    @Override
    public void playTick(Player p, int tick) {
        if (!p.getWorld().getName().equals(targetLocation.getWorld().getName())) {
            // not in same world
            return;
        }
        byte playerVolume = WolfAPI.getPlayerVolume(p);

        for (Layer l : song.getLayerHashMap().values()) {
            Note note = l.getNote(tick);
            if (note == null) {
                continue;
            }
            if (Instrument.getBukkitInstrument(note.getInstrument()) == org.bukkit.Instrument.PIANO) {
                p.playSound(targetLocation, "p" + NotePitch.getNote(note.getKey()), (l.getVolume() * (int) volume * (int) playerVolume) / 1000000f, 1f);
            } else {
            	p.playSound(targetLocation, Instrument.getInstrument(note.getInstrument()), (l.getVolume() * (int) volume * (int) playerVolume) / 1000000f, NotePitch.getPitch(note.getKey()));
            }
        }
    }
}
