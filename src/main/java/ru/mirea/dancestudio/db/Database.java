package ru.mirea.dancestudio.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Отвечает за подключение к базе данных PostgreSQL.
 *
 * Параметры подключения берутся из переменных окружения DB_URL / DB_USER / DB_PASSWORD
 * (их задаёт docker-compose.yml для запуска в Docker), а если переменные не заданы —
 * используются значения по умолчанию для локального запуска без Docker.
 */
public final class Database {

    private static final String URL = env("DB_URL", "jdbc:postgresql://localhost:5432/dance_studio");
    private static final String USER = env("DB_USER", "postgres");
    private static final String PASSWORD = env("DB_PASSWORD", "postgres");

    private Database() {
        // утилитный класс, объекты создавать не нужно
    }

    private static String env(String name, String defaultValue) {
        String value = System.getenv(name);
        return (value == null || value.isBlank()) ? defaultValue : value;
    }

    /**
     * Открывает новое соединение с базой данных.
     * Закрыть его обязан вызывающий код — для этого везде используется
     * try-with-resources, соединение закрывается автоматически.
     */
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Несколько попыток подключения с паузой между ними.
     * Нужно при запуске в Docker: контейнер с программой может стартовать
     * на пару секунд раньше, чем база данных будет готова принимать запросы.
     */
    public static void waitUntilReady(int attempts, long delayMillis) throws InterruptedException {
        for (int attempt = 1; attempt <= attempts; attempt++) {
            try (Connection ignored = connect()) {
                return;
            } catch (SQLException e) {
                System.out.println("База данных ещё не готова, ждём... (" + attempt + "/" + attempts + ")");
                Thread.sleep(delayMillis);
            }
        }
        throw new IllegalStateException("Не удалось подключиться к базе данных по адресу " + URL);
    }
}
