package ru.mirea.dancestudio.service;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ru.mirea.dancestudio.exception.DanceStudioException;
import ru.mirea.dancestudio.model.Client;
import ru.mirea.dancestudio.model.DanceClass;
import ru.mirea.dancestudio.model.Enrollment;
import ru.mirea.dancestudio.repository.Repository;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Выгружает данные студии в один Excel-файл (.xlsx) — по листу на таблицу:
 * «Клиенты», «Занятия», «Записи». Используется библиотека Apache POI.
 */
public class ExportService {

    private final Repository<Client> clientRepository;
    private final Repository<DanceClass> classRepository;
    private final Repository<Enrollment> enrollmentRepository;

    public ExportService(Repository<Client> clientRepository,
                          Repository<DanceClass> classRepository,
                          Repository<Enrollment> enrollmentRepository) {
        this.clientRepository = clientRepository;
        this.classRepository = classRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    /** Создаёт файл export/&lt;fileName&gt; и возвращает полный путь к нему. */
    public String exportToExcel(String fileName) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            writeClients(workbook.createSheet("Клиенты"));
            writeClasses(workbook.createSheet("Занятия"));
            writeEnrollments(workbook.createSheet("Записи"));

            Path folder = Path.of("export");
            Files.createDirectories(folder);
            Path file = folder.resolve(fileName);

            try (FileOutputStream out = new FileOutputStream(file.toFile())) {
                workbook.write(out);
            }
            return file.toAbsolutePath().toString();
        } catch (IOException e) {
            throw new DanceStudioException("Не удалось экспортировать данные: " + e.getMessage(), e);
        }
    }

    private void writeClients(Sheet sheet) {
        String[] headers = {"ID", "ФИО", "Телефон", "Email", "Дата рождения", "Возраст"};
        writeHeader(sheet, headers);
        int rowNum = 1;
        for (Client client : clientRepository.findAll()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(client.getId());
            row.createCell(1).setCellValue(client.getFullName());
            row.createCell(2).setCellValue(client.getPhone());
            row.createCell(3).setCellValue(client.getEmail());
            row.createCell(4).setCellValue(client.getBirthDate().toString());
            row.createCell(5).setCellValue(client.getAge());
        }
        autoSizeColumns(sheet, headers.length);
    }

    private void writeClasses(Sheet sheet) {
        String[] headers = {"ID", "Название", "Стиль", "Уровень", "Преподаватель",
                "Начало", "Длительность, мин", "Вместимость", "Мин. возраст", "Цена"};
        writeHeader(sheet, headers);
        int rowNum = 1;
        for (DanceClass danceClass : classRepository.findAll()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(danceClass.getId());
            row.createCell(1).setCellValue(danceClass.getTitle());
            row.createCell(2).setCellValue(danceClass.getStyle().name());
            row.createCell(3).setCellValue(danceClass.getLevel().name());
            row.createCell(4).setCellValue(danceClass.getInstructor());
            row.createCell(5).setCellValue(danceClass.getStartTime().toString());
            row.createCell(6).setCellValue(danceClass.getDurationMinutes());
            row.createCell(7).setCellValue(danceClass.getCapacity());
            row.createCell(8).setCellValue(danceClass.getMinAge());
            row.createCell(9).setCellValue(danceClass.getPrice().doubleValue());
        }
        autoSizeColumns(sheet, headers.length);
    }

    private void writeEnrollments(Sheet sheet) {
        String[] headers = {"ID", "ID клиента", "ID занятия", "Статус", "Цена", "Оплачено", "Комментарий", "Создана"};
        writeHeader(sheet, headers);
        int rowNum = 1;
        for (Enrollment enrollment : enrollmentRepository.findAll()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(enrollment.getId());
            row.createCell(1).setCellValue(enrollment.getClientId());
            row.createCell(2).setCellValue(enrollment.getClassId());
            row.createCell(3).setCellValue(enrollment.getStatus().name());
            row.createCell(4).setCellValue(enrollment.getPrice().doubleValue());
            row.createCell(5).setCellValue(enrollment.isPaid() ? "да" : "нет");
            row.createCell(6).setCellValue(enrollment.getNote() == null ? "" : enrollment.getNote());
            row.createCell(7).setCellValue(enrollment.getCreatedAt().toString());
        }
        autoSizeColumns(sheet, headers.length);
    }

    private void writeHeader(Sheet sheet, String[] titles) {
        Row row = sheet.createRow(0);
        for (int i = 0; i < titles.length; i++) {
            row.createCell(i).setCellValue(titles[i]);
        }
    }

    private void autoSizeColumns(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}
