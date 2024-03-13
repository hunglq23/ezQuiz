package com.group3.ezquiz.utils;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Utility {

    public static Timestamp convertStringToTimestamp(String dateString, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime dateTime = LocalDateTime.parse(dateString, formatter);
        return Timestamp.valueOf(dateTime);
    }

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

        if (days > 0) {
            return days + " day(s) ago";
        }
        if (hours > 0) {
            return hours + " hour(s) ago";
        }
        if (minutes > 0) {
            return minutes + " minute(s) ago";
        }
        return seconds + " second(s) ago";
    }
}
