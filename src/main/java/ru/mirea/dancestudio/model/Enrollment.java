package ru.mirea.dancestudio.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ОСНОВНАЯ СУЩНОСТЬ проекта — запись клиента на занятие.
 * Связывает клиента (clientId) и занятие (classId): именно эта связь
 * "клиент N --- N занятие" через таблицу enrollments и есть та самая
 * связь между двумя таблицами, которая требуется по заданию.
 *
 * Таблица enrollments хранит только id клиента и id занятия (внешние ключи),
 * а не сами объекты Client/DanceClass — это обычная нормализация в реляционной
 * БД. Поэтому в ConsoleApp есть отдельные методы describeClient/describeClass,
 * которые по id подтягивают человекочитаемое имя/название для вывода.
 */
public class Enrollment {

    // Первичный ключ в таблице enrollments. У ещё не сохранённой записи равен 0.
    private long id;
    // Внешний ключ на Client.id — кто записался.
    private long clientId;
    // Внешний ключ на DanceClass.id — на какое занятие записались.
    private long classId;
    // Текущий статус записи (enum EnrollmentStatus). Допустимые переходы между
    // статусами проверяются в EnrollmentService — прямо в модели правил нет,
    // модель лишь хранит текущее значение.
    private EnrollmentStatus status;
    // Цена записи. Фиксируется в момент создания (обычно равна цене занятия
    // на тот момент), поэтому дальнейшее изменение цены занятия не меняет
    // задним числом уже созданные записи.
    private BigDecimal price;
    // Признак оплаты записи: true — клиент оплатил, false — ещё нет.
    private boolean paid;
    // Необязательный комментарий к записи (например, пожелание клиента). Может быть null.
    private String note;
    // Дата и время создания записи. Поле final — не меняется после создания,
    // используется для сортировки записей "по дате создания".
    private final LocalDateTime createdAt;

    /** Конструктор для новой записи. Статус всегда начинается с CREATED — так безопаснее. */
    public Enrollment(long clientId, long classId, BigDecimal price, String note) {
        this(0, clientId, classId, EnrollmentStatus.CREATED, price, false, note, LocalDateTime.now());
    }

    /** Конструктор для записи, прочитанной из базы данных. */
    public Enrollment(long id, long clientId, long classId, EnrollmentStatus status,
                       BigDecimal price, boolean paid, String note, LocalDateTime createdAt) {
        this.id = id;
        this.clientId = clientId;
        this.classId = classId;
        this.status = status;
        this.price = price;
        this.paid = paid;
        this.note = note;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public long getClassId() {
        return classId;
    }

    public void setClassId(long classId) {
        this.classId = classId;
    }

    public EnrollmentStatus getStatus() {
        return status;
    }

    public void setStatus(EnrollmentStatus status) {
        this.status = status;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /** Удобочитаемое строковое представление для вывода в консоли (списки, поиск, фильтрация и т.д.). */
    @Override
    public String toString() {
        return "#%d  клиент #%d  занятие #%d  %-9s  цена %-8s оплачено: %-3s  %s"
                .formatted(id, clientId, classId, status, price, paid ? "да" : "нет",
                        note == null ? "" : note);
    }
}
