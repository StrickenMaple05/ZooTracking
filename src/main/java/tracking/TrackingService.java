package tracking;

import zoo.IllegalAction;
import zoo.Position;
import zoo.Zoo;
import zoo.animal.Animal;
import zoo.employee.Employee;
import zoo.employee.WardInteraction;

import java.util.*;

/**
 * Сервис отслеживания {@link Tracked}
 */
public class TrackingService {


    /** размер стороны зоны */
    private static final double zone = Zoo.size;


    /** Множество отслеживаемых объектов */
    private Set<Tracked> trackable;
    /** Журнал приходов и уходов сотрудников */
    private List<String> employeeActions;
    /** Список текущих взаимодействий между сотрудниками и животными */
    private Set<Interaction> currentInteractions;
    /** Список взаимодействий между сотрудниками и животными */
    private List<Interaction> interactions;
    /** Журнал взаимодействий сотрудников */
    private List<Interaction> employeeInteractions;
    /** Журнал незаконных действий */
    private List<IllegalAction> illegalActions;


    /** Сервис отслеживания */
    public TrackingService() {
        trackable = new HashSet<>();
        interactions = new ArrayList<>();
        currentInteractions = new HashSet<>();
        employeeActions = new ArrayList<>();
        employeeInteractions = new ArrayList<>();
        illegalActions = new ArrayList<>();
    }


    //=========== Методы обновления местоположения ===========//
    /** Обновление местоположения отслеживаемых объектов */
    public void updatePositions() {
        for (Tracked tracked : trackable) {
            /* Для животного только обновляется местоположение */
            if (tracked instanceof Animal) {
                tracked.updatePosition(Math.random() % (zone * 1.5) - zone / 2,
                        Math.random() % (zone * 1.5) - zone / 2);
                continue;
            }

            /* Для сотрудника определяем, будет ли он находиться в
               той же части пространства по отношению к зоопарку */
            boolean inZoo = ((Employee) tracked).isInZoo();
            tracked.updatePosition(Math.random() % (zone * 1.5) - zone / 2,
                    Math.random() % (zone * 1.5) - zone / 2);

            /* Если сотрудник не входил в зоопарк и не покидал его */
            if (inZoo == ((Employee) tracked).isInZoo()) {
                continue;
            }

            Date date = new Date();
            /* Если вошёл в зоопарк */
            if (inZoo) {
                ((Employee) tracked).setBegin(date);
            }
            /* Если вышел из зоопарка */
            else {
                ((Employee) tracked).setEnd(date);
            }
            employeeActions.add(date + " | " + tracked.getId() +
                    (inZoo ? " left the zoo" : " entered the zoo"));
        }

        /* Проверяем журнал текущих контактов */
        CurrentOrInterruptedInteractions();
        /* Добавляем новые */
        defineInteractions();
    }
    public void updatePositions(Position... newPositions) {
        int i = 0;
        if (newPositions.length != trackable.size()) {
            return;
        }
        List<Position> positions = new ArrayList<>(Arrays.asList(newPositions));
        for (Tracked tracked : trackable) {

            /* Для животного только обновляется местоположение */
            if (tracked instanceof Animal) {
                tracked.updatePosition(positions.get(i).x, positions.get(i++).y);
                continue;
            }

            /* Для сотрудника определяем, будет ли он находиться в
               той же части пространства по отношению к зоопарку */
            boolean inZoo = ((Employee) tracked).isInZoo();
            tracked.updatePosition(positions.get(i).x, positions.get(i++).y);

            /* Если сотрудник не входил в зоопарк и не покидал его */
            if (inZoo == ((Employee) tracked).isInZoo()) {
                continue;
            }

            Date date = new Date();
            /* Если вошёл в зоопарк */
            if (!inZoo) {
                ((Employee) tracked).setBegin(date);
            }
            /* Если вышел из зоопарка */
            else {
                ((Employee) tracked).setEnd(date);
            }
            employeeActions.add(date + " | " + tracked.getId() +
                    (inZoo ? " left the zoo" : " entered the zoo"));
        }

        /* Проверяем журнал текущих контактов */
        CurrentOrInterruptedInteractions();
        /* Добавляем новые */
        defineInteractions();
    }
    //========================================================//


    //=========== Методы, инициализирующие взаимодействия ===========//
    /**
     * Метод, инициализирующий прерывание контактов.
     * Проверяется журнал текущих взаимодействий: если какое-то
     * из них прервалось, то запись добавляется в журнал взаимодействий
     * и удаляется из журнала текущих взаимодействий.
     */
    public void CurrentOrInterruptedInteractions() {

        for (Interaction interaction : currentInteractions) {
            /* Теперь необходимо отследить выход сотрудника с животным */
            if (isContact(interaction.trackedA(),
                    interaction.trackedB())) {
                /* Случай с двумя сотрудниками нас не интересует */
                if (!(interaction.trackedB() instanceof Animal)) {
                    continue;
                }
                /* Проверяем, находится ли сотрудник в зоопарке */
                if (((Employee) interaction.trackedA()).isInZoo()) {
                    continue;
                }
                /* Опа, нарушение!
                Проверяем, было ли это нарушение уже зафиксировано */
                boolean wasNoticed = false;
                IllegalAction illegalAction = new IllegalAction(
                        interaction, new Date());
                for (IllegalAction temp : illegalActions) {
                    if (notEqual(illegalAction, temp)) {
                        continue;
                    }
                    wasNoticed = true;
                    break;
                }
                /* Если преступление было зафиксировано ранее */
                if (wasNoticed) {
                    continue;
                }
                illegalActions.add(illegalAction);
                continue;
            }

            /* удаляем из журнала текущих контактов */
            currentInteractions.remove(interaction);
            /* устанавливаем конец контакта */
            interaction.setEnd(new Date());
            /* добавляем контакт в журнал */
            if (interaction.trackedB() instanceof Animal) {
                interactions.add(interaction);
                /* Нас интересует случай, когда сотрудник - опекун животного */
                if (!(((Employee) interaction.trackedA()).isCare(
                        (Animal) interaction.trackedB()))) {
                    return;
                }
                /* Добавляем запись о контакте сотрудника и подопечного */
                ((Employee) interaction.trackedA()).add(
                        new WardInteraction(
                                (Animal) interaction.trackedB(),
                                interaction.getBegin(),
                                interaction.getEnd()));
                return;
            }
            employeeInteractions.add(interaction);
        }
    }
    /**
     * Метод, определяющий взаимодействие сотрудников и животных.
     * Проводится поиск контактов "сотрудник : животное ", которые
     * добавляются в список текущих контактов
     */
    public void defineInteractions() {

        /* Для определённости первый объект - сотрудник */
        for (Tracked trackedA : trackable) {
            if (trackedA instanceof Animal) {
                continue;
            }
            for (Tracked trackedB : trackable) {
                if (trackedA == trackedB) {
                    continue;
                }
                if (!isContact(trackedA, trackedB)) {
                    continue;
                }
                boolean ongoingContact = false;
                for (Interaction interaction : currentInteractions) {
                    if ((interaction.trackedA() == trackedA) && (interaction.trackedB() == trackedB) ||
                            (interaction.trackedB() == trackedA) && (interaction.trackedA() == trackedB)) {
                        ongoingContact = true;
                        break;
                    }
                }
                if (ongoingContact) {
                    continue;
                }
                currentInteractions.add(new Interaction(
                        trackedA, trackedB, new Date()));
            }
        }
    }
    /**
     * Определяет, есть ли контакт между объектами
     * @param trackedA первый объект
     * @param trackedB второй объект
     * @return {@code true}, если есть контакт
     */
    public boolean isContact(Tracked trackedA, Tracked trackedB) {
        Employee a = (Employee) trackedA;
        if (trackedB instanceof Employee) {
            Employee b = (Employee) trackedB;
            return Math.pow(b.x - a.x, 2) + Math.pow(b.y - a.y, 2) <= 9;
        }
        Animal b = (Animal) trackedB;
        return Math.pow(b.x - a.x, 2) + Math.pow(b.y - a.y, 2) <= 9;
    }
    //===============================================================//

    /**
     * Проверка, являются ли два инициализированных нарушения одним и тем же
     * @param illegalAction1 первое нарушение
     * @param illegalAction2 второе нарушение
     * @return {@code true}, если они разные
     */
    public boolean notEqual(IllegalAction illegalAction1,
                           IllegalAction illegalAction2) {

        return (illegalAction1.getEmployee() != illegalAction2.getEmployee() ||
                illegalAction1.getAnimal() != illegalAction2.getAnimal() ||
                illegalAction1.getBegin() != illegalAction2.getBegin());
    }


    /**
     * Добавление отслеживаемого объекта
     * @param tracked объект
     */
    public void add(Tracked... tracked) {
        trackable.addAll(Arrays.asList(tracked));
    }
    /**
     * Снятие отслеживания с объекта
     * @param tracked объект
     */
    public void remove(Tracked... tracked) {
        for (Tracked temp : tracked) {
            trackable.remove(temp);
        }
    }


    public Set<Tracked> getTrackable() {
        return trackable;
    }
    public List<Interaction> getInteractions() {
        return interactions;
    }
    public Set<Interaction> getCurrentInteractions() {
        return currentInteractions;
    }
    public List<String> getEmployeeActions() {
        return employeeActions;
    }
    public List<Interaction> getEmployeeInteractions() {
        return employeeInteractions;
    }
    public List<IllegalAction> getIllegalActions() {
        return illegalActions;
    }

    /**
     * Возвращает количество преступлений, совершённых {@code employee}
     * @param employee сотрудник
     * @return количество преступлений
     */
    public int getIllegalActionsNumber(Employee employee) {
        int number = 0;
        for (IllegalAction illegalAction : illegalActions) {
            number += illegalAction.getEmployee() == employee ? 1 : 0;
        }
        return number;
    }

    @Override
    public String toString() {
        return "TrackingService{" +
                "trackable=" + trackable +
                ", employeeActions=" + employeeActions +
                ", currentInteractions=" + currentInteractions +
                ", interactions=" + interactions +
                ", employeeInteractions=" + employeeInteractions +
                ", illegalActions=" + illegalActions +
                '}';
    }
}
