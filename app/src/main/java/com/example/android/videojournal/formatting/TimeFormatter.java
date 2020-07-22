package com.example.android.videojournal.formatting;

import static com.example.android.videojournal.utilities.Constants.MINUTES_IN_HOUR;

/**
 * Helps convert between hours, minutes, and format time for displaying
 */
public class TimeFormatter {

    /**
     * Formats time to be displayed in Reminder Time summary
     * text in Settings.
     * Formats to 12-hour AM-PM format
     * @param minutesAfterMidnight total time after midnight in minutes
     * @return String for summary to be displayed showing time in 12hr AM-PM format
     */
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
