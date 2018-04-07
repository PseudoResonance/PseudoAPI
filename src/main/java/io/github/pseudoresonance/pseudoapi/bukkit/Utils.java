package io.github.pseudoresonance.pseudoapi.bukkit;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

public class Utils {

	private static long lastPoll = System.nanoTime();
	private final static LinkedList<Double> history = new LinkedList<Double>();
	private static long tickInterval = 20;

	private static int taskID;

	private static final DecimalFormat df = new DecimalFormat("#.##");
	
	private static HashMap<String, String> brands = new HashMap<String, String>();
	
	public static void playerLogin(String name, String brand) {
		brands.put(name.toLowerCase(), brand);
	}
	
	public static void playerLogout(String name) {
		brands.remove(name.toLowerCase());
	}
	
	public static String getBrand(String name) {
		return brands.get(name.toLowerCase());
	}

	public static void startTps() {
		df.setRoundingMode(RoundingMode.HALF_UP);
		tickInterval = ConfigOptions.tpsUpdateFrequency;
		taskID = PseudoAPI.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(PseudoAPI.plugin, new Runnable() {
			@Override
			public void run() {
				final long startTime = System.nanoTime();
				long timeSpent = (startTime - lastPoll);
				if (timeSpent == 0) {
					timeSpent = 1;
				}
				double tps = tickInterval * 1000000000.0 / timeSpent;
				if (tps <= 21) {
					history.add(tps);
				}
				if (history.size() > 10) {
					history.remove();
				}
				lastPoll = startTime;
			}
		}, tickInterval, tickInterval);
	}

	public static void stopTps() {
		PseudoAPI.plugin.getServer().getScheduler().cancelTask(taskID);
	}

	public static double getTps() {
		if (history.size() > 0) {
			double avg = 0;
			for (Double d : history) {
				if (d != null) {
					avg += d;
				}
			}
			double round = avg / history.size();
			double end = Double.valueOf(df.format(round));
			if (end > 20)
				end = Double.valueOf(df.format(20d));
			return end;
		} else {
			return Double.valueOf(df.format(20d));
		}
	}

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
