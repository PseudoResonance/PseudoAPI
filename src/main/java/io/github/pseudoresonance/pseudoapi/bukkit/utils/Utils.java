package io.github.pseudoresonance.pseudoapi.bukkit.utils;

import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;

public class Utils {

	private static String bukkitVersion;
	
	public static String getBukkitVersion() {
		if (bukkitVersion == null)
			bukkitVersion = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
		return bukkitVersion;
	}

	@Deprecated
	public static String millisToHumanFormat(long millis) {
		if (millis < 0) {
			throw new IllegalArgumentException("Duration must be greater than zero!");
		}

		long days = TimeUnit.MILLISECONDS.toDays(millis);
		millis -= TimeUnit.DAYS.toMillis(days);
		long hours = TimeUnit.MILLISECONDS.toHours(millis);
		millis -= TimeUnit.HOURS.toMillis(hours);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
		millis -= TimeUnit.MINUTES.toMillis(minutes);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

		String day = days + " Day";
		if (days > 1 || days == 0) {
			day += "s";
		}
		String hour = hours + " Hour";
		if (hours > 1 || hours == 0) {
			hour += "s";
		}
		String minute = minutes + " Minute";
		if (minutes > 1 || minutes == 0) {
			minute += "s";
		}
		String second = seconds + " Second";
		if (seconds > 1 || seconds == 0) {
			second += "s";
		}

		String s = "";
		if (days > 0)
			s = day + " " + hour + " " + minute + " " + second;
		else if (hours > 0)
			s = hour + " " + minute + " " + second;
		else if (minutes > 0)
			s = minute + " " + second;
		else
			s = second;
		return s;
	}

}
