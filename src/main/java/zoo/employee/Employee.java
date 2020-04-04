package zoo.employee;

import tracking.Tracked;
import zoo.Position;
import zoo.Shift;
import zoo.Zoo;
import zoo.animal.Animal;

import java.util.*;

public class Employee implements Tracked {


    private static final String PREFIX = "employee-";
    private static int ID = 0;


    public double x;
    public double y;


    /** Уникальный идентификатор */
    private final int id;
    /** Имя сотрудника */
    private final String name;
    /** дата рождения */
    private final Date dateOfBirth;
    /** Подопечные */
    private Set<Animal> animals;
    /** Журнал передвижений сотрудника */
    private List<Position> movements;
    /** Журнал, фиксирующий длительности рабочих смен */
    private List<Shift> shifts;
    /** Журнал всех контактов с животными */
    private List<WardInteraction> wardInteractions;


    /**
     * Сотрудник
     * @param name Имя
     * @param dateOfBirth дата рождения
     */
    public Employee(String name, Date dateOfBirth) {
        id = ID++;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        animals = new HashSet<>();
        movements = new ArrayList<>();
        shifts = new ArrayList<>();
        wardInteractions = new ArrayList<>();
    }


    /**
     * Находится ли сотрудник в зоопарке
     * @return {@code true}, если находится
     */
    public boolean isInZoo() {
        return !(Math.abs(x) > Zoo.size || Math.abs(y) > Zoo.size);
    }


    //================ Методы взаимодействия с подопечными ================//

    /**
     * Добавление в журнал нового взаимодействия
     * @param wardInteraction взаимодействие
     */
    public void add(WardInteraction wardInteraction) {
        wardInteractions.add(wardInteraction);
    }
    /**
     * Добавление подопечного
     * @param animal подопечный
     */
    public void add(Animal animal) {
        animals.add(animal);
    }
    /**
     * Снятие ответственности за животное
     * @param animal животное
     */
    public void remove(Animal animal) {
        animals.remove(animal);
    }
    /**
     * Находится ли животное на попечении?
     * @param animal животное
     * @return правда, если находится
     */
    public boolean isCare(Animal animal) {
        return animals.contains(animal);
    }
    //=====================================================================//


    //==================== Методы работы с рабочей сменой ====================//
    /**
     * Начало нового рабочего дня
     * @param begin начало
     */
    public void setBegin(Date begin) {
        shifts.add(new Shift(begin));
    }
    /**
     * Конец рабочего дня
     * @param end конец
     */
    public void setEnd(Date end) {

        shifts.get(shifts.size() - 1).setEnd(end);
    }
    //========================================================================//


    //========================== Интерфейсные методы =============================//
    /**
     * Реализация интерфейсного метода: получение уникального идентификатора
     * @return id
     */
    public String getId() {
        return PREFIX.concat(Integer.toString(id));
    }
    /**
     * Реализация интерфейсного метода обновления позиции
     * @param x по OX
     * @param y по OY
     */
    public void updatePosition(double x, double y) {

        this.x = x;
        this.y = y;

        Position position = new Position(x, y);

        if (movements.size() == 0) {
            movements.add(position);
            return;
        }
        Position latestPosition = movements.get(movements.size() - 1);
        if (position.x != latestPosition.x || position.y != latestPosition.y) {
            movements.add(position);
        }
    }
    //============================================================================//


    public String getName() {
        return name;
    }
    public Date getDateOfBirth() {
        return dateOfBirth;
    }
    public Set<Animal> getAnimals() {
        return animals;
    }
    public List<Position> getMovements() {
        return movements;
    }
    public List<Shift> getShifts() {
        return shifts;
    }
    public List<WardInteraction> getWardInteractions() {
        return wardInteractions;
    }
    /**
     * Метод подсчитывает суммарное время, проведённое сотрудником с подопечными
     * @return время формата {@code Date}
     */
    public Date getWardInteractionTime() {
        Date begin;
        Date end;

        int hour;
        int minute;
        int second;
        long milliseconds = 0;

        for (WardInteraction wardInteraction : wardInteractions) {
            begin = wardInteraction.getBegin();
            end = wardInteraction.getEnd();
            milliseconds += end.getTime() - begin.getTime();
        }
        second = (int) (milliseconds / 1000);
        minute = second / 60;
        hour = minute / 60;
        second %= 60;
        minute %= 60;
        Calendar calendar = new GregorianCalendar();
        calendar.set(0,Calendar.JANUARY, 0, hour, minute, second);
        return calendar.getTime();
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", animals=" + animals +
                ", movements=" + movements +
                ", shifts=" + shifts +
                ", wardInteractions=" + wardInteractions +
                '}';
    }
}
