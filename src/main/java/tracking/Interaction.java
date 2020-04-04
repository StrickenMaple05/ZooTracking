package tracking;

import java.util.Date;

public class Interaction {

    private final Tracked trackedA;
    private final Tracked trackedB;
    private final Date begin;
    private Date end;

    public Interaction(Tracked trackedA, Tracked trackedB, Date begin) {
        this.trackedA = trackedA;
        this.trackedB = trackedB;
        this.begin = begin;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public Tracked trackedA() {
        return trackedA;
    }
    public Tracked trackedB() {
        return trackedB;
    }

    public Date getBegin() {
        return begin;
    }
    public Date getEnd() {
        return end;
    }

    @Override
    public String toString() {
        return "Interaction{" +
                "trackedA=" + trackedA +
                ", trackedB=" + trackedB +
                ", begin=" + begin +
                ", end=" + end +
                '}';
    }
}
