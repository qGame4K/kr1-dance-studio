package ru.mirea.dancestudio.service;

import ru.mirea.dancestudio.exception.ValidationException;
import ru.mirea.dancestudio.model.Client;
import ru.mirea.dancestudio.model.DanceClass;
import ru.mirea.dancestudio.model.Enrollment;
import ru.mirea.dancestudio.model.EnrollmentStatus;
import ru.mirea.dancestudio.repository.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Бизнес-логика записи клиентов на занятия — это сердце проекта, основная сущность.
 * Здесь собраны все правила, которые нельзя доверить одному только интерфейсу:
 *
 *   1. клиент должен существовать;
 *   2. занятие должно существовать;
 *   3. клиент не может быть младше минимального возраста занятия;
 *   4. клиент не может дважды записаться на одно и то же занятие;
 *   5. нельзя записаться, если свободных мест не осталось;
 *   6. нельзя перевести запись в статус, который не разрешён из текущего
 *      (например, из CANCELLED никуда, а из CREATED — только в CONFIRMED или CANCELLED);
 *   7. нельзя подтвердить или завершить запись, если она не оплачена.
 */
public class EnrollmentService {

    // Разрешённые переходы статуса: из какого статуса в какие можно перейти.
    private static final Map<EnrollmentStatus, Set<EnrollmentStatus>> ALLOWED_TRANSITIONS = Map.of(
            EnrollmentStatus.CREATED, Set.of(EnrollmentStatus.CONFIRMED, EnrollmentStatus.CANCELLED),
            EnrollmentStatus.CONFIRMED, Set.of(EnrollmentStatus.COMPLETED, EnrollmentStatus.CANCELLED),
            EnrollmentStatus.COMPLETED, Set.of(),
            EnrollmentStatus.CANCELLED, Set.of()
    );

    private final Repository<Enrollment> enrollmentRepository;
    private final Repository<Client> clientRepository;
    private final Repository<DanceClass> classRepository;

    public EnrollmentService(Repository<Enrollment> enrollmentRepository,
                              Repository<Client> clientRepository,
                              Repository<DanceClass> classRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.clientRepository = clientRepository;
        this.classRepository = classRepository;
    }

    /** Создание новой записи — здесь проверяются правила 1-5. */
    public Enrollment create(long clientId, long classId, String note) {
        Client client = clientRepository.findById(clientId);        // правило 1: клиент должен существовать
        DanceClass danceClass = classRepository.findById(classId);  // правило 2: занятие должно существовать

        if (client.getAge() < danceClass.getMinAge()) {              // правило 3: минимальный возраст
            throw new ValidationException("Клиенту %d лет, а минимальный возраст для занятия — %d"
                    .formatted(client.getAge(), danceClass.getMinAge()));
        }

        List<Enrollment> active = activeEnrollmentsFor(classId);

        boolean alreadyEnrolled = active.stream().anyMatch(e -> e.getClientId() == clientId);
        if (alreadyEnrolled) {                                        // правило 4: не записываться дважды
            throw new ValidationException("Клиент уже записан на это занятие");
        }

        if (active.size() >= danceClass.getCapacity()) {              // правило 5: свободные места
            throw new ValidationException("Свободных мест нет (вместимость: %d)".formatted(danceClass.getCapacity()));
        }

        Enrollment enrollment = new Enrollment(clientId, classId, danceClass.getPrice(), note);
        return enrollmentRepository.save(enrollment);
    }

    /** Изменение статуса записи — правила 6 и 7. */
    public void changeStatus(long enrollmentId, EnrollmentStatus newStatus) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId);

        if (!ALLOWED_TRANSITIONS.get(enrollment.getStatus()).contains(newStatus)) {
            throw new ValidationException("Нельзя перевести запись из статуса %s в %s"
                    .formatted(enrollment.getStatus(), newStatus));
        }
        boolean mustBePaid = newStatus == EnrollmentStatus.CONFIRMED || newStatus == EnrollmentStatus.COMPLETED;
        if (mustBePaid && !enrollment.isPaid()) {
            throw new ValidationException("Нельзя подтвердить или завершить неоплаченную запись");
        }

        enrollment.setStatus(newStatus);
        enrollmentRepository.update(enrollment);
    }

    public void markPaid(long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId);
        enrollment.setPaid(true);
        enrollmentRepository.update(enrollment);
    }

    public void delete(long enrollmentId) {
        enrollmentRepository.delete(enrollmentId);
    }

    public List<Enrollment> findAll() {
        return enrollmentRepository.findAll();
    }

    public Enrollment findById(long id) {
        return enrollmentRepository.findById(id);
    }

    // ---------------------------------------------------------------
    // Поиск (два способа)
    // ---------------------------------------------------------------

    /** Поиск по имени клиента — способ №1. */
    public List<Enrollment> searchByClientName(String query) {
        Set<Long> matchingClientIds = clientRepository.findAll().stream()
                .filter(client -> client.getFullName().toLowerCase().contains(query.toLowerCase()))
                .map(Client::getId)
                .collect(Collectors.toSet());
        return enrollmentRepository.findAll().stream()
                .filter(e -> matchingClientIds.contains(e.getClientId()))
                .toList();
    }

    /** Поиск по названию занятия — способ №2. */
    public List<Enrollment> searchByClassTitle(String query) {
        Set<Long> matchingClassIds = classRepository.findAll().stream()
                .filter(danceClass -> danceClass.getTitle().toLowerCase().contains(query.toLowerCase()))
                .map(DanceClass::getId)
                .collect(Collectors.toSet());
        return enrollmentRepository.findAll().stream()
                .filter(e -> matchingClassIds.contains(e.getClassId()))
                .toList();
    }

    // ---------------------------------------------------------------
    // Фильтрация (два способа)
    // ---------------------------------------------------------------

    /** Фильтр по статусу — способ №1. */
    public List<Enrollment> filterByStatus(EnrollmentStatus status) {
        return enrollmentRepository.findAll().stream()
                .filter(e -> e.getStatus() == status)
                .toList();
    }

    /** Фильтр по признаку оплаты — способ №2. */
    public List<Enrollment> filterByPaid(boolean paid) {
        return enrollmentRepository.findAll().stream()
                .filter(e -> e.isPaid() == paid)
                .toList();
    }

    // ---------------------------------------------------------------
    // Сортировка (два способа)
    // ---------------------------------------------------------------

    /** Сортировка по дате создания — способ №1. */
    public List<Enrollment> sortByDateCreated() {
        return enrollmentRepository.findAll().stream()
                .sorted(Comparator.comparing(Enrollment::getCreatedAt))
                .toList();
    }

    /** Сортировка по цене — способ №2. */
    public List<Enrollment> sortByPrice() {
        return enrollmentRepository.findAll().stream()
                .sorted(Comparator.comparing(Enrollment::getPrice))
                .toList();
    }

    /** Все активные (не отменённые) записи на конкретное занятие — нужно для проверки мест и повторной записи. */
    private List<Enrollment> activeEnrollmentsFor(long classId) {
        return enrollmentRepository.findAll().stream()
                .filter(e -> e.getClassId() == classId && e.getStatus() != EnrollmentStatus.CANCELLED)
                .toList();
    }
}
