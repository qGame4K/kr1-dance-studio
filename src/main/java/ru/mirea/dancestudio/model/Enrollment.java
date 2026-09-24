package ru.mirea.dancestudio.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ОСНОВНАЯ СУЩНОСТЬ проекта — запись клиента на занятие.
 * Связывает клиента (clientId) и занятие (classId): именно эта связь
 * "клиент N --- N занятие" через таблицу enrollments и есть та самая
 * связь между двумя таблицами, которая требуется по заданию.
 */
public class Enrollment {

    private long id;
    private long clientId;
    private long classId;
    private EnrollmentStatus status;
    private BigDecimal price;
    private boolean paid;
    private String note;
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

    @Override
    public String toString() {
        return "#%d  клиент #%d  занятие #%d  %-9s  цена %-8s оплачено: %-3s  %s"
                .formatted(id, clientId, classId, status, price, paid ? "да" : "нет",
                        note == null ? "" : note);
    }
}
