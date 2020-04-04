package zoo;

import tracking.Interaction;
import tracking.Tracked;

import java.util.Date;

/** Незаконное действие */
public class IllegalAction {

    /* По заданию, незаконным действием считается выход сотрудника из зоопарка,
    при этом сохраняя контакт с животным. Но в общем случае незаконные действия могут
    совершаться и двумя сотрудниками, поэтому поля будут иметь тип Tracked, для
    облегчения дальнейших модификаций.
     */

    private final Tracked employee;
    private final Tracked animal;
    private final Date begin;
    private final Date date;

    public IllegalAction(Interaction interaction, Date date) {

        this.employee = interaction.trackedA();
        this.animal = interaction.trackedB();
        this.begin = interaction.getBegin();
        this.date = date;
    }

    public Tracked getEmployee() {
        return employee;
    }

    public Tracked getAnimal() {
        return animal;
    }

    public Date getBegin() { return begin; }

    public Date getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "IllegalAction{" +
                "employee=" + employee +
                ", animal=" + animal +
                ", date=" + date +
                '}';
    }
}
