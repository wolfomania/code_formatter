package pl.edu.pja.tpo07.services;

import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import org.springframework.stereotype.Service;
import pl.edu.pja.tpo07.models.FormattedCodeDTO;
import pl.edu.pja.tpo07.models.SavedCode;
import pl.edu.pja.tpo07.repository.CodeRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class FormatterService {

    private final Formatter formatter;

    private final CodeRepository codeRepository;

    public FormatterService(CodeRepository codeRepository) {
        formatter = new Formatter();
        this.codeRepository = codeRepository;
    }

    public String format(String inputCode) throws FormatterException {
        String result = formatter.formatSource(inputCode);
        if(result.equals("\n"))
            return "";
        return result;
    }

    public void saveCode(FormattedCodeDTO code) {
        SavedCode savedCode = new SavedCode(code.getId(), code.getCode(), getExpirationDate(code));
        codeRepository.addCode(savedCode);
    }

    public Optional<SavedCode> getCode(String code) {
        return Optional.ofNullable(codeRepository.getCode(code));
    }

    private LocalDateTime getExpirationDate(FormattedCodeDTO code) {
        return LocalDateTime.now()
                .plusDays(code.getDays())
                .plusHours(code.getHours())
                .plusMinutes(code.getMinutes())
                .plusSeconds(code.getSeconds());
    }
}
