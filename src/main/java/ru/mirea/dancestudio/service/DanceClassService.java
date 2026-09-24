package ru.mirea.dancestudio.service;

import ru.mirea.dancestudio.exception.ValidationException;
import ru.mirea.dancestudio.model.DanceClass;
import ru.mirea.dancestudio.model.DanceLevel;
import ru.mirea.dancestudio.model.DanceStyle;
import ru.mirea.dancestudio.repository.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** Проверка данных занятия (валидация) и доступ к его CRUD-операциям. */
public class DanceClassService {

    private final Repository<DanceClass> classRepository;

    public DanceClassService(Repository<DanceClass> classRepository) {
        this.classRepository = classRepository;
    }

    public DanceClass create(String title, DanceStyle style, DanceLevel level, String instructor,
                              LocalDateTime startTime, int durationMinutes, int capacity, int minAge, BigDecimal price) {
        validate(title, instructor, durationMinutes, capacity, minAge, price);
        return classRepository.save(
                new DanceClass(title, style, level, instructor, startTime, durationMinutes, capacity, minAge, price));
    }

    public void update(long id, String title, DanceStyle style, DanceLevel level, String instructor,
                        LocalDateTime startTime, int durationMinutes, int capacity, int minAge, BigDecimal price) {
        validate(title, instructor, durationMinutes, capacity, minAge, price);
        DanceClass danceClass = classRepository.findById(id);
        danceClass.setTitle(title);
        danceClass.setStyle(style);
        danceClass.setLevel(level);
        danceClass.setInstructor(instructor);
        danceClass.setStartTime(startTime);
        danceClass.setDurationMinutes(durationMinutes);
        danceClass.setCapacity(capacity);
        danceClass.setMinAge(minAge);
        danceClass.setPrice(price);
        classRepository.update(danceClass);
    }

    public void delete(long id) {
        classRepository.delete(id);
    }

    public DanceClass findById(long id) {
        return classRepository.findById(id);
    }

    public List<DanceClass> findAll() {
        return classRepository.findAll();
    }

    private void validate(String title, String instructor, int durationMinutes, int capacity, int minAge, BigDecimal price) {
        if (title == null || title.trim().length() < 2) {
            throw new ValidationException("Название занятия должно содержать минимум 2 символа");
        }
        if (instructor == null || instructor.trim().length() < 2) {
            throw new ValidationException("Имя преподавателя должно содержать минимум 2 символа");
        }
        if (durationMinutes < 15 || durationMinutes > 240) {
            throw new ValidationException("Длительность занятия должна быть от 15 до 240 минут");
        }
        if (capacity < 1 || capacity > 100) {
            throw new ValidationException("Вместимость должна быть от 1 до 100 человек");
        }
        if (minAge < 0 || minAge > 99) {
            throw new ValidationException("Минимальный возраст должен быть от 0 до 99");
        }
        if (price == null || price.signum() < 0) {
            throw new ValidationException("Цена не может быть отрицательной");
        }
    }
}
