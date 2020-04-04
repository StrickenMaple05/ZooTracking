package test.tracking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tracking.Interaction;
import tracking.TrackingService;
import zoo.Position;
import zoo.animal.Animal;
import zoo.employee.Employee;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@DisplayName("Проверка системы отслеживания")
public class TrackingTest {

    private final Calendar calendar =new GregorianCalendar(
            1981, Calendar.FEBRUARY, 4);
    private TrackingService trackingService;
    private Employee John;
    private Animal lion;

    @BeforeEach
    public void Init() {
        trackingService = new TrackingService();
        John = new Employee("Джон", calendar.getTime());
        lion = new Animal("лев", new Date());
    }

    @DisplayName("Обновление местоположения")
    @Test
    public void UpdatePositionTest() {

        trackingService.add(John, lion);
        List<Position> JohnMovements = new ArrayList<>();
        List<Position> lionMovements = new ArrayList<>();
        JohnMovements.add(new Position(3,5));
        JohnMovements.add(new Position(5,7));
        lionMovements.add(new Position(-3,-5));
        lionMovements.add(new Position(-5,-7));

        John.updatePosition(JohnMovements.get(0).x, JohnMovements.get(0).y);
        John.updatePosition(JohnMovements.get(1).x, JohnMovements.get(1).y);
        lion.updatePosition(lionMovements.get(0).x, lionMovements.get(0).y);
        lion.updatePosition(lionMovements.get(1).x, lionMovements.get(1).y);

        Assertions.assertEquals(JohnMovements.toString(), John.getMovements().toString());
        Assertions.assertEquals(lionMovements.toString(), lion.getMovements().toString());
    }


    @DisplayName("Контакт сотрудника и животного")
    @Test
    public void ContactTest() {

        trackingService.add(John, lion);

        Position JohnPosition = new Position(4,5);
        Position lionPosition = new Position(3,5);

        trackingService.updatePositions(JohnPosition, lionPosition);

        List<Interaction> currentInteractions = new ArrayList<>();
        List<Interaction> interactions = new ArrayList<>();
        Interaction JohnLion = new Interaction(John, lion, new Date());
        /* Добавляем новое взаимодействие в список текущих контактов */
        currentInteractions.add(JohnLion);

        Assertions.assertEquals(currentInteractions.toString(),
                trackingService.getCurrentInteractions().toString());

        /* Перемещаем Джона на позицию (7,5) */
        JohnPosition.x = 7;
        trackingService.updatePositions(JohnPosition, lionPosition);

        /* Удаляем контакт из списка текущих взаимодействий.
           Устанавливаем конец взаимодействия */
        currentInteractions.remove(JohnLion);
        JohnLion.setEnd(new Date());

        Assertions.assertEquals(currentInteractions.toString(),
                trackingService.getCurrentInteractions().toString());

        /* Добавляем его в историю взаимодействий */
        interactions.add(JohnLion);

        Assertions.assertEquals(interactions.toString(),
                trackingService.getInteractions().toString());
    }

    @DisplayName("Инициализация прихода и ухода сотрудников")
    @Test
    public void EmployeeActionTest() {
        List<String> employeeActions = new ArrayList<>();
        John.updatePosition(100,100);
        trackingService.add(John);
        employeeActions.add(new Date() + " | " + John.getId() + " entered the zoo");

        trackingService.updatePositions(new Position(0, 0));

        /* Проверяем, зашёл ли Джон в зоопарк */
        Assertions.assertTrue(John.isInZoo());
        /* Проверяем, внесена ли заметка о том, что Джон зашёл в зоопарк */
        Assertions.assertEquals(employeeActions.toString(),
                trackingService.getEmployeeActions().toString());

        trackingService.updatePositions(new Position(100, 100));
        employeeActions.add(new Date() + " | " + John.getId() + " left the zoo");
        /* Проверяем, находится ли Джон в зоопарке */
        Assertions.assertFalse(John.isInZoo());
        /* Проверяем, внесена ли заметка о том, что Джон вышел из зоопарка */
        Assertions.assertEquals(employeeActions.toString(),
                trackingService.getEmployeeActions().toString());
    }
}
