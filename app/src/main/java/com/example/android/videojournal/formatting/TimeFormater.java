package com.example.android.videojournal.formatting;

public class TimeFormater {

    public static String formatTime(int hours, int minutes) {
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

}
