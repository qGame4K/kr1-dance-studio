package ru.mirea.dancestudio.exception;

/**
 * Бросается, когда введённые данные некорректны (например, пустое имя)
 * или нарушено одно из бизнес-правил (например, свободных мест нет).
 */
public class ValidationException extends DanceStudioException {

    public ValidationException(String message) {
        super(message);
    }
}
