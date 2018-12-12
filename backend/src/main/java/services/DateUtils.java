package services;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

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

}
