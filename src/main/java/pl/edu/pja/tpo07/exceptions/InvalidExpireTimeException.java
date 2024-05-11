package pl.edu.pja.tpo07.exceptions;

public class InvalidExpireTimeException extends RuntimeException {
    public InvalidExpireTimeException(String message) {
        super(message);
    }
}
