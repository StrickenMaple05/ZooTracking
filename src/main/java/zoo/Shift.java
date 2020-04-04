package zoo;

import java.util.Date;

/** Рабочая смена */
public class Shift {

    private final Date begin;
    private Date end;

    public Shift(Date begin) {
        this.begin = begin;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "Shift{" +
                "begin=" + begin +
                ", end=" + end +
                '}';
    }
}
