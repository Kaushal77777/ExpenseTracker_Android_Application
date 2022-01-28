package com.dduproject.expensetracker.utils;

import java.util.Calendar;
import java.util.Date;

public class CalendarHelper {

    public static Calendar getUserPeriodStartDate() {
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        if (new Date().getTime() < cal.getTime().getTime()) {
            cal.add(Calendar.MONTH, -1);
        }
        return cal;
    }


    public static Calendar getUserPeriodEndDate() {
        Calendar cal = getUserPeriodStartDate();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.clear(Calendar.MILLISECOND);
        cal.add(Calendar.MONTH, 1);
        cal.add(Calendar.DATE, -1);
        return cal;
    }
}
