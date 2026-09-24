package ru.mirea.dancestudio.ui;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/**
 * Читает данные, которые вводит пользователь в консоли, и следит,
 * чтобы программа не падала из-за некорректного ввода: если вместо числа
 * ввели текст, просто просим ввести значение ещё раз, а не останавливаем программу.
 */
public class ConsoleInput {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private final Scanner scanner;

    public ConsoleInput(Scanner scanner) {
        this.scanner = scanner;
    }

    public String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public long readLong(String prompt) {
        while (true) {
            String text = readLine(prompt);
            try {
                return Long.parseLong(text);
            } catch (NumberFormatException e) {
                System.out.println("Нужно ввести целое число, попробуйте снова.");
            }
        }
    }

    public int readInt(String prompt) {
        while (true) {
            String text = readLine(prompt);
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException e) {
                System.out.println("Нужно ввести целое число, попробуйте снова.");
            }
        }
    }

    public BigDecimal readPrice(String prompt) {
        while (true) {
            String text = readLine(prompt).replace(",", ".");
            try {
                return new BigDecimal(text);
            } catch (NumberFormatException e) {
                System.out.println("Нужно ввести число, например 1200.50. Попробуйте снова.");
            }
        }
    }

    public LocalDate readDate(String prompt) {
        while (true) {
            String text = readLine(prompt + " (в формате 31.12.2000): ");
            try {
                return LocalDate.parse(text, DATE_FORMAT);
            } catch (Exception e) {
                System.out.println("Некорректная дата, попробуйте снова.");
            }
        }
    }

    public LocalDateTime readDateTime(String prompt) {
        while (true) {
            String text = readLine(prompt + " (в формате 31.12.2000 19:30): ");
            try {
                return LocalDateTime.parse(text, DATE_TIME_FORMAT);
            } catch (Exception e) {
                System.out.println("Некорректные дата и время, попробуйте снова.");
            }
        }
    }

    /** Выбор одного из значений перечисления по номеру — так не ошибёшься в написании. */
    public <E extends Enum<E>> E readEnum(String prompt, Class<E> enumType) {
        E[] values = enumType.getEnumConstants();
        System.out.println(prompt);
        for (int i = 0; i < values.length; i++) {
            System.out.println("  " + (i + 1) + ". " + values[i]);
        }
        while (true) {
            int choice = readInt("Ваш выбор: ");
            if (choice >= 1 && choice <= values.length) {
                return values[choice - 1];
            }
            System.out.println("Введите число от 1 до " + values.length);
        }
    }

    public boolean readYesNo(String prompt) {
        while (true) {
            String text = readLine(prompt + " (да/нет): ").toLowerCase();
            if (text.equals("да") || text.equals("y") || text.equals("yes")) {
                return true;
            }
            if (text.equals("нет") || text.equals("n") || text.equals("no")) {
                return false;
            }
            System.out.println("Введите «да» или «нет».");
        }
    }
}
