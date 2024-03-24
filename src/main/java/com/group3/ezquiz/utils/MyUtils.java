package com.group3.ezquiz.utils;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;

public class MyUtils {

    public static String calculateTimeElapsed(Timestamp creationTime) {
        Instant instant = creationTime.toInstant();
        Instant currentInstant = Instant.now();
        Duration duration = Duration.between(instant, currentInstant);

        long seconds = duration.getSeconds();

        long days = seconds / (24 * 3600);
        seconds = seconds % (24 * 3600);
        long hours = seconds / 3600;
        seconds %= 3600;
        long minutes = seconds / 60;
        seconds %= 60;

        if (days > 1) {
            return days + " days ago";
        } else if (days == 1) {
            return days + " day ago";
        }
        if (hours > 1) {
            return hours + " hours ago";
        } else if (hours == 1) {
            return hours + " hour ago";
        }
        if (minutes > 1) {
            return minutes + " minutes ago";
        } else if (minutes == 1) {
            return minutes + " minute ago";
        }
        if (seconds > 1) {
            return seconds + " seconds ago";
        }
        return seconds + " second ago";
    }
}
