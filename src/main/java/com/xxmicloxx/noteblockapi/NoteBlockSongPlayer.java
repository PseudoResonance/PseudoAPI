package com.xxmicloxx.noteblockapi;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import io.github.wolfleader116.wolfapi.bukkit.WolfAPI;

/**
 * Created with IntelliJ IDEA.
 * User: ml
 * Date: 07.12.13
 * Time: 12:56
 */
public class NoteBlockSongPlayer extends SongPlayer {
    private Block noteBlock;

    public NoteBlockSongPlayer(Song song) {
        super(song);
    }

    public Block getNoteBlock() {
        return noteBlock;
    }

    public void setNoteBlock(Block noteBlock) {
        this.noteBlock = noteBlock;
    }

    @Override
    public void playTick(Player p, int tick) {
        if (noteBlock.getType() != Material.NOTE_BLOCK) {
            return;
        }
        if (!p.getWorld().getName().equals(noteBlock.getWorld().getName())) {
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
                p.playSound(noteBlock.getLocation(), "p" + NotePitch.getNote(note.getKey()), (l.getVolume() * (int) volume * (int) playerVolume) / 1000000f, 1f);
            } else {
            	p.playSound(noteBlock.getLocation(), Instrument.getInstrument(note.getInstrument()), (l.getVolume() * (int) volume * (int) playerVolume) / 1000000f, NotePitch.getPitch(note.getKey()));
            }
        }
    }
}
