package ru.mirea.dancestudio.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Занятие в расписании студии — например, «Сальса: базовый курс», вторник в 20:00.
 * Вторая сущность предметной области (наряду с Client), с которой связана
 * основная сущность варианта — Enrollment.
 *
 * Хранится в таблице БД dance_classes. С таблицей enrollments связана
 * "один ко многим": на одно занятие может быть записано много клиентов
 * (но не больше, чем capacity — это правило проверяет EnrollmentService).
 */
public class DanceClass {

    // Первичный ключ в таблице dance_classes. У ещё не сохранённого занятия равен 0.
    private long id;
    // Название занятия, которое видит клиент, например "Сальса: базовый курс".
    private String title;
    // Стиль танца (enum DanceStyle) — фиксированный список, чтобы не было опечаток в БД.
    private DanceStyle style;
    // Уровень подготовки, на который рассчитано занятие (enum DanceLevel).
    private DanceLevel level;
    // ФИО или имя преподавателя, который ведёт занятие.
    private String instructor;
    // Дата и время начала конкретного занятия в расписании.
    private LocalDateTime startTime;
    // Длительность занятия в минутах (например, 60).
    private int durationMinutes;
    // Вместимость зала — максимальное число клиентов, которые могут записаться.
    // EnrollmentService сверяет с ней текущее число активных записей перед созданием новой.
    private int capacity;
    // Минимальный возраст, с которого допускается запись на занятие.
    // Сверяется с Client.getAge() при создании записи (Enrollment).
    private int minAge;
    // Стоимость одного посещения. BigDecimal, а не double/float — чтобы избежать
    // ошибок округления при работе с деньгами.
    private BigDecimal price;

    /** Конструктор для нового занятия — id присвоит база данных. */
    public DanceClass(String title, DanceStyle style, DanceLevel level, String instructor,
                       LocalDateTime startTime, int durationMinutes, int capacity, int minAge, BigDecimal price) {
        this(0, title, style, level, instructor, startTime, durationMinutes, capacity, minAge, price);
    }

    /** Конструктор для занятия, прочитанного из базы данных. */
    public DanceClass(long id, String title, DanceStyle style, DanceLevel level, String instructor,
                       LocalDateTime startTime, int durationMinutes, int capacity, int minAge, BigDecimal price) {
        this.id = id;
        this.title = title;
        this.style = style;
        this.level = level;
        this.instructor = instructor;
        this.startTime = startTime;
        this.durationMinutes = durationMinutes;
        this.capacity = capacity;
        this.minAge = minAge;
        this.price = price;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public DanceStyle getStyle() {
        return style;
    }

    public void setStyle(DanceStyle style) {
        this.style = style;
    }

    public DanceLevel getLevel() {
        return level;
    }

    public void setLevel(DanceLevel level) {
        this.level = level;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getMinAge() {
        return minAge;
    }

    public void setMinAge(int minAge) {
        this.minAge = minAge;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /** Удобочитаемое строковое представление для вывода в консоли (списки, поиск и т.д.). */
    @Override
    public String toString() {
        return "#%d  %-30s  %-12s %-12s  преп. %-18s  %s  %d мин  мест: %d  мин.возраст: %d  цена: %s"
                .formatted(id, title, style, level, instructor, startTime, durationMinutes, capacity, minAge, price);
    }
}
