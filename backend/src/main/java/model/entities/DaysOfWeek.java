package model.entities;

import com.sogoodlabs.common_mapper.IEnumForCommonMapper;

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

    @Override
    public String value() {
        return this.name();
    }
}
