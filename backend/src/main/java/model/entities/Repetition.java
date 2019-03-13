package model.entities;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.sql.Date;

@Entity
public class Repetition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @ManyToOne
    SpacedRepetitions spacedRepetitions;

    @Basic
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    Date date;

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

    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
}
