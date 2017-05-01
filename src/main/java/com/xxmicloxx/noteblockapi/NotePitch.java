package com.xxmicloxx.noteblockapi;

public enum NotePitch {

    A0((byte) 0),
    Bb0((byte) 1),
    B0((byte) 2),
    C1((byte) 3),
    Db1((byte) 4),
    D1((byte) 5),
    Eb1((byte) 6),
    E1((byte) 7),
    F1((byte) 8),
    Gb1((byte) 9),
    G1((byte) 10),
    Ab1((byte) 11),
    A1((byte) 12),
    Bb1((byte) 13),
    B1((byte) 14),
    C2((byte) 15),
    Db2((byte) 16),
    D2((byte) 17),
    Eb2((byte) 18),
    E2((byte) 19),
    F2((byte) 20),
    Gb2((byte) 21),
    G2((byte) 22),
    Ab2((byte) 23),
    A2((byte) 24),
    Bb2((byte) 25),
    B2((byte) 26),
    C3((byte) 27),
    Db3((byte) 28),
    D3((byte) 29),
    Eb3((byte) 30),
    E3((byte) 31),
    F3((byte) 32),
    Gb3((byte) 33, 0.5F),
    G3((byte) 34, 0.53F),
    Ab3((byte) 35, 0.56F),
    A3((byte) 36, 0.6F),
    Bb3((byte) 37, 0.63F),
    B3((byte) 38, 0.67F),
    C4((byte) 39, 0.7F),
    Db4((byte) 40, 0.76F),
    D4((byte) 41, 0.8F),
    Eb4((byte) 42, 0.84F),
    E4((byte) 43, 0.9F),
    F4((byte) 44, 0.94F),
    Gb4((byte) 45, 1.0F),
    G4((byte) 46, 1.06F),
    Ab4((byte) 47, 1.12F),
    A4((byte) 48, 1.18F),
    Bb4((byte) 49, 1.26F),
    B4((byte) 50, 1.34F),
    C5((byte) 51, 1.42F),
    Db5((byte) 52, 1.5F),
    D5((byte) 53, 1.6F),
    Eb5((byte) 54, 1.68F),
    E5((byte) 55, 1.78F),
    F5((byte) 56, 1.88F),
    Gb5((byte) 57, 2.0F),
    G5((byte) 58),
    Ab5((byte) 59),
    A5((byte) 60),
    Bb5((byte) 61),
    B5((byte) 62),
    C6((byte) 63),
    Db6((byte) 64),
    D6((byte) 65),
    Eb6((byte) 66),
    E6((byte) 67),
    F6((byte) 68),
    Gb6((byte) 69),
    G6((byte) 70),
    Ab6((byte) 71),
    A6((byte) 72),
    Bb6((byte) 73),
    B6((byte) 74),
    C7((byte) 75),
    Db7((byte) 76),
    D7((byte) 77),
    Eb7((byte) 78),
    E7((byte) 79),
    F7((byte) 80),
    Gb7((byte) 81),
    G7((byte) 82),
    Ab7((byte) 83),
    A7((byte) 84),
    Bb7((byte) 85),
    B7((byte) 86),
    C8((byte) 87);

    public byte note;
    public float pitch;

    private NotePitch(byte note, float pitch) {
        this.note = note;
        this.pitch = pitch;
    }
    
    private NotePitch(byte note) {
        this.note = note;
        this.pitch = 6f;
    }

    public static float getPitch(int note) {
        for (NotePitch notePitch : values()) {
            if (notePitch.note == note) {
                return notePitch.pitch;
            }
        }
        return 6f;
    }

    public static String getNote(int note) {
        for (NotePitch notePitch : values()) {
            if (notePitch.note == note) {
                return notePitch.toString().toLowerCase();
            }
        }
        return "c4";
    }
}