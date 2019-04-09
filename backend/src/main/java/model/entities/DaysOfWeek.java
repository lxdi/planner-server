package model.entities;

import com.sogoodlabs.common_mapper.IEnumForCommonMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum DaysOfWeek implements IEnumForCommonMapper {

    mon(0, "Monday"), tue(1, "Tuesday"), wed(2, "Wednesday"), thu(3, "Thursday"), fri(4, "Friday"), sat(5, "Saturday"), sun(6, "Sunday");

    int id;
    String fullname;

    DaysOfWeek(int id, String fullname){
        this.id = id;
        this.fullname = fullname;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public static DaysOfWeek findById(int id){
        for(DaysOfWeek day : DaysOfWeek.values()){
            if(day.getId()==id){
                return day;
            }
        }
        return null;
    }

    public static List<DaysOfWeek> getLessThen(DaysOfWeek daysOfWeek){
        List<DaysOfWeek> result = new ArrayList<>();
        Arrays.stream(DaysOfWeek.values()).forEach(dow -> {
            if(dow.getId()<daysOfWeek.getId()){
                result.add(dow);
            }
        });
        return result;
    }

    @Override
    public String value() {
        return this.name();
    }
}
