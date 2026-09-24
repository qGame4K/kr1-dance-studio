package ru.mirea.dancestudio.ui;

import ru.mirea.dancestudio.exception.DanceStudioException;
import ru.mirea.dancestudio.model.Client;
import ru.mirea.dancestudio.model.DanceClass;
import ru.mirea.dancestudio.model.DanceLevel;
import ru.mirea.dancestudio.model.DanceStyle;
import ru.mirea.dancestudio.model.Enrollment;
import ru.mirea.dancestudio.model.EnrollmentStatus;
import ru.mirea.dancestudio.model.Statistics;
import ru.mirea.dancestudio.service.ClientService;
import ru.mirea.dancestudio.service.DanceClassService;
import ru.mirea.dancestudio.service.EnrollmentService;
import ru.mirea.dancestudio.service.ExportService;
import ru.mirea.dancestudio.service.StatisticsService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Консольное меню программы — единственное место, где мы печатаем текст
 * и читаем ввод пользователя. Вся проверка данных и вся логика находятся
 * в сервисах (service/*), сюда попадают только готовые вызовы и печать результата.
 *
 * Если сервис бросает DanceStudioException (например, "занятие не найдено"
 * или "свободных мест нет") — мы ловим её в run() и печатаем сообщение об ошибке,
 * вместо того чтобы уронить всю программу.
 */
public class ConsoleApp {

    private static final DateTimeFormatter FILE_TIMESTAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    private final ClientService clientService;
    private final DanceClassService classService;
    private final EnrollmentService enrollmentService;
    private final StatisticsService statisticsService;
    private final ExportService exportService;
    private final ConsoleInput input;

    public ConsoleApp(ClientService clientService, DanceClassService classService,
                       EnrollmentService enrollmentService, StatisticsService statisticsService,
                       ExportService exportService, ConsoleInput input) {
        this.clientService = clientService;
        this.classService = classService;
        this.enrollmentService = enrollmentService;
        this.statisticsService = statisticsService;
        this.exportService = exportService;
        this.input = input;
    }

    public void run() {
        System.out.println("==================== ТАНЦЕВАЛЬНАЯ СТУДИЯ ====================");
        boolean working = true;
        while (working) {
            System.out.println();
            System.out.println("1. Клиенты");
            System.out.println("2. Занятия");
            System.out.println("3. Записи на занятия");
            System.out.println("4. Статистика");
            System.out.println("5. Экспорт в Excel");
            System.out.println("6. Показать все таблицы базы данных");
            System.out.println("0. Выход");

            int choice = input.readInt("Выберите пункт меню: ");
            try {
                switch (choice) {
                    case 1 -> clientsMenu();
                    case 2 -> classesMenu();
                    case 3 -> enrollmentsMenu();
                    case 4 -> printStatistics();
                    case 5 -> exportToExcel();
                    case 6 -> printAllTables();
                    case 0 -> working = false;
                    default -> System.out.println("Нет такого пункта меню, попробуйте снова.");
                }
            } catch (DanceStudioException e) {
                // Сюда попадает любая ошибка бизнес-логики: некорректные данные,
                // нарушение правил студии, отсутствие записи с таким id и т.п.
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
        System.out.println("До свидания!");
    }

    // =================================================================
    // Клиенты
    // =================================================================

    private void clientsMenu() {
        boolean inMenu = true;
        while (inMenu) {
            System.out.println();
            System.out.println("--- Клиенты ---");
            System.out.println("1. Добавить клиента");
            System.out.println("2. Показать всех клиентов");
            System.out.println("3. Найти клиента по ID");
            System.out.println("4. Изменить клиента");
            System.out.println("5. Удалить клиента");
            System.out.println("6. Поиск клиента (по имени или по контакту)");
            System.out.println("0. Назад");

            try {
                switch (input.readInt("Выберите пункт меню: ")) {
                    case 1 -> createClient();
                    case 2 -> printClients(clientService.findAll());
                    case 3 -> printClients(List.of(clientService.findById(input.readLong("ID клиента: "))));
                    case 4 -> updateClient();
                    case 5 -> {
                        clientService.delete(input.readLong("ID клиента для удаления: "));
                        System.out.println("Клиент удалён.");
                    }
                    case 6 -> searchClients();
                    case 0 -> inMenu = false;
                    default -> System.out.println("Нет такого пункта меню.");
                }
            } catch (DanceStudioException e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }

    private void createClient() {
        String fullName = input.readLine("ФИО: ");
        String phone = input.readLine("Телефон (+79991234567): ");
        String email = input.readLine("Email: ");
        var birthDate = input.readDate("Дата рождения");
        Client client = clientService.create(fullName, phone, email, birthDate);
        System.out.println("Клиент добавлен: " + client);
    }

    private void updateClient() {
        long id = input.readLong("ID клиента: ");
        System.out.println("Текущие данные: " + clientService.findById(id));
        String fullName = input.readLine("Новое ФИО: ");
        String phone = input.readLine("Новый телефон: ");
        String email = input.readLine("Новый email: ");
        var birthDate = input.readDate("Новая дата рождения");
        clientService.update(id, fullName, phone, email, birthDate);
        System.out.println("Клиент обновлён.");
    }

    private void searchClients() {
        System.out.println("1. По имени   2. По телефону/email");
        int mode = input.readInt("Способ поиска: ");
        String query = input.readLine("Что искать: ");
        List<Client> result = mode == 2 ? clientService.searchByContact(query) : clientService.searchByName(query);
        printClients(result);
    }

    private void printClients(List<Client> clients) {
        if (clients.isEmpty()) {
            System.out.println("Ничего не найдено.");
            return;
        }
        clients.forEach(System.out::println);
    }

    // =================================================================
    // Занятия
    // =================================================================

    private void classesMenu() {
        boolean inMenu = true;
        while (inMenu) {
            System.out.println();
            System.out.println("--- Занятия ---");
            System.out.println("1. Добавить занятие");
            System.out.println("2. Показать все занятия");
            System.out.println("3. Найти занятие по ID");
            System.out.println("4. Изменить занятие");
            System.out.println("5. Удалить занятие");
            System.out.println("0. Назад");

            try {
                switch (input.readInt("Выберите пункт меню: ")) {
                    case 1 -> createClass();
                    case 2 -> printClasses(classService.findAll());
                    case 3 -> printClasses(List.of(classService.findById(input.readLong("ID занятия: "))));
                    case 4 -> updateClass();
                    case 5 -> {
                        classService.delete(input.readLong("ID занятия для удаления: "));
                        System.out.println("Занятие удалено.");
                    }
                    case 0 -> inMenu = false;
                    default -> System.out.println("Нет такого пункта меню.");
                }
            } catch (DanceStudioException e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }

    private void createClass() {
        String title = input.readLine("Название занятия: ");
        DanceStyle style = input.readEnum("Стиль танца:", DanceStyle.class);
        DanceLevel level = input.readEnum("Уровень подготовки:", DanceLevel.class);
        String instructor = input.readLine("Преподаватель: ");
        LocalDateTime startTime = input.readDateTime("Дата и время начала");
        int duration = input.readInt("Длительность в минутах: ");
        int capacity = input.readInt("Вместимость (сколько человек): ");
        int minAge = input.readInt("Минимальный возраст: ");
        var price = input.readPrice("Цена занятия: ");

        DanceClass danceClass = classService.create(title, style, level, instructor, startTime, duration, capacity, minAge, price);
        System.out.println("Занятие добавлено: " + danceClass);
    }

    private void updateClass() {
        long id = input.readLong("ID занятия: ");
        System.out.println("Текущие данные: " + classService.findById(id));

        String title = input.readLine("Новое название: ");
        DanceStyle style = input.readEnum("Стиль танца:", DanceStyle.class);
        DanceLevel level = input.readEnum("Уровень подготовки:", DanceLevel.class);
        String instructor = input.readLine("Преподаватель: ");
        LocalDateTime startTime = input.readDateTime("Дата и время начала");
        int duration = input.readInt("Длительность в минутах: ");
        int capacity = input.readInt("Вместимость: ");
        int minAge = input.readInt("Минимальный возраст: ");
        var price = input.readPrice("Цена: ");

        classService.update(id, title, style, level, instructor, startTime, duration, capacity, minAge, price);
        System.out.println("Занятие обновлено.");
    }

    private void printClasses(List<DanceClass> classes) {
        if (classes.isEmpty()) {
            System.out.println("Ничего не найдено.");
            return;
        }
        classes.forEach(System.out::println);
    }

    // =================================================================
    // Записи на занятия (основная сущность)
    // =================================================================

    private void enrollmentsMenu() {
        boolean inMenu = true;
        while (inMenu) {
            System.out.println();
            System.out.println("--- Записи на занятия ---");
            System.out.println("1. Создать запись");
            System.out.println("2. Показать все записи");
            System.out.println("3. Найти запись по ID");
            System.out.println("4. Изменить статус записи");
            System.out.println("5. Отметить запись оплаченной");
            System.out.println("6. Удалить запись");
            System.out.println("7. Поиск записей (по клиенту или по занятию)");
            System.out.println("8. Фильтрация записей (по статусу или по оплате)");
            System.out.println("9. Сортировка записей (по дате или по цене)");
            System.out.println("0. Назад");

            try {
                switch (input.readInt("Выберите пункт меню: ")) {
                    case 1 -> createEnrollment();
                    case 2 -> printEnrollments(enrollmentService.findAll());
                    case 3 -> printEnrollments(List.of(enrollmentService.findById(input.readLong("ID записи: "))));
                    case 4 -> changeStatus();
                    case 5 -> {
                        enrollmentService.markPaid(input.readLong("ID записи: "));
                        System.out.println("Запись отмечена как оплаченная.");
                    }
                    case 6 -> {
                        enrollmentService.delete(input.readLong("ID записи для удаления: "));
                        System.out.println("Запись удалена.");
                    }
                    case 7 -> searchEnrollments();
                    case 8 -> filterEnrollments();
                    case 9 -> sortEnrollments();
                    case 0 -> inMenu = false;
                    default -> System.out.println("Нет такого пункта меню.");
                }
            } catch (DanceStudioException e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }

    private void createEnrollment() {
        long clientId = input.readLong("ID клиента: ");
        long classId = input.readLong("ID занятия: ");
        String note = input.readLine("Комментарий (можно оставить пустым): ");
        Enrollment enrollment = enrollmentService.create(clientId, classId, note.isBlank() ? null : note);
        System.out.println("Запись создана: " + enrollment);
    }

    private void changeStatus() {
        long id = input.readLong("ID записи: ");
        EnrollmentStatus status = input.readEnum("Новый статус:", EnrollmentStatus.class);
        enrollmentService.changeStatus(id, status);
        System.out.println("Статус изменён.");
    }

    private void searchEnrollments() {
        System.out.println("1. По имени клиента   2. По названию занятия");
        int mode = input.readInt("Способ поиска: ");
        String query = input.readLine("Что искать: ");
        List<Enrollment> result = mode == 2
                ? enrollmentService.searchByClassTitle(query)
                : enrollmentService.searchByClientName(query);
        printEnrollments(result);
    }

    private void filterEnrollments() {
        System.out.println("1. По статусу   2. По оплате");
        int mode = input.readInt("Способ фильтрации: ");
        List<Enrollment> result = mode == 2
                ? enrollmentService.filterByPaid(input.readYesNo("Показать только оплаченные?"))
                : enrollmentService.filterByStatus(input.readEnum("Статус:", EnrollmentStatus.class));
        printEnrollments(result);
    }

    private void sortEnrollments() {
        System.out.println("1. По дате создания   2. По цене");
        int mode = input.readInt("Способ сортировки: ");
        List<Enrollment> result = mode == 2 ? enrollmentService.sortByPrice() : enrollmentService.sortByDateCreated();
        printEnrollments(result);
    }

    private void printEnrollments(List<Enrollment> enrollments) {
        if (enrollments.isEmpty()) {
            System.out.println("Ничего не найдено.");
            return;
        }
        for (Enrollment enrollment : enrollments) {
            String clientName = describeClient(enrollment.getClientId());
            String className = describeClass(enrollment.getClassId());
            System.out.printf("#%d  %-25s -> %-30s  %-9s  цена %-8s оплачено: %-3s  %s%n",
                    enrollment.getId(), clientName, className, enrollment.getStatus(),
                    enrollment.getPrice(), enrollment.isPaid() ? "да" : "нет",
                    enrollment.getNote() == null ? "" : enrollment.getNote());
        }
    }

    /** Имя клиента по id для красивого вывода записи (вместо голого числа). */
    private String describeClient(long clientId) {
        try {
            return clientService.findById(clientId).getFullName();
        } catch (DanceStudioException e) {
            return "клиент #" + clientId;
        }
    }

    /** Название занятия по id для красивого вывода записи. */
    private String describeClass(long classId) {
        try {
            return classService.findById(classId).getTitle();
        } catch (DanceStudioException e) {
            return "занятие #" + classId;
        }
    }

    // =================================================================
    // Статистика, экспорт, таблицы целиком
    // =================================================================

    private void printStatistics() {
        Statistics stats = statisticsService.calculate();
        System.out.println();
        System.out.println("--- Статистика ---");
        System.out.println("Всего клиентов: " + stats.getTotalClients());
        System.out.println("Всего занятий: " + stats.getTotalClasses());
        System.out.println("Всего записей: " + stats.getTotalEnrollments());
        stats.getCountByStatus().forEach((status, count) -> System.out.println("  из них " + status + ": " + count));
        System.out.println("Общая выручка (по оплаченным записям): " + stats.getTotalRevenue());
        System.out.println("Средняя цена записи: " + stats.getAveragePrice());
    }

    private void exportToExcel() {
        String fileName = "dance_studio_" + LocalDateTime.now().format(FILE_TIMESTAMP) + ".xlsx";
        String path = exportService.exportToExcel(fileName);
        System.out.println("Данные выгружены в файл: " + path);
    }

    private void printAllTables() {
        System.out.println();
        System.out.println("--- Таблица clients ---");
        printClients(clientService.findAll());
        System.out.println();
        System.out.println("--- Таблица dance_classes ---");
        printClasses(classService.findAll());
        System.out.println();
        System.out.println("--- Таблица enrollments ---");
        printEnrollments(enrollmentService.findAll());
    }
}
