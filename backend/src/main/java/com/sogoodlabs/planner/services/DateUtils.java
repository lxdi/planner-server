package com.sogoodlabs.planner.services;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;

public class DateUtils {

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static Date toDate(String dateStr) {
        SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_FORMAT);
        java.util.Date date = null;
        try {
            date = sdf1.parse(dateStr);
            return new Date(date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("date parse exception");
    }

    public static Date toDate(int year, int month, int day){
        return toDate(year+"-"+month+"-"+day);
    }

    public static String fromDate(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(date);
    }

    public static int getYear(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }

    public static Date addDays(Date date, int days){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return new Date(cal.getTime().getTime());
    }

    public static Date subtractDays(Date date, int days){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, -days); //minus number would decrement the days
        return new Date(cal.getTime().getTime());
    }

    public static Date currentDate(){
        return new Date(new java.util.Date().getTime());
    }

    public static String currentDateString(){
        return fromDate(currentDate());
    }

    public static Date addWeeks(Date date, int weeks){
//        Calendar c = Calendar.getInstance();
//        c.setTime(date);
//        c.add(Calendar.DATE, weeks*7);
        return addDays(date, weeks*7);
    }

    public static int differenceInDays(Date date1, Date date2){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        LocalDate date1LD = LocalDate.parse(fromDate(date1), formatter);
        LocalDate date2LD = LocalDate.parse(fromDate(date2), formatter);
        return Integer.parseInt(""+ChronoUnit.DAYS.between(date1LD, date2LD));
    }

    public static int dayOfWeek(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayOfWeek = cal.get(java.util.Calendar.DAY_OF_WEEK);
        return dayOfWeek==1?6:dayOfWeek-2;
    }

}
