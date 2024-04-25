package pl.edu.pja.tpo07.services;

import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import org.springframework.stereotype.Service;

@Service
public class FormatterService {

    private Formatter formatter;

    public FormatterService() {
        formatter = new Formatter();
    }

    public String format(String inputCode) throws FormatterException {
        return formatter.formatSource(inputCode);
    }
}
