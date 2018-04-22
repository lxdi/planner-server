package model;

import model.entities.Week;

import java.util.List;
import java.util.Map;

/**
 * Created by Alexander on 22.04.2018.
 */
public interface IWeekDAO {

    void createWeek(Week week);
    List<Week> allWeeks();
    List<Week> getWeeksOfYear(int year);
    Map<Integer, List<Week>> getWeeksMap();

}
