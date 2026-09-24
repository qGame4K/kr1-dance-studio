package ru.mirea.dancestudio.repository;

import ru.mirea.dancestudio.db.Database;
import ru.mirea.dancestudio.exception.DanceStudioException;
import ru.mirea.dancestudio.exception.NotFoundException;
import ru.mirea.dancestudio.model.Client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Читает и сохраняет клиентов в таблице clients.
 * Все запросы — ПАРАМЕТРИЗОВАННЫЕ (PreparedStatement с "?"), а не построены
 * склейкой строк. Это защита от SQL-инъекций: значения, которые ввёл
 * пользователь, никогда не попадают прямо в текст SQL-запроса.
 */
public class ClientRepository implements Repository<Client> {

    @Override
    public Client save(Client client) {
        String sql = """
                INSERT INTO clients (full_name, phone, email, birth_date, registered_at)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (Connection connection = Database.connect();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, client.getFullName());
            statement.setString(2, client.getPhone());
            statement.setString(3, client.getEmail());
            statement.setObject(4, client.getBirthDate());
            statement.setObject(5, client.getRegisteredAt());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                keys.next();
                client.setId(keys.getLong(1));
            }
            return client;
        } catch (SQLException e) {
            throw new DanceStudioException("Не удалось сохранить клиента: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Client> findAll() {
        String sql = "SELECT * FROM clients ORDER BY id";
        List<Client> result = new ArrayList<>();
        try (Connection connection = Database.connect();
             Statement statement = connection.createStatement();
             ResultSet rows = statement.executeQuery(sql)) {

            while (rows.next()) {
                result.add(map(rows));
            }
            return result;
        } catch (SQLException e) {
            throw new DanceStudioException("Не удалось прочитать клиентов: " + e.getMessage(), e);
        }
    }

    @Override
    public Client findById(long id) {
        String sql = "SELECT * FROM clients WHERE id = ?";
        try (Connection connection = Database.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            try (ResultSet rows = statement.executeQuery()) {
                if (!rows.next()) {
                    throw new NotFoundException("Клиент с id=" + id + " не найден");
                }
                return map(rows);
            }
        } catch (SQLException e) {
            throw new DanceStudioException("Не удалось найти клиента: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Client client) {
        String sql = """
                UPDATE clients SET full_name = ?, phone = ?, email = ?, birth_date = ?
                WHERE id = ?
                """;
        try (Connection connection = Database.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, client.getFullName());
            statement.setString(2, client.getPhone());
            statement.setString(3, client.getEmail());
            statement.setObject(4, client.getBirthDate());
            statement.setLong(5, client.getId());

            if (statement.executeUpdate() == 0) {
                throw new NotFoundException("Клиент с id=" + client.getId() + " не найден");
            }
        } catch (SQLException e) {
            throw new DanceStudioException("Не удалось обновить клиента: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(long id) {
        String sql = "DELETE FROM clients WHERE id = ?";
        try (Connection connection = Database.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            if (statement.executeUpdate() == 0) {
                throw new NotFoundException("Клиент с id=" + id + " не найден");
            }
        } catch (SQLException e) {
            throw new DanceStudioException(
                    "Не удалось удалить клиента (возможно, у него есть записи на занятия): " + e.getMessage(), e);
        }
    }

    /** Превращает одну строку результата SQL-запроса в объект Client. */
    private Client map(ResultSet rows) throws SQLException {
        return new Client(
                rows.getLong("id"),
                rows.getString("full_name"),
                rows.getString("phone"),
                rows.getString("email"),
                rows.getObject("birth_date", LocalDate.class),
                rows.getObject("registered_at", LocalDateTime.class)
        );
    }
}
