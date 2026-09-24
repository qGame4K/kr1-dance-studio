package ru.mirea.dancestudio.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

/**
 * Клиент студии — человек, который записывается на занятия.
 * Это и есть та обязательная сущность "пользователь системы" из задания.
 *
 * Поля private — это инкапсуляция: снаружи класса нельзя напрямую написать
 * client.fullName = "..."; изменить данные можно только через методы
 * (геттеры/сеттеры), которые в сервисах ещё и проверяются на корректность.
 */
public class Client {

    private long id;
    private String fullName;
    private String phone;
    private String email;
    private LocalDate birthDate;
    private final LocalDateTime registeredAt;

    /** Конструктор для нового клиента: id ещё нет — его присвоит база данных при сохранении. */
    public Client(String fullName, String phone, String email, LocalDate birthDate) {
        this(0, fullName, phone, email, birthDate, LocalDateTime.now());
    }

    /** Конструктор для клиента, уже прочитанного из базы данных — все поля уже известны. */
    public Client(long id, String fullName, String phone, String email,
                  LocalDate birthDate, LocalDateTime registeredAt) {
        this.id = id;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.birthDate = birthDate;
        this.registeredAt = registeredAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    /** Возраст клиента на сегодняшний день (полных лет). Используется в бизнес-правиле про минимальный возраст. */
    public int getAge() {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    @Override
    public String toString() {
        return "#%d  %-30s  тел. %-15s  %-28s  возраст %d"
                .formatted(id, fullName, phone, email, getAge());
    }
}
