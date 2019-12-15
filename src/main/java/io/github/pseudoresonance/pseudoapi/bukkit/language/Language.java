package io.github.pseudoresonance.pseudoapi.bukkit.language;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Pattern;

public class Language {
	
	private final String lang;
	private final HashMap<String, String> languageMap = new HashMap<String, String>();
	
	private Pattern dateFormatPattern = Pattern.compile("\\{\\$date\\.date\\$\\}");
	private Pattern dateTimeFormatPattern = Pattern.compile("\\{\\$date\\.dateTime\\$\\}");
	private Pattern timeFormatPattern = Pattern.compile("\\{\\$date\\.time\\$\\}");
	
	private Locale locale = null;

	private DateTimeFormatter dateFormat = null;
	private DateTimeFormatter dateTimeFormat = null;
	private DateTimeFormatter timeFormat = null;
	
	private Pattern relativeFormatPattern = Pattern.compile("\\{\\$1\\$\\}");
	
	private boolean relativeFormatAscending = false;
	private String relativeNanoseconds = "";
	private String relativeMilliseconds = "";
	private String relativeSeconds = "";
	private String relativeMinutes = "";
	private String relativeHours = "";
	private String relativeDays = "";
	private String relativeMonths = "";
	private String relativeYears = "";
	
	public Language(String lang) {
		this.lang = lang;
	}
	
	public String getName() {
		return lang;
	}
	
	public String getUnprocessedMessage(String key) {
		return languageMap.get(key);
	}
	
	public String getMessage(String key, Object... args) {
		String msg = languageMap.get(key);
		if (msg == null) {
			msg = LanguageManager.getLanguage().getUnprocessedMessage(key);
		}
		if (msg != null) {
			for (int i = 0; i < args.length; i++) {
				msg = Pattern.compile("\\{\\$" + (i + 1) + "\\$\\}").matcher(msg).replaceFirst(args[i].toString());
			}
			msg = dateFormatPattern.matcher(msg).replaceAll(languageMap.get("date.dateFormatHumanReadable"));
			msg = dateTimeFormatPattern.matcher(msg).replaceAll(languageMap.get("date.dateTimeFormatHumanReadable"));
			msg = timeFormatPattern.matcher(msg).replaceAll(languageMap.get("date.timeFormatHumanReadable"));
			return msg;
		}
		return "Error: Localization for " + key + " is missing!";
	}
	
	public String formatDate(LocalDate date) {
		return dateFormat.format(date);
	}
	
	public String formatDate(Date date) {
		return formatDate(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
	}
	
	public String formatDateTime(LocalDateTime dateTime) {
		return dateTimeFormat.format(dateTime);
	}
	
	public String formatDateTime(Date dateTime) {
		return formatDateTime(dateTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
	}
	
	public String formatTime(LocalTime time) {
		return timeFormat.format(time);
	}
	
	public String formatTime(Date time) {
		return formatTime(time.toInstant().atZone(ZoneId.systemDefault()).toLocalTime());
	}
	
	public String formatTimeAgo(LocalDateTime dateTime, ChronoUnit minUnit, ChronoUnit maxUnit) {
		if (relativeFormatAscending)
			return timeAgoAscending(dateTime, minUnit, maxUnit);
		else
			return timeAgoDescending(dateTime, minUnit, maxUnit);
	}
	
	public String formatTimeAgo(Date dateTime, ChronoUnit minUnit, ChronoUnit maxUnit) {
		return formatTimeAgo(dateTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(), minUnit, maxUnit);
	}
	
	public String formatTimeAgo(LocalDateTime dateTime) {
		if (relativeFormatAscending)
			return timeAgoAscending(dateTime);
		else
			return timeAgoDescending(dateTime);
	}
	
	public String formatTimeAgo(Date dateTime) {
		return formatTimeAgo(dateTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
	}
	
	private String timeAgoAscending(LocalDateTime dateTime, ChronoUnit minUnit, ChronoUnit maxUnit) {
		LocalDateTime temp = LocalDateTime.from(dateTime);
		LocalDateTime now = LocalDateTime.now();
		long years = temp.until(now, ChronoUnit.YEARS);
		temp = temp.plusYears(years);
		long months = temp.until(now, ChronoUnit.MONTHS);
		temp = temp.plusMonths(months);
		long days = temp.until(now, ChronoUnit.DAYS);
		temp = temp.plusDays(days);
		long hours = temp.until(now, ChronoUnit.HOURS);
		temp = temp.plusHours(hours);
		long minutes = temp.until(now, ChronoUnit.MINUTES);
		temp = temp.plusMinutes(minutes);
		long seconds = temp.until(now, ChronoUnit.SECONDS);
		temp = temp.plusSeconds(seconds);
		long milliseconds = temp.until(now, ChronoUnit.MILLIS);
		temp = temp.plus(milliseconds, ChronoUnit.MILLIS);
		long nanoseconds = temp.until(now, ChronoUnit.NANOS);
		String sYears = "";
		String sMonths = "";
		String sDays = "";
		String sHours = "";
		String sMinutes = "";
		String sSeconds = "";
		String sMilliseconds = "";
		String sNanoseconds = "";
		if (relativeYears.length() > 0 && isUnitInRange(minUnit, ChronoUnit.YEARS, maxUnit) && (years > 0 || minUnit == ChronoUnit.YEARS))
			sYears = relativeFormatPattern.matcher(relativeYears).replaceFirst(String.valueOf(years)) + " ";
		else
			months += years * 12;
		if (relativeMonths.length() > 0 && isUnitInRange(minUnit, ChronoUnit.MONTHS, maxUnit) && (months > 0 || minUnit == ChronoUnit.MONTHS))
			sMonths = relativeFormatPattern.matcher(relativeMonths).replaceFirst(String.valueOf(months)) + " ";
		else
			days += (long) (months * 30.436875);
		if (relativeDays.length() > 0 && isUnitInRange(minUnit, ChronoUnit.DAYS, maxUnit) && (days > 0 || minUnit == ChronoUnit.DAYS))
			sDays = relativeFormatPattern.matcher(relativeDays).replaceFirst(String.valueOf(days)) + " ";
		else
			hours += days * 24;
		if (relativeHours.length() > 0 && isUnitInRange(minUnit, ChronoUnit.HOURS, maxUnit) && (hours > 0 || minUnit == ChronoUnit.HOURS))
			sHours = relativeFormatPattern.matcher(relativeHours).replaceFirst(String.valueOf(hours)) + " ";
		else
			minutes += hours * 60;
		if (relativeMinutes.length() > 0 && isUnitInRange(minUnit, ChronoUnit.MINUTES, maxUnit) && (minutes > 0 || minUnit == ChronoUnit.MINUTES))
			sMinutes = relativeFormatPattern.matcher(relativeMinutes).replaceFirst(String.valueOf(minutes)) + " ";
		else
			seconds += minutes * 60;
		if (relativeSeconds.length() > 0 && isUnitInRange(minUnit, ChronoUnit.SECONDS, maxUnit) && (seconds > 0 || minUnit == ChronoUnit.SECONDS))
			sSeconds = relativeFormatPattern.matcher(relativeSeconds).replaceFirst(String.valueOf(seconds)) + " ";
		else
			milliseconds += seconds * 1000;
		if (relativeMilliseconds.length() > 0 && isUnitInRange(minUnit, ChronoUnit.MILLIS, maxUnit) && (milliseconds > 0 || minUnit == ChronoUnit.MILLIS))
			sMilliseconds = relativeFormatPattern.matcher(relativeMilliseconds).replaceFirst(String.valueOf(milliseconds)) + " ";
		else
			nanoseconds += milliseconds * 1000000;
		if (relativeNanoseconds.length() > 0 && isUnitInRange(minUnit, ChronoUnit.NANOS, maxUnit) && (nanoseconds > 0 || minUnit == ChronoUnit.NANOS))
			sNanoseconds = relativeFormatPattern.matcher(relativeNanoseconds).replaceFirst(String.valueOf(nanoseconds)) + " ";
		String ret = sNanoseconds + sMilliseconds + sSeconds + sMinutes + sHours + sDays + sMonths + sYears;
		return getMessage("date.relativeAgo", ret.substring(0, ret.length() - 1));
	}
	
	private String timeAgoAscending(LocalDateTime dateTime) {
		return timeAgoAscending(dateTime, ChronoUnit.SECONDS, ChronoUnit.YEARS);
	}
	
	private String timeAgoDescending(LocalDateTime dateTime, ChronoUnit minUnit, ChronoUnit maxUnit) {
		LocalDateTime temp = LocalDateTime.from(dateTime);
		LocalDateTime now = LocalDateTime.now();
		long years = temp.until(now, ChronoUnit.YEARS);
		temp = temp.plusYears(years);
		long months = temp.until(now, ChronoUnit.MONTHS);
		temp = temp.plusMonths(months);
		long days = temp.until(now, ChronoUnit.DAYS);
		temp = temp.plusDays(days);
		long hours = temp.until(now, ChronoUnit.HOURS);
		temp = temp.plusHours(hours);
		long minutes = temp.until(now, ChronoUnit.MINUTES);
		temp = temp.plusMinutes(minutes);
		long seconds = temp.until(now, ChronoUnit.SECONDS);
		temp = temp.plusSeconds(seconds);
		long milliseconds = temp.until(now, ChronoUnit.MILLIS);
		temp = temp.plus(milliseconds, ChronoUnit.MILLIS);
		long nanoseconds = temp.until(now, ChronoUnit.NANOS);
		String sYears = "";
		String sMonths = "";
		String sDays = "";
		String sHours = "";
		String sMinutes = "";
		String sSeconds = "";
		String sMilliseconds = "";
		String sNanoseconds = "";
		if (relativeYears.length() > 0 && isUnitInRange(minUnit, ChronoUnit.YEARS, maxUnit) && (years > 0 || minUnit == ChronoUnit.YEARS))
			sYears = relativeFormatPattern.matcher(relativeYears).replaceFirst(String.valueOf(years)) + " ";
		else
			months += years * 12;
		if (relativeMonths.length() > 0 && isUnitInRange(minUnit, ChronoUnit.MONTHS, maxUnit) && (months > 0 || minUnit == ChronoUnit.MONTHS))
			sMonths = relativeFormatPattern.matcher(relativeMonths).replaceFirst(String.valueOf(months)) + " ";
		else
			days += (long) (months * 30.436875);
		if (relativeDays.length() > 0 && isUnitInRange(minUnit, ChronoUnit.DAYS, maxUnit) && (days > 0 || minUnit == ChronoUnit.DAYS))
			sDays = relativeFormatPattern.matcher(relativeDays).replaceFirst(String.valueOf(days)) + " ";
		else
			hours += days * 24;
		if (relativeHours.length() > 0 && isUnitInRange(minUnit, ChronoUnit.HOURS, maxUnit) && (hours > 0 || minUnit == ChronoUnit.HOURS))
			sHours = relativeFormatPattern.matcher(relativeHours).replaceFirst(String.valueOf(hours)) + " ";
		else
			minutes += hours * 60;
		if (relativeMinutes.length() > 0 && isUnitInRange(minUnit, ChronoUnit.MINUTES, maxUnit) && (minutes > 0 || minUnit == ChronoUnit.MINUTES))
			sMinutes = relativeFormatPattern.matcher(relativeMinutes).replaceFirst(String.valueOf(minutes)) + " ";
		else
			seconds += minutes * 60;
		if (relativeSeconds.length() > 0 && isUnitInRange(minUnit, ChronoUnit.SECONDS, maxUnit) && (seconds > 0 || minUnit == ChronoUnit.SECONDS))
			sSeconds = relativeFormatPattern.matcher(relativeSeconds).replaceFirst(String.valueOf(seconds)) + " ";
		else
			milliseconds += seconds * 1000;
		if (relativeMilliseconds.length() > 0 && isUnitInRange(minUnit, ChronoUnit.MILLIS, maxUnit) && (milliseconds > 0 || minUnit == ChronoUnit.MILLIS))
			sMilliseconds = relativeFormatPattern.matcher(relativeMilliseconds).replaceFirst(String.valueOf(milliseconds)) + " ";
		else
			nanoseconds += milliseconds * 1000000;
		if (relativeNanoseconds.length() > 0 && isUnitInRange(minUnit, ChronoUnit.NANOS, maxUnit) && (nanoseconds > 0 || minUnit == ChronoUnit.NANOS))
			sNanoseconds = relativeFormatPattern.matcher(relativeNanoseconds).replaceFirst(String.valueOf(nanoseconds)) + " ";
		String ret = sYears + sMonths + sDays + sHours + sMinutes + sSeconds + sMilliseconds + sNanoseconds;
		return getMessage("date.relativeAgo", ret.substring(0, ret.length() - 1));
	}
	
	private String timeAgoDescending(LocalDateTime dateTime) {
		return timeAgoDescending(dateTime, ChronoUnit.SECONDS, ChronoUnit.YEARS);
	}
	
	private boolean isUnitInRange(ChronoUnit min, ChronoUnit test, ChronoUnit max) {
		if (test.compareTo(min) >= 0 && test.compareTo(max) <= 0)
			return true;
		return false;
	}
	
	public void addLanguageMap(HashMap<String, String> map) {
		languageMap.putAll(map);
		if (locale == null && languageMap.keySet().contains("date.locale")) {
			locale = Locale.forLanguageTag(languageMap.get("date.locale"));
		}
		if (locale != null && dateFormat == null && languageMap.keySet().contains("date.dateFormat")) {
			
			dateFormat = DateTimeFormatter.ofPattern(languageMap.get("date.dateFormat"), locale);
		}
		if (locale != null && dateTimeFormat == null && languageMap.keySet().contains("date.dateTimeFormat")) {
			dateTimeFormat = DateTimeFormatter.ofPattern(languageMap.get("date.dateTimeFormat"), locale);
		}
		if (locale != null && timeFormat == null && languageMap.keySet().contains("date.timeFormat")) {
			timeFormat = DateTimeFormatter.ofPattern(languageMap.get("date.timeFormat"), locale);
		}
		if (languageMap.keySet().contains("date.relativeNanoseconds")) {
			relativeNanoseconds = languageMap.get("date.relativeNanoseconds");
		}
		if (languageMap.keySet().contains("date.relativeMilliseconds")) {
			relativeMilliseconds = languageMap.get("date.relativeMilliseconds");
		}
		if (languageMap.keySet().contains("date.relativeSeconds")) {
			relativeSeconds = languageMap.get("date.relativeSeconds");
		}
		if (languageMap.keySet().contains("date.relativeMinutes")) {
			relativeMinutes = languageMap.get("date.relativeMinutes");
		}
		if (languageMap.keySet().contains("date.relativeHours")) {
			relativeHours = languageMap.get("date.relativeHours");
		}
		if (languageMap.keySet().contains("date.relativeDays")) {
			relativeDays = languageMap.get("date.relativeDays");
		}
		if (languageMap.keySet().contains("date.relativeMonths")) {
			relativeMonths = languageMap.get("date.relativeMonths");
		}
		if (languageMap.keySet().contains("date.relativeYears")) {
			relativeYears = languageMap.get("date.relativeYears");
		}
		if (languageMap.keySet().contains("date.relativeFormatAscending")) {
			relativeFormatAscending = languageMap.get("date.relativeFormatAscending").toLowerCase().startsWith("t") ? true : false;
		}
	}

}
