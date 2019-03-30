package model.dto.hquarter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class HquarterDtoLazy {

    long id;
    Map<String, Object> startWeek;
    Map<String, Object> endWeek;

    List<Map<String, Object>> slotsLazy = new ArrayList<>();

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public Map<String, Object> getStartWeek() {
        return startWeek;
    }
    public void setStartWeek(Map<String, Object> startWeek) {
        this.startWeek = startWeek;
    }

    public Map<String, Object> getEndWeek() {
        return endWeek;
    }
    public void setEndWeek(Map<String, Object> endWeek) {
        this.endWeek = endWeek;
    }

    public List<Map<String, Object>> getSlotsLazy() {
        return slotsLazy;
    }
    public void setSlotsLazy(List<Map<String, Object>> slotsLazy) {
        this.slotsLazy = slotsLazy;
    }
}
