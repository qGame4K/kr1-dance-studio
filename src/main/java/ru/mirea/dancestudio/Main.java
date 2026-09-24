package ru.mirea.dancestudio;

import ru.mirea.dancestudio.db.Database;
import ru.mirea.dancestudio.repository.ClientRepository;
import ru.mirea.dancestudio.repository.DanceClassRepository;
import ru.mirea.dancestudio.repository.EnrollmentRepository;
import ru.mirea.dancestudio.service.ClientService;
import ru.mirea.dancestudio.service.DanceClassService;
import ru.mirea.dancestudio.service.EnrollmentService;
import ru.mirea.dancestudio.service.ExportService;
import ru.mirea.dancestudio.service.StatisticsService;
import ru.mirea.dancestudio.ui.ConsoleApp;
import ru.mirea.dancestudio.ui.ConsoleInput;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Точка входа в программу.
 * Здесь и только здесь создаются все объекты и связываются друг с другом
 * (репозитории -> сервисы -> консольное меню) — это называется "сборка
 * зависимостей вручную": никакого скрытого волшебства, всё видно построчно.
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {
        // Явно печатаем в UTF-8, чтобы русский текст корректно отображался
        // и в Docker-контейнере, и в консоли Windows.
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        System.out.println("Подключаемся к базе данных...");
        Database.waitUntilReady(20, 1000);

        ClientRepository clientRepository = new ClientRepository();
        DanceClassRepository classRepository = new DanceClassRepository();
        EnrollmentRepository enrollmentRepository = new EnrollmentRepository();

        ClientService clientService = new ClientService(clientRepository);
        DanceClassService classService = new DanceClassService(classRepository);
        EnrollmentService enrollmentService =
                new EnrollmentService(enrollmentRepository, clientRepository, classRepository);
        StatisticsService statisticsService =
                new StatisticsService(clientRepository, classRepository, enrollmentRepository);
        ExportService exportService =
                new ExportService(clientRepository, classRepository, enrollmentRepository);

        ConsoleInput input = new ConsoleInput(new Scanner(System.in, StandardCharsets.UTF_8));
        ConsoleApp app = new ConsoleApp(clientService, classService, enrollmentService,
                statisticsService, exportService, input);
        app.run();
    }
}
