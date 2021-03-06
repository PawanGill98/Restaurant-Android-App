package me.cmpt276.restaurantinspector.Model;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *  implements methods related to time computations
 */

public class Time {
    private static final int NUM_MILLISECONDS_IN_DAY = 86400000;
    private static final int MONTH_START_INDEX = 4;
    private static final int MONTH_END_INDEX = 6;
    private static final int DAY_START_INDEX = 6;
    private static final int DAY_END_INDEX = 8;
    private static final int YEAR_START_INDEX = 0;
    private static final int YEAR_END_INDEX = 4;


    public long calculateDaysSince(String dateString) {
        Date today = new Date();
        Calendar dateCalendar = Calendar.getInstance();
        int year = Integer.parseInt(dateString.substring(YEAR_START_INDEX, YEAR_END_INDEX));
        int month = Integer.parseInt(dateString.substring(MONTH_START_INDEX, MONTH_END_INDEX));
        int day = Integer.parseInt(dateString.substring(DAY_START_INDEX, DAY_END_INDEX));
        dateCalendar.set(year, month - 1, day);
        Date date = dateCalendar.getTime();
        return (today.getTime() - date.getTime()) / NUM_MILLISECONDS_IN_DAY;
    }

    public static String getMonthFromNumber(int monthNumber) {
        if (monthNumber < 1 || monthNumber > 12) {
            return null;
        }
        String monthName;
        DateFormatSymbols dateFormat = new DateFormatSymbols();
        String[] months = dateFormat.getMonths();
        monthName = months[monthNumber-1];
        return monthName;
    }

    public static int calculateHourDifference(String date1, String date2) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        Date d1 = null;
        Date d2 = null;
        try {
            d1 = format.parse(date1);
            d2 = format.parse(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long diff = d2.getTime() - d1.getTime();

        long diffHours = diff / (60 * 60 * 1000);
        int hours = Math.abs((int) diffHours);
        return hours;
    }
}
