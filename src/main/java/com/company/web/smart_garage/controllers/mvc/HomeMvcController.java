package com.company.web.smart_garage.controllers.mvc;

import com.company.web.smart_garage.data_transfer_objects.ContactUsDto;
import com.company.web.smart_garage.services.EmailSenderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.company.web.smart_garage.utils.Constants.CONTACT_DTO_KEY;
import static com.company.web.smart_garage.utils.Constants.CONTACT_EMAIL_FORMAT;

@RequiredArgsConstructor
@Controller
@RequestMapping("/")
public class HomeMvcController {

    public static final String INDEX_VIEW = "index";
    public static final String ABOUT_VIEW = "about";
    public static final String CONTACT_VIEW = "contact";
    private final EmailSenderService emailService;
    @Value("${spring.mail.username}")
    private String email;

    @GetMapping
    public String showHomePage() {
        return INDEX_VIEW;
    }

    @GetMapping("/about")
    public String showAboutPage() {
        return ABOUT_VIEW;
    }

    @GetMapping("/contact")
    public String showContactPage(Model model) {
        model.addAttribute(CONTACT_DTO_KEY, new ContactUsDto());
        return CONTACT_VIEW;
    }

    @PostMapping("/contact")
    public String sendContactEmail(@Valid @ModelAttribute(CONTACT_DTO_KEY) ContactUsDto contactDto,
                                   BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return CONTACT_VIEW;
        }
        String emailBody = String.format(
                CONTACT_EMAIL_FORMAT, contactDto.getName(), contactDto.getEmail(), contactDto.getMessage());
        emailService.sendEmail(email, contactDto.getSubject(), emailBody);
        return "redirect:/contact";
    }
}
