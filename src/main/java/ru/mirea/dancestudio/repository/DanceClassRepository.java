package ru.mirea.dancestudio.repository;

import ru.mirea.dancestudio.db.Database;
import ru.mirea.dancestudio.exception.DanceStudioException;
import ru.mirea.dancestudio.exception.NotFoundException;
import ru.mirea.dancestudio.model.DanceClass;
import ru.mirea.dancestudio.model.DanceLevel;
import ru.mirea.dancestudio.model.DanceStyle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Читает и сохраняет занятия в таблице dance_classes. */
public class DanceClassRepository implements Repository<DanceClass> {

    @Override
    public DanceClass save(DanceClass danceClass) {
        String sql = """
                INSERT INTO dance_classes
                    (title, style, level, instructor, start_time, duration_minutes, capacity, min_age, price)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = Database.connect();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            fillParameters(statement, danceClass);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                keys.next();
                danceClass.setId(keys.getLong(1));
            }
            return danceClass;
        } catch (SQLException e) {
            throw new DanceStudioException("Не удалось сохранить занятие: " + e.getMessage(), e);
        }
    }

    @Override
    public List<DanceClass> findAll() {
        String sql = "SELECT * FROM dance_classes ORDER BY id";
        List<DanceClass> result = new ArrayList<>();
        try (Connection connection = Database.connect();
             Statement statement = connection.createStatement();
             ResultSet rows = statement.executeQuery(sql)) {

            while (rows.next()) {
                result.add(map(rows));
            }
            return result;
        } catch (SQLException e) {
            throw new DanceStudioException("Не удалось прочитать занятия: " + e.getMessage(), e);
        }
    }

    @Override
    public DanceClass findById(long id) {
        String sql = "SELECT * FROM dance_classes WHERE id = ?";
        try (Connection connection = Database.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            try (ResultSet rows = statement.executeQuery()) {
                if (!rows.next()) {
                    throw new NotFoundException("Занятие с id=" + id + " не найдено");
                }
                return map(rows);
            }
        } catch (SQLException e) {
            throw new DanceStudioException("Не удалось найти занятие: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(DanceClass danceClass) {
        String sql = """
                UPDATE dance_classes
                SET title = ?, style = ?, level = ?, instructor = ?, start_time = ?,
                    duration_minutes = ?, capacity = ?, min_age = ?, price = ?
                WHERE id = ?
                """;
        try (Connection connection = Database.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            fillParameters(statement, danceClass);
            statement.setLong(10, danceClass.getId());

            if (statement.executeUpdate() == 0) {
                throw new NotFoundException("Занятие с id=" + danceClass.getId() + " не найдено");
            }
        } catch (SQLException e) {
            throw new DanceStudioException("Не удалось обновить занятие: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(long id) {
        String sql = "DELETE FROM dance_classes WHERE id = ?";
        try (Connection connection = Database.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            if (statement.executeUpdate() == 0) {
                throw new NotFoundException("Занятие с id=" + id + " не найдено");
            }
        } catch (SQLException e) {
            throw new DanceStudioException(
                    "Не удалось удалить занятие (возможно, на него есть записи клиентов): " + e.getMessage(), e);
        }
    }

    private void fillParameters(PreparedStatement statement, DanceClass danceClass) throws SQLException {
        statement.setString(1, danceClass.getTitle());
        statement.setString(2, danceClass.getStyle().name());
        statement.setString(3, danceClass.getLevel().name());
        statement.setString(4, danceClass.getInstructor());
        statement.setObject(5, danceClass.getStartTime());
        statement.setInt(6, danceClass.getDurationMinutes());
        statement.setInt(7, danceClass.getCapacity());
        statement.setInt(8, danceClass.getMinAge());
        statement.setBigDecimal(9, danceClass.getPrice());
    }

    private DanceClass map(ResultSet rows) throws SQLException {
        return new DanceClass(
                rows.getLong("id"),
                rows.getString("title"),
                DanceStyle.valueOf(rows.getString("style")),
                DanceLevel.valueOf(rows.getString("level")),
                rows.getString("instructor"),
                rows.getObject("start_time", LocalDateTime.class),
                rows.getInt("duration_minutes"),
                rows.getInt("capacity"),
                rows.getInt("min_age"),
                rows.getBigDecimal("price")
        );
    }
}
