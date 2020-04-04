package zoo.employee;

import zoo.animal.Animal;

import java.util.Date;

/** Контакт с животным (вместе с длительностью) */
public class WardInteraction {
    private final Animal animal;
    private final Date begin;
    private final Date end;

    public WardInteraction(Animal animal, Date begin, Date end) {
        this.animal = animal;
        this.begin = begin;
        this.end = end;
    }

    public Animal getAnimal() {
        return animal;
    }

    public Date getBegin() {
        return begin;
    }

    public Date getEnd() {
        return end;
    }

    @Override
    public String toString() {
        return "WardInteraction{" +
                "animal=" + animal +
                ", begin=" + begin +
                ", end=" + end +
                '}';
    }
}
