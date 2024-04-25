package pl.edu.pja.tpo07.models;

public class FormattedCodeDTO {

    private String code;

    private String id;

    private int seconds;

    private int minutes;

    private int hours;

    private int days;

    public FormattedCodeDTO(String code, String id, int seconds, int minutes, int hours, int days) {
        this.code = code;
        this.id = id;
        this.seconds = seconds;
        this.minutes = minutes;
        this.hours = hours;
        this.days = days;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getId() {
        return id;
    }

    public int getSeconds() {
        return seconds;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getHours() {
        return hours;
    }

    public int getDays() {
        return days;
    }


}
