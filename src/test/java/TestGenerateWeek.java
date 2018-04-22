import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.Test;
import org.threeten.extra.YearWeek;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander on 22.04.2018.
 */
public class TestGenerateWeek {

    @Test
    public void test(){
        YearWeek start = YearWeek.of( 2018 , 1 ) ;  // First week of the week-based year 2017.
        int numberDaysInTheYear = start.lengthOfYear() ;
        List<String> results = new ArrayList<>(numberDaysInTheYear) ;
        YearWeek yw = start ;
        for( int i = 1 ; i < numberDaysInTheYear; i ++ ) {
            String message = "Week: " + yw + " | start: " + yw.atDay( DayOfWeek.MONDAY ) + " | stop: " + yw.atDay( DayOfWeek.SUNDAY ) ;
            results.add(message);
            // Prepare for next loop.
            yw = yw.plusWeeks( 1 ) ;
            System.out.println(message);
        }
    }

}
