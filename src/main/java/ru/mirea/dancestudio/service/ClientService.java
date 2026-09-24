package ru.mirea.dancestudio.service;

import ru.mirea.dancestudio.exception.ValidationException;
import ru.mirea.dancestudio.model.Client;
import ru.mirea.dancestudio.repository.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Проверка данных клиента (валидация) и доступ к его CRUD-операциям.
 * UI (ConsoleApp) никогда не обращается к репозиторию напрямую — только через сервис,
 * поэтому проверки в validate() срабатывают всегда, откуда бы ни пришли данные.
 */
public class ClientService {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+\\d{11,15}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final Repository<Client> clientRepository;

    public ClientService(Repository<Client> clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Client create(String fullName, String phone, String email, LocalDate birthDate) {
        validate(fullName, phone, email, birthDate);
        return clientRepository.save(new Client(fullName, phone, email, birthDate));
    }

    public void update(long id, String fullName, String phone, String email, LocalDate birthDate) {
        validate(fullName, phone, email, birthDate);
        Client client = clientRepository.findById(id);
        client.setFullName(fullName);
        client.setPhone(phone);
        client.setEmail(email);
        client.setBirthDate(birthDate);
        clientRepository.update(client);
    }

    public void delete(long id) {
        clientRepository.delete(id);
    }

    public Client findById(long id) {
        return clientRepository.findById(id);
    }

    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    /** Поиск по имени (подстрока, без учёта регистра) — способ поиска №1. */
    public List<Client> searchByName(String query) {
        String needle = query.toLowerCase();
        return clientRepository.findAll().stream()
                .filter(client -> client.getFullName().toLowerCase().contains(needle))
                .toList();
    }

    /** Поиск по телефону или email — способ поиска №2. */
    public List<Client> searchByContact(String query) {
        String needle = query.toLowerCase();
        return clientRepository.findAll().stream()
                .filter(client -> client.getPhone().toLowerCase().contains(needle)
                        || client.getEmail().toLowerCase().contains(needle))
                .toList();
    }

    private void validate(String fullName, String phone, String email, LocalDate birthDate) {
        if (fullName == null || fullName.trim().length() < 2) {
            throw new ValidationException("Имя должно содержать минимум 2 символа");
        }
        if (phone == null || !PHONE_PATTERN.matcher(phone).matches()) {
            throw new ValidationException("Телефон должен быть в формате +79001234567");
        }
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidationException("Некорректный email");
        }
        if (birthDate == null || birthDate.isAfter(LocalDate.now()) || birthDate.isBefore(LocalDate.of(1900, 1, 1))) {
            throw new ValidationException("Некорректная дата рождения");
        }
    }
}
