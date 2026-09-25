package ru.mirea.dancestudio.model;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Набор показателей для пункта меню «Статистика».
 * Это просто контейнер данных (DTO): сам он ничего не считает, все числа
 * ему передаёт StatisticsService, который читает данные из всех трёх
 * репозиториев (клиенты, занятия, записи) и агрегирует их.
 *
 * Все поля final и без сеттеров — объект собирается один раз в конструкторе
 * и дальше не изменяется (иммутабельность), что логично для "снимка" статистики
 * на момент вызова.
 */
public class Statistics {

    // Общее количество клиентов в таблице clients.
    private final int totalClients;
    // Общее количество занятий в таблице dance_classes.
    private final int totalClasses;
    // Общее количество записей в таблице enrollments.
    private final int totalEnrollments;
    // Разбивка количества записей по каждому статусу (CREATED/CONFIRMED/COMPLETED/CANCELLED),
    // чтобы видеть, например, сколько записей ещё ожидает оплаты, а сколько отменено.
    private final Map<EnrollmentStatus, Long> countByStatus;
    // Суммарная выручка — сумма цен всех записей, отмеченных как оплаченные (paid = true).
    private final BigDecimal totalRevenue;
    // Средняя цена одной записи (по всем записям, не только оплаченным).
    private final BigDecimal averagePrice;

    public Statistics(int totalClients, int totalClasses, int totalEnrollments,
                       Map<EnrollmentStatus, Long> countByStatus,
                       BigDecimal totalRevenue, BigDecimal averagePrice) {
        this.totalClients = totalClients;
        this.totalClasses = totalClasses;
        this.totalEnrollments = totalEnrollments;
        this.countByStatus = countByStatus;
        this.totalRevenue = totalRevenue;
        this.averagePrice = averagePrice;
    }

    public int getTotalClients() {
        return totalClients;
    }

    public int getTotalClasses() {
        return totalClasses;
    }

    public int getTotalEnrollments() {
        return totalEnrollments;
    }

    public Map<EnrollmentStatus, Long> getCountByStatus() {
        return countByStatus;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public BigDecimal getAveragePrice() {
        return averagePrice;
    }
}
