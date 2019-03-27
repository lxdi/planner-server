package model.entities;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.sql.Date;

@Entity
public class Repetition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @ManyToOne(fetch = FetchType.LAZY)
    SpacedRepetitions spacedRepetitions;

    @Basic
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    Date planDate;

    @Basic
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    Date factDate;

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public SpacedRepetitions getSpacedRepetitions() {
        return spacedRepetitions;
    }
    public void setSpacedRepetitions(SpacedRepetitions spacedRepetitions) {
        this.spacedRepetitions = spacedRepetitions;
    }

    public Date getPlanDate() {
        return planDate;
    }
    public void setPlanDate(Date planDate) {
        this.planDate = planDate;
    }

    public Date getFactDate() {
        return factDate;
    }

    public void setFactDate(Date factDate) {
        this.factDate = factDate;
    }
}
