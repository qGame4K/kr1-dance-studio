package ru.mirea.dancestudio.exception;

/**
 * Общий предок всех собственных исключений проекта.
 * Наследуется от RuntimeException (unchecked), чтобы не писать "throws ..."
 * в каждом методе — вместо этого все ошибки ловятся один раз в ConsoleApp
 * и программа не падает, а просто печатает понятное сообщение.
 */
public class DanceStudioException extends RuntimeException {

    public DanceStudioException(String message) {
        super(message);
    }

    public DanceStudioException(String message, Throwable cause) {
        super(message, cause);
    }
}
