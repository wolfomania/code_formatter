    package pl.edu.pja.tpo07.controllers;

    import com.google.googlejavaformat.java.FormatterException;
    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.web.bind.annotation.*;
    import org.springframework.web.servlet.mvc.support.RedirectAttributes;
    import pl.edu.pja.tpo07.models.FormattedCodeDTO;
    import pl.edu.pja.tpo07.services.FormatterService;

    @Controller
    public class FormatController {

        private final FormatterService formatterService;

        public FormatController(FormatterService formatterService) {
            this.formatterService = formatterService;
        }

        @GetMapping("/formatter")
        public String format(
//                @RequestParam(required = false) String formattedCode,
                @RequestParam(required = false) String errorMessage,
                Model model
        ) {
//            model.addAttribute("formattedCode", formattedCode);
            model.addAttribute("errorMessage", errorMessage);
            return "mainPage";
        }

        @PostMapping("/formatCode")
        public String formatCode(String codeToFormat, RedirectAttributes redirectAttributes) throws FormatterException {
            redirectAttributes.addFlashAttribute("formattedCode", formatterService.format(codeToFormat));
            return "redirect:/formatter";
        }

        @ExceptionHandler(FormatterException.class)
        public String handleFormatterException(FormatterException e) {
            return "redirect:/formatter?errorMessage=" + e.getMessage();
        }
    }
