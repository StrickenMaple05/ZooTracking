package test.tracking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tracking.Interaction;
import tracking.TrackingService;
import zoo.IllegalAction;
import zoo.Position;
import zoo.Shift;
import zoo.Zoo;
import zoo.animal.Animal;
import zoo.employee.Employee;

import javax.security.auth.callback.LanguageCallback;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@DisplayName("Тесты для нового задания")
public class NewTrackingTest {

    private final Calendar calendar =new GregorianCalendar(
            1981, Calendar.FEBRUARY, 4);
    private TrackingService trackingService;
    private Employee John;
    private Employee Jack;
    private Animal lion;

    @BeforeEach
    public void Init() {
        trackingService = new TrackingService();
        John = new Employee("Джон", calendar.getTime());
        Jack = new Employee("Джек", calendar.getTime());
        lion = new Animal("лев", new Date());
    }

    @DisplayName("Длительность смены работника")
    @Test
    public void shiftTest() {

        /* Учитывая, что точка "генерации" - середина зоопарка,
         *  зададим Джону местоположение отдельно от сервиса отслеживания */
        John.updatePosition(Zoo.size + 1, 0);
        trackingService.add(John);

        List<Shift> shifts = new ArrayList<>();
        trackingService.updatePositions(new Position(Zoo.size - 1, 0));
        shifts.add(new Shift(new Date()));
        Assertions.assertEquals(shifts.toString(), John.getShifts().toString());

        trackingService.updatePositions(new Position(Zoo.size + 1, 0));
        shifts.get(0).setEnd(new Date());
        Assertions.assertEquals(shifts.toString(), John.getShifts().toString());
    }

    @DisplayName("Взаимодействие между сотрудниками")
    @Test
    public void employeeEmployeeTest() {

        /* Из-за того что trackable типа set, контакт между двумя сотрудниками может быть записан
       двумя способами, в зависимости от того, в каком порядке будут храниться элементы множества.
       Поэтому для проверки создаю два взаимодействия.
         */

        trackingService.add(John, Jack);
        /* John - (6,3)
           Jack - (3,3) */
        Interaction interaction;
        Interaction inverse_interaction;
        List<Interaction> interactions;

        trackingService.updatePositions(new Position(6,3), new Position(3,3));
        interaction = new Interaction(John, Jack, new Date());
        inverse_interaction = new Interaction(Jack, John, new Date());
        interactions = new ArrayList<>(trackingService.getCurrentInteractions());

        Assertions.assertTrue(interactions.get(0).toString().equals(interaction.toString()) ||
                interactions.get(0).toString().equals(inverse_interaction.toString()));

        trackingService.updatePositions(new Position(9,3), new Position(3,3));
        interaction.setEnd(new Date());
        inverse_interaction.setEnd(new Date());
        interactions = new ArrayList<>(trackingService.getEmployeeInteractions());

        Assertions.assertTrue(interactions.get(0).toString().equals(interaction.toString()) ||
                interactions.get(0).toString().equals(inverse_interaction.toString()));
    }

    @DisplayName("Взаимодействие между сотрудником и животным")
    @Test
    public void employeeAnimalTest() {

        /* В случае с животными всё проще, потому что во взаимодействии первый участник
        всегда сотрудник. Поэтому заморочки с двумя взаимодействиями не требуются.
         */

        trackingService.add(John, lion);
        /* John - (6,3)
           lion - (3,3) */
        Interaction interaction;
        List<Interaction> interactions;

        trackingService.updatePositions(new Position(6,3), new Position(3,3));
        interaction = new Interaction(John, lion, new Date());
        interactions = new ArrayList<>(trackingService.getCurrentInteractions());

        Assertions.assertEquals(interactions.get(0).toString(), interaction.toString());

        trackingService.updatePositions(new Position(9,3), new Position(3,3));
        interaction.setEnd(new Date());
        interactions = new ArrayList<>(trackingService.getInteractions());

        Assertions.assertEquals(interactions.get(0).toString(), interaction.toString());
    }

    @DisplayName("Подсчёт времени, проведённого сотрудником с подопечными")
    @Test
    public void EmployeeWardTest() throws InterruptedException {
        John.updatePosition(Zoo.size + 1, 0);

        trackingService.add(John, lion);
        /* Джон не является опекуном льва */
        /* Инициализирован контакт */
        trackingService.updatePositions(
                new Position(Zoo.size - 3, 0),
                new Position(Zoo.size - 2, 0));
        trackingService.updatePositions(
                new Position(Zoo.size - 3, 0),
                new Position(Zoo.size - 1, 3));

        /* Теперь Джон - опекун льва */
        lion.add(John);
        John.add(lion);

        /* Инциализированы ещё два контакта */
        trackingService.updatePositions(
                new Position(Zoo.size - 1, 1),
                new Position(Zoo.size - 2, 2));
        Thread.sleep(500);
        trackingService.updatePositions(
                new Position(Zoo.size - 3, 0),
                new Position(Zoo.size - 1, 3));

        trackingService.updatePositions(
                new Position(Zoo.size - 3, 0),
                new Position(Zoo.size - 2, 0));
        Thread.sleep(500);
        trackingService.updatePositions(
                new Position(Zoo.size - 3, 0),
                new Position(Zoo.size - 1, 3));

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Assertions.assertEquals("00:00:01",
                dateFormat.format(John.getWardInteractionTime()));
    }

    @DisplayName("Инициализация нарушений сотрудников")
    @Test
    public void IllegalActionTest() {
        IllegalAction illegalAction;
        Interaction interaction;
        /* Джон находится вне зоопарка */
        John.updatePosition(Zoo.size + 2, 0);

        /* Джона взяли на работу */
        trackingService.add(John, lion);

        /* Джон заходит в зоопарк, к тому же контактирует со львом  */
        trackingService.updatePositions(new Position(Zoo.size - 1, 0),
                                        new Position(Zoo.size - 2, 0));
        interaction = new Interaction(John, lion, new Date());
        /* Джон вынес льва из зоопарка... а он же только-только на свет появился(  */
        trackingService.updatePositions(new Position(Zoo.size + 1, 0),
                                        new Position(Zoo.size + 1, 1));
        /* Фиксируем нарушение! Ты у нас за всё ответишь! */
        illegalAction = new IllegalAction(interaction, new Date());

        Assertions.assertEquals((Collections.singletonList(illegalAction)).toString(),
                trackingService.getIllegalActions().toString());

        /* Они вернулись... непонятно */
        trackingService.updatePositions(new Position(Zoo.size - 5, 0),
                                        new Position(Zoo.size - 1, 0));
        /* Опять взял льва */
        trackingService.updatePositions(new Position(Zoo.size - 2, 0),
                                        new Position(Zoo.size - 1, 0));
        /* Опять ушли... */
        trackingService.updatePositions(new Position(Zoo.size + 4, 0),
                                        new Position(Zoo.size + 3, 0));

        /* Всё, собираем инфу по Джону и идём в полицию */
        Assertions.assertEquals(2,trackingService.getIllegalActionsNumber(John));
        Date date = new Date();
    }
}
