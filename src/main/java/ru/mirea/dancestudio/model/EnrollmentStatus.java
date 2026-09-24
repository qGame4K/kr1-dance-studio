package ru.mirea.dancestudio.model;

/**
 * Статус записи клиента на занятие.
 * Допустимые переходы между статусами (например, что из CREATED можно перейти
 * только в CONFIRMED или CANCELLED) проверяются в EnrollmentService — это одно
 * из бизнес-правил проекта.
 */
public enum EnrollmentStatus {
    /** Запись создана, ждёт оплаты. */
    CREATED,
    /** Оплачена и подтверждена — клиент придёт на занятие. */
    CONFIRMED,
    /** Занятие прошло, клиент его посетил. */
    COMPLETED,
    /** Запись отменена. */
    CANCELLED
}
