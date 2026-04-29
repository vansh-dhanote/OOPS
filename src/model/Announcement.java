package model;

public class Announcement {
    private int id;
    private String message;
    private String postedBy;

    public Announcement() {
    }

    public Announcement(String message, String postedBy) {
        this.message = message;
        this.postedBy = postedBy;
    }

    public Announcement(int id, String message, String postedBy) {
        this.id = id;
        this.message = message;
        this.postedBy = postedBy;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    @Override
    public String toString() {
        return postedBy + ": " + message;
    }
}
