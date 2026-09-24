package ru.mirea.dancestudio.exception;

/** Бросается, когда записи с указанным id нет в базе данных. */
public class NotFoundException extends DanceStudioException {

    public NotFoundException(String message) {
        super(message);
    }
}
