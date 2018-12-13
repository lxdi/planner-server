package model.dao;

import model.entities.Week;

import java.sql.Date;
import java.util.List;

/**
 * Created by Alexander on 22.04.2018.
 */
public interface IWeekDAO {

    Week getById(long id);
    void saveOrUpdate(Week week);
    List<Week> allWeeks();
    Week weekByStartDate(int day, int month, int year);
    Week weekByStartDate(Date date);
    Week weekByYearAndNumber(int year, int number);

}
