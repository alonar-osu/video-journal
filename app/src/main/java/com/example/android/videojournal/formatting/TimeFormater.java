package com.example.android.videojournal.formatting;

public class TimeFormater {

    private static final int MINUTES_IN_HOUR = 60;

    public static String formatTime(int minutesAfterMidnight) {

        int hours = findHoursFromTotalMinutes(minutesAfterMidnight);
        int minutes = findMinutesFromTotalMinutes(minutesAfterMidnight);

        String summary = "";
        String minutesStr = "";
        if (minutes < 10) {
            minutesStr += "0";
            minutesStr += minutes;
        } else minutesStr = String.valueOf(minutes);

        if (hours >= 12) {
            if (hours >= 13) summary += hours - 12;
            else summary += hours;
            summary += " : " + minutesStr + " PM";
        } else {
            if (hours == 0) summary += hours + 12;
            else summary += hours;
            summary += " : " + minutesStr + " AM";
        }
        return summary;
    }

    public static int findHoursFromTotalMinutes(int totalMinutes) {
        return totalMinutes / MINUTES_IN_HOUR;
    }

    public static int findMinutesFromTotalMinutes(int totalMinutes) {
        return totalMinutes % MINUTES_IN_HOUR;
    }

    public static int findTotalMinutesFromHoursAndMins(int hours, int minutes) {
        return (hours * MINUTES_IN_HOUR) + minutes;
    }

}
