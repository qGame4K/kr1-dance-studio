package ru.mirea.dancestudio.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Занятие в расписании студии — например, «Сальса: базовый курс», вторник в 20:00.
 * Вторая сущность предметной области (наряду с Client), с которой связана
 * основная сущность варианта — Enrollment.
 */
public class DanceClass {

    private long id;
    private String title;
    private DanceStyle style;
    private DanceLevel level;
    private String instructor;
    private LocalDateTime startTime;
    private int durationMinutes;
    private int capacity;
    private int minAge;
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

    @Override
    public String toString() {
        return "#%d  %-30s  %-12s %-12s  преп. %-18s  %s  %d мин  мест: %d  мин.возраст: %d  цена: %s"
                .formatted(id, title, style, level, instructor, startTime, durationMinutes, capacity, minAge, price);
    }
}
