package ru.mirea.dancestudio.repository;

import java.util.List;

/**
 * ЕДИНСТВЕННЫЙ ОБЩИЙ ИНТЕРФЕЙС проекта для CRUD-операций
 * (Create, Read, Update, Delete — «создать, прочитать, изменить, удалить»).
 *
 * Каждая сущность (Client, DanceClass, Enrollment) имеет свою реализацию —
 * ClientRepository, DanceClassRepository, EnrollmentRepository, — которая
 * сама решает, в какую таблицу и как именно писать SQL-запрос. Сервисы же
 * везде работают через тип Repository<T>, не зная, какая именно реализация
 * за ним стоит — это и есть полиморфизм.
 *
 * @param <T> тип сущности, например Client
 */
public interface Repository<T> {

    /** Сохраняет новую запись в базе данных и возвращает её же с присвоенным id. */
    T save(T entity);

    /** Возвращает все записи таблицы. */
    List<T> findAll();

    /** Возвращает запись по id или бросает NotFoundException, если такой нет. */
    T findById(long id);

    /** Обновляет уже существующую запись (ищет по id внутри объекта entity). */
    void update(T entity);

    /** Удаляет запись по id. */
    void delete(long id);
}
