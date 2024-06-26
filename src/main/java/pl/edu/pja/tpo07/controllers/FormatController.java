    package pl.edu.pja.tpo07.controllers;

    import com.google.googlejavaformat.java.FormatterException;
    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.web.bind.annotation.*;
    import org.springframework.web.servlet.mvc.support.RedirectAttributes;
    import org.springframework.web.servlet.view.RedirectView;
    import pl.edu.pja.tpo07.exceptions.InvalidExpireTimeException;
    import pl.edu.pja.tpo07.exceptions.MyResourceNotFoundException;
    import pl.edu.pja.tpo07.models.FormattedCodeDTO;
    import pl.edu.pja.tpo07.models.SavedCode;
    import pl.edu.pja.tpo07.services.FormatterService;

    import java.time.format.DateTimeFormatter;

    @Controller
    public class FormatController {

        private final FormatterService formatterService;

        public FormatController(FormatterService formatterService) {
            this.formatterService = formatterService;
        }

        @GetMapping("/")
        public String format() {
            return "mainPage";
        }

        @GetMapping("/error-page")
        public String errorPage(@RequestParam String errorMessage, Model model) {
            model.addAttribute("errorMessage", errorMessage);
            return "errorPage";
        }

        @PostMapping("/formatCode")
        public RedirectView formatCode(String codeToFormat, RedirectAttributes redirectAttributes) throws FormatterException {
            redirectAttributes.addFlashAttribute("formattedCode", formatterService.format(codeToFormat));
            return new RedirectView("/", true, false);
        }

        @PostMapping("/saveCode")
        public RedirectView saveCode(@ModelAttribute FormattedCodeDTO formattedCodeDTO) {
            long expireIn = formattedCodeDTO.getExpireInSeconds();
            if (expireIn < 10 || expireIn > 60 * 60 * 24 * 90)
                throw new InvalidExpireTimeException("Expire time must be between 10 seconds and 90 days");
            formattedCodeDTO.stripId();
            formatterService.saveCode(formattedCodeDTO);
            return new RedirectView("/" + formattedCodeDTO.getId(), true, false);
        }

        @GetMapping("/{id}")
        public String formatter(@PathVariable("id") String id, Model model) {

            SavedCode savedCode = formatterService.getCode(id.strip())
                    .orElseThrow(() -> new MyResourceNotFoundException("Code snippet not found"));
            model.addAttribute("formattedCode", savedCode.getCode());
            String expirationTime = savedCode.getExpirationDate().format(DateTimeFormatter.ofPattern("HH:mm:ss yyyy-MM-dd"));
            model.addAttribute("expirationDate", expirationTime);
            return "savedCode";
        }

        @PostMapping("/")
        public RedirectView getCode(String id) {
            return new RedirectView("/" + id, true, false);
        }

    }
