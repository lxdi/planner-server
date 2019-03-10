package model.entities;

public enum DaysOfWeek {

    mon(1, "Monday"), tue(2, "Tuesday"), wed(3, "Wednesday"), thu(4, "Thursday"), fri(5, "Friday"), sat(6, "Saturday"), sun(7, "Sunday");

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
}
