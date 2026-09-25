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
 *
 * Класс не хранит и не переиспользует одно соединение — каждый репозиторий сам
 * открывает Connection через connect() на время одного запроса и сразу закрывает
 * его (try-with-resources). Для консольного приложения с редкими запросами
 * этого достаточно, пул соединений (HikariCP и т.п.) здесь избыточен.
 */
public final class Database {

    // URL вида jdbc:postgresql://host:port/db_name — адрес базы данных для JDBC-драйвера.
    private static final String URL = env("DB_URL", "jdbc:postgresql://localhost:5432/dance_studio");
    // Имя пользователя PostgreSQL, от которого выполняются все запросы.
    private static final String USER = env("DB_USER", "postgres");
    // Пароль этого пользователя. В реальном продакшене хранить пароль в коде/переменных
    // окружения в открытом виде небезопасно, но для учебного проекта это приемлемо.
    private static final String PASSWORD = env("DB_PASSWORD", "postgres");

    private Database() {
        // утилитный класс, объекты создавать не нужно
    }

    /**
     * Читает переменную окружения по имени; если она не задана или пустая —
     * возвращает значение по умолчанию. Так один и тот же код работает
     * и локально (без Docker, с настройками "из коробки"), и в контейнере,
     * где docker-compose подставляет реальные значения через окружение.
     */
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
     *
     * @param attempts     сколько раз пробовать подключиться, прежде чем сдаться
     * @param delayMillis  пауза между попытками в миллисекундах
     * @throws InterruptedException если поток прервали во время ожидания (Thread.sleep)
     * @throws IllegalStateException если за все попытки подключиться так и не удалось —
     *         дальше запускать приложение бессмысленно, поэтому это RuntimeException,
     *         а не проверяемое исключение
     */
    public static void waitUntilReady(int attempts, long delayMillis) throws InterruptedException {
        for (int attempt = 1; attempt <= attempts; attempt++) {
            // Пробуем открыть и сразу закрыть соединение — это самый простой
            // способ проверить, что база данных уже приняла запрос.
            try (Connection ignored = connect()) {
                return;
            } catch (SQLException e) {
                System.out.println("База данных ещё не готова, ждём... (" + attempt + "/" + attempts + ")");
                Thread.sleep(delayMillis);
            }
        }
        // Все попытки исчерпаны — база так и не ответила. Дальше запускать
        // репозитории и сервисы бессмысленно, поэтому явно останавливаем программу.
        throw new IllegalStateException("Не удалось подключиться к базе данных по адресу " + URL);
    }
}
