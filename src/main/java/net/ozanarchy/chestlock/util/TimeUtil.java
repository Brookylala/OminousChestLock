package net.ozanarchy.chestlock.util;

public class TimeUtil {

    public static String formatDuration(long ms) {
        if (ms < 0) {
            return "unknown";
        }
        long seconds = ms / 1000L;
        long minutes = seconds / 60L;
        long hours = minutes / 60L;
        if (hours > 0) {
            long remMinutes = minutes % 60L;
            return hours + "h" + (remMinutes > 0 ? " " + remMinutes + "m" : "");
        }
        if (minutes > 0) {
            long remSeconds = seconds % 60L;
            return minutes + "m" + (remSeconds > 0 ? " " + remSeconds + "s" : "");
        }
        return seconds + "s";
    }
}
