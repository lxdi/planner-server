package model.entities;

public enum DaysOfWeek {

    sun("Sunday"), mon("Monday"), tue("Tuesday"), wed("Wednesday"), thu("Thursday"), fri("Friday"), sat("Saturday");

    String fullname;

    DaysOfWeek(String fullname){
        this.fullname = fullname;
    }

}
