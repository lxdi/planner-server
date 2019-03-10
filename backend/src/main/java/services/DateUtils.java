package services;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateUtils {

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static Date toDate(String dateStr) {
        SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_FORMAT);
        java.util.Date date = null;
        try {
            date = sdf1.parse(dateStr);
            return new java.sql.Date(date.getTime());
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

}
