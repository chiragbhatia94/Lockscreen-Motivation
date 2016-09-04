package com.urhive.lockscreendaycountdown;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Chirag Bhatia on 28-08-2016.
 */
public class DateTimeUtil {

    private static final SimpleDateFormat WEEK_DAYS_FORMAT = new SimpleDateFormat("EEEE", Locale.getDefault());
    private static final SimpleDateFormat SHORT_WEEK_DAYS_FORMAT = new SimpleDateFormat("E", Locale.getDefault());
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    public static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    public static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    String mDate;
    String mTime;
    Calendar mCalendar;
    int mDay, mMonth, mYear, mHour, mMin;
    long inMillis;

    // constructors
    public DateTimeUtil() {
        mCalendar = Calendar.getInstance();
        mDate = dateFormat.format(mCalendar);
        mTime = timeFormat.format(mCalendar);

        String d[] = mDate.split("/");
        mDay = Integer.parseInt(d[0]);
        mMonth = Integer.parseInt(d[1]);
        mYear = Integer.parseInt(d[2]);

        String t[] = mTime.split(":");
        mHour = Integer.parseInt(t[0]);
        mMin = Integer.parseInt(t[1]);

        inMillis = mCalendar.getTimeInMillis();
    }

    public DateTimeUtil(String date, String time) {
        this.mDate = date;
        this.mTime = time;

        String d[] = mDate.split("/");
        mDay = Integer.parseInt(d[0]);
        mMonth = Integer.parseInt(d[1]);
        mYear = Integer.parseInt(d[2]);

        String t[] = mTime.split(":");
        mHour = Integer.parseInt(t[0]);
        mMin = Integer.parseInt(t[1]);

        mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.MONTH, --mMonth);
        mCalendar.set(Calendar.YEAR, mYear);
        mCalendar.set(Calendar.DAY_OF_MONTH, mDay);
        mCalendar.set(Calendar.HOUR_OF_DAY, mHour);
        mCalendar.set(Calendar.MINUTE, mMin);
        mCalendar.set(Calendar.SECOND, 0);
        mCalendar.set(Calendar.MILLISECOND, 0);
        this.inMillis = mCalendar.getTimeInMillis();
    }

    public DateTimeUtil(Calendar calendar) {
        this.mCalendar = calendar;

        mDate = dateFormat.format(mCalendar);
        mTime = timeFormat.format(mCalendar);

        String d[] = mDate.split("/");
        mDay = Integer.parseInt(d[0]);
        mMonth = Integer.parseInt(d[1]);
        mYear = Integer.parseInt(d[2]);

        String t[] = mTime.split(":");
        mHour = Integer.parseInt(t[0]);
        mMin = Integer.parseInt(t[1]);

        inMillis = mCalendar.getTimeInMillis();
    }

    public DateTimeUtil(long timeInMillis) {
        this.inMillis = timeInMillis;

        mCalendar.setTimeInMillis(inMillis);
        mDate = dateFormat.format(mCalendar);
        mTime = timeFormat.format(mCalendar);

        String d[] = mDate.split("/");
        mDay = Integer.parseInt(d[0]);
        mMonth = Integer.parseInt(d[1]);
        mYear = Integer.parseInt(d[2]);

        String t[] = mTime.split(":");
        mHour = Integer.parseInt(t[0]);
        mMin = Integer.parseInt(t[1]);
    }

    // static methods
    // compare current date time with parameters
    public static Boolean isInPast(String date, String time) {
        String d[] = date.split("/");
        int day = Integer.parseInt(d[0]);
        int month = Integer.parseInt(d[1]);
        int year = Integer.parseInt(d[2]);

        String t[] = time.split(":");
        int hour = Integer.parseInt(t[0]);
        int min = Integer.parseInt(t[1]);

        Calendar calendar = Calendar.getInstance();
        calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, --month);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        long timeInMillis = calendar.getTimeInMillis();

        Calendar now = Calendar.getInstance();
        return now.getTimeInMillis() >= timeInMillis;
    }

    public static int noOfDaysLeft(String date, String weekDaysToCount) {

        String d[] = date.split("/");
        int day = Integer.parseInt(d[0]);
        int month = Integer.parseInt(d[1]);
        int year = Integer.parseInt(d[2]);

        Calendar endCal = Calendar.getInstance();
        endCal.set(Calendar.MONTH, --month);
        endCal.set(Calendar.YEAR, year);
        endCal.set(Calendar.DAY_OF_MONTH, day);

        Calendar startCal = Calendar.getInstance();

        int workDays = 0;

        //Return 0 if start and end are the same
        if (startCal.getTimeInMillis() >= endCal.getTimeInMillis()) {
            return 0;
        }

        do {
            //excluding start date
            startCal.add(Calendar.DAY_OF_MONTH, 1);

            int dayX = startCal.get(Calendar.DAY_OF_WEEK);

            if (weekDaysToCount.contains(String.valueOf(dayX))) {
                ++workDays;
            }

        } while (startCal.getTimeInMillis() < endCal.getTimeInMillis()); //excluding end date

        return workDays;
    }

    // get current date and time
    public static String getCurrentDate() {
        Calendar now = Calendar.getInstance();
        return dateFormat.format(now.getTime());
    }

    public static String getCurrentTime() {
        Calendar now = Calendar.getInstance();
        return timeFormat.format(now.getTime());
    }

    public static String getDate(int d, int m, int y) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, --m);
        calendar.set(Calendar.YEAR, y);
        calendar.set(Calendar.DAY_OF_MONTH, d);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return dateFormat.format(calendar.getTime());
    }

    public static String getTime(int h, int m) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, h);
        calendar.set(Calendar.MINUTE, m);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return timeFormat.format(calendar.getTime());
    }

    public static String[] getShortWeekDays() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        String[] weekDays = new String[7];
        for (int i = 0; i < 7; i++) {
            weekDays[i] = SHORT_WEEK_DAYS_FORMAT.format(calendar.getTime());
            calendar.add(Calendar.DATE, 1);
        }
        return weekDays;
    }

    public static String addDaysToDate(String date, int noOfDays) {
        String d[] = date.split("/");
        int day = Integer.parseInt(d[0]);
        int month = Integer.parseInt(d[1]);
        int year = Integer.parseInt(d[2]);


        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, --month);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        calendar.add(Calendar.DATE, noOfDays);

        return dateFormat.format(calendar.getTime());
    }

    public static Calendar addTimeToCalender(String time, Calendar calendar) {
        String t[] = time.split(":");
        int hour = Integer.parseInt(t[0]);
        int min = Integer.parseInt(t[1]);

        calendar.add(Calendar.HOUR, hour);
        calendar.add(Calendar.MINUTE, min);

        return calendar;
    }

    public static Calendar getCalendar(String mDate) {
        String d[] = mDate.split("/");
        int mDay = Integer.parseInt(d[0]);
        int mMonth = Integer.parseInt(d[1]);
        int mYear = Integer.parseInt(d[2]);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, --mMonth);
        calendar.set(Calendar.YEAR, mYear);
        calendar.set(Calendar.DAY_OF_MONTH, mDay);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar;
    }

    public String getmDate() {
        return mDate;
    }

    // getters

    public String getmTime() {
        return mTime;
    }

    public Calendar getmCalendar() {
        return mCalendar;
    }

    public void setmCalendar(Calendar mCalendar) {
        this.mCalendar = mCalendar;
        mDate = dateFormat.format(mCalendar.getTime());
        mTime = timeFormat.format(mCalendar.getTime());

        inMillis = mCalendar.getTimeInMillis();

        String d[] = mDate.split("/");
        mDay = Integer.parseInt(d[0]);
        mMonth = Integer.parseInt(d[1]);
        mYear = Integer.parseInt(d[2]);

        String t[] = mTime.split(":");
        mHour = Integer.parseInt(t[0]);
        mMin = Integer.parseInt(t[1]);
    }

    public int getmDay() {
        return mDay;
    }

    public int getmMonth() {
        return mMonth;
    }

    public int getmYear() {
        return mYear;
    }

    public int getmHour() {
        return mHour;
    }

    public int getmMin() {
        return mMin;
    }

    public long getInMillis() {
        return inMillis;
    }
}
