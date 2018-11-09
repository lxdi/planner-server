package model;

import model.entities.Week;

import java.util.List;
import java.util.Map;

/**
 * Created by Alexander on 22.04.2018.
 */
public interface IWeekDAO {

    Week getById(long id);
    void createWeek(Week week);
    List<Week> allWeeks();
    List<Week> getWeeksOfYear(int year);
    Map<Integer, List<Week>> getWeeksMap();
    Week weekByStartDate(int day, int month, int year);

}
