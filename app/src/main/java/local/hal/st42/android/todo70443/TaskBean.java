package local.hal.st42.android.todo70443;

public class TaskBean {
    private String name;
    private String deadline;
    private int done;
    private String note;
    private long _id;



    public TaskBean() {

    }
    public long getId() {
        return _id;
    }

    public void setId(long _id) {
        this._id = _id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public int getDone() {
        return done;
    }

    public void setDone(int done) {
        this.done = done;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }


}
