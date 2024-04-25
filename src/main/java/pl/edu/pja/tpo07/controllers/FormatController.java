    package pl.edu.pja.tpo07.controllers;

    import com.google.googlejavaformat.java.FormatterException;
    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.web.bind.annotation.*;
    import org.springframework.web.servlet.mvc.support.RedirectAttributes;
    import pl.edu.pja.tpo07.models.FormattedCodeDTO;
    import pl.edu.pja.tpo07.models.SavedCode;
    import pl.edu.pja.tpo07.services.FormatterService;

    @Controller
    public class FormatController {

        private final FormatterService formatterService;

        public FormatController(FormatterService formatterService) {
            this.formatterService = formatterService;
        }

        @GetMapping("/formatter")
        public String format(
                @RequestParam(required = false) String errorMessage,
                Model model
        ) {
            if (errorMessage != null) {
                model.addAttribute("errorMessage", errorMessage);
                return "errorPage";
            }
            return "mainPage";
        }

        @PostMapping("/formatCode")
        public String formatCode(String codeToFormat, RedirectAttributes redirectAttributes) throws FormatterException {
            redirectAttributes.addFlashAttribute("formattedCode", formatterService.format(codeToFormat));
            return "redirect:/formatter";
        }

        @PostMapping("/saveCode")
        public String saveCode(@ModelAttribute FormattedCodeDTO formattedCodeDTO, Model model) {
            long expireIn = formattedCodeDTO.getSeconds()
                    + formattedCodeDTO.getMinutes() * 60L
                    + formattedCodeDTO.getHours() * 60 * 60L
                    + formattedCodeDTO.getDays() * 60 * 60 * 24L;
            if (expireIn < 10 || expireIn > 60 * 60 * 24 * 90)
                throw new IllegalArgumentException("Expire time must be between 10 seconds and 90 days");
            formatterService.saveCode(formattedCodeDTO);
            model.addAttribute("formattedCode", formattedCodeDTO.getCode());
            return "redirect:/formatter/" + formattedCodeDTO.getId();
        }

        @GetMapping("/formatter/{id}")
        public String formatter(@PathVariable("id") String id, Model model) {
            SavedCode savedCode = formatterService.getCode(id);
            if (savedCode == null)
                throw new NullPointerException("Code snippet not found");
            model.addAttribute("formattedCode", formatterService.getCode(id).getCode());
            return "savedCode";
        }

        @ExceptionHandler(
                {
                        FormatterException.class,
                        IllegalArgumentException.class,
                        NullPointerException.class
                }
                )
        public String handleFormatterException(Exception e) {
            return "redirect:/formatter?errorMessage=" + e.getMessage();
        }
    }
