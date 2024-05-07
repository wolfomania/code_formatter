package pl.edu.pja.tpo07.models;

import java.io.Serializable;
import java.time.LocalDateTime;

public class SavedCode implements Serializable {

    private final String id;

    private String code;

    private LocalDateTime expirationDate;

    public SavedCode(String id, String code, LocalDateTime expirationDate) {
        this.id = id;
        this.code = code;
        this.expirationDate = expirationDate;
    }

    public String getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }
}
