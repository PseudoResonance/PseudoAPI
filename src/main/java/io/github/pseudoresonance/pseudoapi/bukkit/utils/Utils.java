package io.github.pseudoresonance.pseudoapi.bukkit.utils;

import org.bukkit.Bukkit;

public class Utils {

	private static String bukkitVersion;
	
	/**
	 * Returns the current Bukkit version for use in reflection or ASM methods
	 * 
	 * @return Current Bukkit version
	 */
	public static String getBukkitVersion() {
		if (bukkitVersion == null)
			bukkitVersion = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
		return bukkitVersion;
	}

	public static String bytesToHumanFormat(long bytes, boolean si) {
		int unit = si ? 1000 : 1024;
		if (bytes < unit)
			return bytes + " B";
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}

}
