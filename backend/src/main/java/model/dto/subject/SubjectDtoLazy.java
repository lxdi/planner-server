package model.dto.subject;

public class SubjectDtoLazy {

    private long id;
    private String title;
    private Long layerid;
    private int position;

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public Long getLayerid() {
        return layerid;
    }
    public void setLayerid(Long layerid) {
        this.layerid = layerid;
    }

    public int getPosition() {
        return position;
    }
    public void setPosition(int position) {
        this.position = position;
    }
}
