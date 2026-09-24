package ru.mirea.dancestudio.model;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Набор показателей для пункта меню «Статистика».
 * Это просто контейнер данных: сам он ничего не считает, все числа
 * ему передаёт StatisticsService.
 */
public class Statistics {

    private final int totalClients;
    private final int totalClasses;
    private final int totalEnrollments;
    private final Map<EnrollmentStatus, Long> countByStatus;
    private final BigDecimal totalRevenue;
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
