package pl.edu.pja.tpo07.controllers;

import com.google.googlejavaformat.java.FormatterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.servlet.view.RedirectView;
import pl.edu.pja.tpo07.exceptions.MyResourceNotFoundException;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(
            {
                    FormatterException.class,
                    IllegalArgumentException.class,
                    NullPointerException.class,
                    MyResourceNotFoundException.class
            }
    )
    public RedirectView handleFormatterException(Exception e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addAttribute("errorMessage", e.getMessage());
        return new RedirectView("/error-page", true, false);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public RedirectView handleNoResourceFoundException(Exception ignore) {
        return new RedirectView("/", true, false);
    }
}
