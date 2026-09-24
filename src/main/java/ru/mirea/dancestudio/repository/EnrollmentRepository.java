package ru.mirea.dancestudio.repository;

import ru.mirea.dancestudio.db.Database;
import ru.mirea.dancestudio.exception.DanceStudioException;
import ru.mirea.dancestudio.exception.NotFoundException;
import ru.mirea.dancestudio.model.Enrollment;
import ru.mirea.dancestudio.model.EnrollmentStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Читает и сохраняет записи на занятия в таблице enrollments (основная сущность). */
public class EnrollmentRepository implements Repository<Enrollment> {

    @Override
    public Enrollment save(Enrollment enrollment) {
        String sql = """
                INSERT INTO enrollments (client_id, class_id, status, price, paid, note, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = Database.connect();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setLong(1, enrollment.getClientId());
            statement.setLong(2, enrollment.getClassId());
            statement.setString(3, enrollment.getStatus().name());
            statement.setBigDecimal(4, enrollment.getPrice());
            statement.setBoolean(5, enrollment.isPaid());
            statement.setString(6, enrollment.getNote());
            statement.setObject(7, enrollment.getCreatedAt());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                keys.next();
                enrollment.setId(keys.getLong(1));
            }
            return enrollment;
        } catch (SQLException e) {
            throw new DanceStudioException("Не удалось сохранить запись: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Enrollment> findAll() {
        String sql = "SELECT * FROM enrollments ORDER BY id";
        List<Enrollment> result = new ArrayList<>();
        try (Connection connection = Database.connect();
             Statement statement = connection.createStatement();
             ResultSet rows = statement.executeQuery(sql)) {

            while (rows.next()) {
                result.add(map(rows));
            }
            return result;
        } catch (SQLException e) {
            throw new DanceStudioException("Не удалось прочитать записи: " + e.getMessage(), e);
        }
    }

    @Override
    public Enrollment findById(long id) {
        String sql = "SELECT * FROM enrollments WHERE id = ?";
        try (Connection connection = Database.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            try (ResultSet rows = statement.executeQuery()) {
                if (!rows.next()) {
                    throw new NotFoundException("Запись с id=" + id + " не найдена");
                }
                return map(rows);
            }
        } catch (SQLException e) {
            throw new DanceStudioException("Не удалось найти запись: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Enrollment enrollment) {
        String sql = "UPDATE enrollments SET status = ?, price = ?, paid = ?, note = ? WHERE id = ?";
        try (Connection connection = Database.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, enrollment.getStatus().name());
            statement.setBigDecimal(2, enrollment.getPrice());
            statement.setBoolean(3, enrollment.isPaid());
            statement.setString(4, enrollment.getNote());
            statement.setLong(5, enrollment.getId());

            if (statement.executeUpdate() == 0) {
                throw new NotFoundException("Запись с id=" + enrollment.getId() + " не найдена");
            }
        } catch (SQLException e) {
            throw new DanceStudioException("Не удалось обновить запись: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(long id) {
        String sql = "DELETE FROM enrollments WHERE id = ?";
        try (Connection connection = Database.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            if (statement.executeUpdate() == 0) {
                throw new NotFoundException("Запись с id=" + id + " не найдена");
            }
        } catch (SQLException e) {
            throw new DanceStudioException("Не удалось удалить запись: " + e.getMessage(), e);
        }
    }

    private Enrollment map(ResultSet rows) throws SQLException {
        return new Enrollment(
                rows.getLong("id"),
                rows.getLong("client_id"),
                rows.getLong("class_id"),
                EnrollmentStatus.valueOf(rows.getString("status")),
                rows.getBigDecimal("price"),
                rows.getBoolean("paid"),
                rows.getString("note"),
                rows.getObject("created_at", LocalDateTime.class)
        );
    }
}
