package ru.javago.ecxeption;

/**
 * Класс для создания исключений в Restarter
 */
public class RestartException extends RuntimeException {

    public RestartException(String message) {
        super(message);
    }

    public RestartException(String message, Throwable cause) {
        super(message, cause);
    }
}
