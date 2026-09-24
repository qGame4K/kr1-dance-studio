package ru.mirea.dancestudio.service;

import ru.mirea.dancestudio.model.Client;
import ru.mirea.dancestudio.model.DanceClass;
import ru.mirea.dancestudio.model.Enrollment;
import ru.mirea.dancestudio.model.EnrollmentStatus;
import ru.mirea.dancestudio.model.Statistics;
import ru.mirea.dancestudio.repository.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/** Считает показатели по данным студии для пункта меню «Статистика». */
public class StatisticsService {

    private final Repository<Client> clientRepository;
    private final Repository<DanceClass> classRepository;
    private final Repository<Enrollment> enrollmentRepository;

    public StatisticsService(Repository<Client> clientRepository,
                              Repository<DanceClass> classRepository,
                              Repository<Enrollment> enrollmentRepository) {
        this.clientRepository = clientRepository;
        this.classRepository = classRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    public Statistics calculate() {
        List<Enrollment> enrollments = enrollmentRepository.findAll();

        Map<EnrollmentStatus, Long> countByStatus = new EnumMap<>(EnrollmentStatus.class);
        for (EnrollmentStatus status : EnrollmentStatus.values()) {
            long count = enrollments.stream().filter(e -> e.getStatus() == status).count();
            countByStatus.put(status, count);
        }

        BigDecimal totalRevenue = enrollments.stream()
                .filter(Enrollment::isPaid)
                .map(Enrollment::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal averagePrice = BigDecimal.ZERO;
        if (!enrollments.isEmpty()) {
            BigDecimal totalPrice = enrollments.stream().map(Enrollment::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            averagePrice = totalPrice.divide(BigDecimal.valueOf(enrollments.size()), 2, RoundingMode.HALF_UP);
        }

        return new Statistics(
                clientRepository.findAll().size(),
                classRepository.findAll().size(),
                enrollments.size(),
                countByStatus,
                totalRevenue,
                averagePrice
        );
    }
}
