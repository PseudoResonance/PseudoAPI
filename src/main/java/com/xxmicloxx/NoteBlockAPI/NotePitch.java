package com.xxmicloxx.NoteBlockAPI;

import io.github.wolfleader116.wolfapi.bukkit.WolfAPI;
import io.github.wolfleader116.wolfapi.bukkit.WolfAPI.NoteBlockCompatibility;

public enum NotePitch {

	A0(0, 0.6F, 0.59461F, 1),
	Bb0(1, 0.63F, 0.62995F, 1),
	B0(2, 0.67F, 0.66741F, 1),
	C1(3, 0.7F, 0.70711F, 1),
	Db1(4, 0.76F, 0.74916F, 1),
	D1(5, 0.8F, 0.79370F, 1),
	Eb1(6, 0.84F, 0.84089F, 1),
	E1(7, 0.9F, 0.89091F, 1),
	F1(8, 0.94F, 0.94386F, 1),
	Gb1(9, 1.0F, 1.00000F, 1),
	G1(10, 1.06F, 1.05945F, 1),
	Ab1(11, 1.12F, 1.12245F, 1),
	A1(12, 0.6F, 0.59461F, 1),
	Bb1(13, 1.26F, 1.25993F, 1),
	B1(14, 1.34F, 1.33484F, 1),
	C2(15, 0.7F, 0.70711F, 2),
	Db2(16, 0.76F, 0.74916F, 2),
	D2(17, 0.8F, 0.79370F, 2),
	Eb2(18, 0.84F, 0.84089F, 2),
	E2(19, 0.9F, 0.89091F, 2),
	F2(20, 0.94F, 0.94386F, 2),
	Gb2(21, 1.0F, 1.00000F, 2),
	G2(22, 1.06F, 1.05945F, 2),
	Ab2(23, 1.12F, 1.12245F, 2),
	A2(24, 0.6F, 0.59461F, 2),
	Bb2(25, 1.26F, 1.25993F, 2),
	B2(26, 1.34F, 1.33484F, 2),
	C3(27, 0.7F, 0.70711F, 3),
	Db3(28, 0.76F, 0.74916F, 3),
	D3(29, 0.8F, 0.79370F, 3),
	Eb3(30, 0.84F, 0.84089F, 3),
	E3(31, 0.9F, 0.89091F, 3),
	F3(32, 0.94F, 0.94386F, 3),
	Gb3(33, 1.0F, 1.00000F, 3),
	G3(34, 1.06F, 1.05945F, 3),
	Ab3(35, 1.12F, 1.12245F, 3),
	A3(36, 0.6F, 0.59461F, 3),
	Bb3(37, 1.26F, 1.25993F, 3),
	B3(38, 1.34F, 1.33484F, 3),
	C4(39, 0.7F, 0.70711F, 4),
	Db4(40, 0.76F, 0.74916F, 4),
	D4(41, 0.8F, 0.79370F, 4),
	Eb4(42, 0.84F, 0.84089F, 4),
	E4(43, 0.9F, 0.89091F, 4),
	F4(44, 0.94F, 0.94386F, 4),
	Gb4(45, 1.0F, 1.00000F, 4),
	G4(46, 1.06F, 1.05945F, 4),
	Ab4(47, 1.12F, 1.12245F, 4),
	A4(48, 1.18F, 1.18920F, 4),
	Bb4(49, 1.26F, 1.25993F, 4),
	B4(50, 1.34F, 1.33484F, 4),
	C5(51, 0.7F, 0.70711F, 5),
	Db5(52, 0.76F, 0.74916F, 5),
	D5(53, 0.8F, 0.79370F, 5),
	Eb5(54, 0.84F, 0.84089F, 5),
	E5(55, 0.9F, 0.89091F, 5),
	F5(56, 0.94F, 0.94386F, 5),
	Gb5(57, 1.0F, 1.00000F, 5),
	G5(58, 1.06F, 1.05945F, 5),
	Ab5(59, 1.12F, 1.12245F, 5),
	A5(60, 1.18F, 1.18920F, 5),
	Bb5(61, 1.26F, 1.25993F, 5),
	B5(62, 1.34F, 1.33484F, 5),
	C6(63, 0.7F, 0.70711F, 6),
	Db6(64, 0.76F, 0.74916F, 6),
	D6(65, 0.8F, 0.79370F, 6),
	Eb6(66, 0.84F, 0.84089F, 6),
	E6(67, 0.9F, 0.89091F, 6),
	F6(68, 0.94F, 0.94386F, 6),
	Gb6(69, 1.0F, 1.00000F, 6),
	G6(70, 1.12F, 1.12245F, 6),
	Ab6(71, 1.18F, 1.18920F, 6),
	A6(72, 1.26F, 1.25993F, 6),
	Bb6(73, 1.34F, 1.33484F, 6),
	B6(74, 1.34F, 1.33484F, 6),
	C7(75, 0.7F, 0.70711F, 7),
	Db7(76, 0.76F, 0.74916F, 7),
	D7(77, 0.8F, 0.79370F, 7),
	Eb7(78, 0.84F, 0.84089F, 7),
	E7(79, 0.9F, 0.89091F, 7),
	F7(80, 0.94F, 0.94386F, 7),
	Gb7(81, 1.0F, 1.00000F, 7),
	G7(82, 1.12F, 1.12245F, 7),
	Ab7(83, 1.12F, 1.12245F, 7),
	A7(84, 0.6F, 0.59461F, 7),
	Bb7(85, 1.26F, 1.25993F, 7),
	B7(86, 1.34F, 1.33484F, 7),
	C8(87, 1.42F, 1.41420F, 7);

	public int note;
	public float pitchPre1_9;
	public float pitchPost1_9;
	public int file;

	private NotePitch(int note, float pitchPre1_9, float pitchPost1_9, int file) {
		this.note = note;
		this.pitchPre1_9 = pitchPre1_9;
		this.pitchPost1_9 = pitchPost1_9;
		this.file = file;
	}

	public static float getPitch(int note) {
		for (NotePitch notePitch : values()) {
			if (notePitch.note == note) {
				return WolfAPI.getCompatibility() == NoteBlockCompatibility.pre1_9 ? notePitch.pitchPre1_9 : notePitch.pitchPost1_9;
			}
		}

		return 0.0F;
	}

	public static int getFile(int note) {
		for (NotePitch notePitch : values()) {
			if (notePitch.note == note) {
				return notePitch.file;
			}
		}

		return 4;
	}
}