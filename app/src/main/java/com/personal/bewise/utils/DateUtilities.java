package com.personal.bewise.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateUtilities {

    /**
     * The constant DATE_FORMAT.
     */
    public static final String DATE_FORMAT_DD_MM_YYYY_SLASH = "dd/MM/yyyy";

    public static final String DATE_FORMAT_YYYY_MM_DD_DASH = "yyyy-MM-dd";

    public DateUtilities() {
        // TODO Auto-generated constructor stub
    }

    /**
     * Get current date as string.
     *
     * @return
     */
    public static String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_DD_MM_YYYY_SLASH,
                java.util.Locale.getDefault());
        return dateFormat.format(new Date());
    }

    /**
     * Get current date as time stamp in long.
     *
     * @return
     */
    public static long getCurrentDateStamp() {
        return getTimestampFromDate(getCurrentDate());
    }

    /**
     * @param date
     * @param calendarItem
     * @param itemsToAdd
     * @return
     * @throws ParseException
     */
    private static String calculateDate(String date, int calendarItem, int itemsToAdd) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_DD_MM_YYYY_SLASH,
                java.util.Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        String eval = "";
        try {
            Date referDate = dateFormat.parse(date);
            calendar.setTime(referDate);
            calendar.add(calendarItem, itemsToAdd);
            eval = dateFormat.format(calendar.getTime());
        } catch (ParseException pe) {
            eval = null;
        } finally {
            return eval;
        }
    }

    /**
     * @param date      Date to which days are added.
     * @param daysToAdd Days to add.
     * @return New date as String after days addition.
     * @throws ParseException If date format is incorrect it will throw a ParseException.
     */
    public static String addDays(String date, int daysToAdd) {
        return calculateDate(date, Calendar.DATE, daysToAdd);
    }

    /**
     * @param date        Date to which months are added.
     * @param monthsToAdd Months to add.
     * @return New date as String after months addition.
     * @throws ParseException If date format is incorrect it will throw a ParseException.
     */
    public static String addMonths(String date, int monthsToAdd) {
        return calculateDate(date, Calendar.MONTH, monthsToAdd);
    }

    /**
     * @param date       Date to which years are added.
     * @param yearsToAdd Months to add.
     * @return New date as String after years addition.
     * @throws ParseException If date format is incorrect it will throw a ParseException.
     */
    public static String addYears(String date, int yearsToAdd) {
        return calculateDate(date, Calendar.YEAR, yearsToAdd);
    }

    /**
     * @param date
     * @return 1 = Overdue. 0 = Due today. -1 = Not yet due.
     * @throws ParseException
     */
    public static int isAfterToday(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_DD_MM_YYYY_SLASH,
                java.util.Locale.getDefault());
        int eval = 0;
        try {
            Date referDate = dateFormat.parse(date);
            Date currentDate = dateFormat.parse(dateFormat.format(new Date()));
            eval = currentDate.compareTo(referDate);
        } catch (ParseException pe) {
            eval = 0;
        }
        return eval;
    }

    /**
     * @param date
     * @param periodType
     * @return
     * @throws ParseException
     */
    public static long overdueCount(String date, String periodType) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_DD_MM_YYYY_SLASH,
                java.util.Locale.getDefault());
        try {
            Date dueOnDate = dateFormat.parse(date);
            Date currentDate = dateFormat.parse(dateFormat.format(new Date()));
            long difference = currentDate.getTime() - dueOnDate.getTime();
            if (difference >= 0) {
                long days = TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS);
                if (periodType.equalsIgnoreCase(RecurrencePeriod.DAY.toString())) {
                    return days;
                } else if (periodType.equalsIgnoreCase(RecurrencePeriod.WEEK.toString())) {
                    return days / RecurrencePeriod.WEEK.getValue();
                } else if (periodType.equalsIgnoreCase(RecurrencePeriod.MONTH.toString())) {
                    return days / RecurrencePeriod.MONTH.getValue();
                } else if (periodType.equalsIgnoreCase(RecurrencePeriod.QUARTER.toString())) {
                    return days / RecurrencePeriod.QUARTER.getValue();
                } else if (periodType.equalsIgnoreCase(RecurrencePeriod.HALF_YEAR.toString())) {
                    return days / RecurrencePeriod.HALF_YEAR.getValue();
                } else if (periodType.equalsIgnoreCase(RecurrencePeriod.YEAR.toString())) {
                    return days / RecurrencePeriod.YEAR.getValue();
                } else {
                    return 0;
                }
            }
        } catch (ParseException pe) {

        }
        return 0;
    }

    public static String getNextDueDate(String date, int recurrencePeriod) {
        if (recurrencePeriod == RecurrencePeriod.DAY.getValue()) {
            return DateUtilities.addDays(date, 1);
        } else if (recurrencePeriod == RecurrencePeriod.WEEK.getValue()) {
            return DateUtilities.addDays(date, 7);
        } else if (recurrencePeriod == RecurrencePeriod.MONTH.getValue()) {
            return DateUtilities.addMonths(date, 1);
        } else if (recurrencePeriod == RecurrencePeriod.QUARTER.getValue()) {
            return DateUtilities.addMonths(date, 3);
        } else if (recurrencePeriod == RecurrencePeriod.HALF_YEAR.getValue()) {
            return DateUtilities.addMonths(date, 6);
        } else if (recurrencePeriod == RecurrencePeriod.YEAR.getValue()) {
            return DateUtilities.addYears(date, 1);
        } else {
            return date;
        }
    }

    public static RecurrencePeriod getRecurrencePeriod(int period) {
        if (period == RecurrencePeriod.DAY.getValue()) {
            return RecurrencePeriod.DAY;
        } else if (period == RecurrencePeriod.WEEK.getValue()) {
            return RecurrencePeriod.WEEK;
        } else if (period == RecurrencePeriod.MONTH.getValue()) {
            return RecurrencePeriod.MONTH;
        } else if (period == RecurrencePeriod.QUARTER.getValue()) {
            return RecurrencePeriod.QUARTER;
        } else if (period == RecurrencePeriod.HALF_YEAR.getValue()) {
            return RecurrencePeriod.HALF_YEAR;
        } else if (period == RecurrencePeriod.YEAR.getValue()) {
            return RecurrencePeriod.YEAR;
        } else {
            return RecurrencePeriod.NONE;
        }
    }

    public static int getRecurrencePeriodAsInt(String period) {
        if (period.equals(RecurrencePeriod.DAY.toString())) {
            return RecurrencePeriod.DAY.getValue();
        } else if (period.equals(RecurrencePeriod.WEEK.toString())) {
            return RecurrencePeriod.WEEK.getValue();
        } else if (period.equals(RecurrencePeriod.MONTH.toString())) {
            return RecurrencePeriod.MONTH.getValue();
        } else if (period.equals(RecurrencePeriod.QUARTER.toString())) {
            return RecurrencePeriod.QUARTER.getValue();
        } else if (period.equals(RecurrencePeriod.HALF_YEAR.toString())) {
            return RecurrencePeriod.HALF_YEAR.getValue();
        } else if (period.equals(RecurrencePeriod.YEAR.toString())) {
            return RecurrencePeriod.YEAR.getValue();
        } else {
            return RecurrencePeriod.NONE.getValue();
        }
    }

    /**
     * @param date
     * @param initDateFormat
     * @param endDateFormat
     * @return
     */
    public static String formatDate(String date, String initDateFormat, String endDateFormat) {
        if (date == null) {
            return null;
        }
        try {
            Date initDate = new SimpleDateFormat(initDateFormat, java.util.Locale.getDefault())
                    .parse(date);
            SimpleDateFormat formatter = new SimpleDateFormat(endDateFormat,
                    java.util.Locale.getDefault());
            return formatter.format(initDate);
        } catch (ParseException pe) {

        }
        return null;
    }

    public static long getTimestampFromDate(String date) {
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_DD_MM_YYYY_SLASH,
                java.util.Locale.getDefault());
        if (date == null || "".equals(format.toString())) {
            return 0;
        }
        try {
            Date d = format.parse(date);
            return d.getTime();
        } catch (ParseException pe) {

        }
        return 0;
    }

    public static String getDateFromTimestamp(long timeStamp) {
        if (timeStamp == 0) {
            return "";
        }
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_DD_MM_YYYY_SLASH,
                java.util.Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);
        return format.format(calendar.getTime());
    }

}
