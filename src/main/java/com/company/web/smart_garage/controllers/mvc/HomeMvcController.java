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

@RequiredArgsConstructor
@Controller
@RequestMapping("/")
public class HomeMvcController {

    private final EmailSenderService emailService;
    @Value("${spring.mail.username}")
    private String email;

    @GetMapping
    public String showHomePage() {
        return "index";
    }

    @GetMapping("/about")
    public String showAboutPage() {
        return "about";
    }

    @GetMapping("/contact")
    public String showContactPage(Model model) {
        model.addAttribute("contactDto", new ContactUsDto());
        return "contact";
    }

    @PostMapping("/contact")
    public String sendContactEmail(@Valid @ModelAttribute("contactDto") ContactUsDto contactDto,
                                   BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "contact";
        }
        String emailBody = String.format("Name: %s\nContact email: %s\n\n%s",
                contactDto.getName(), contactDto.getEmail(), contactDto.getMessage());
        emailService.sendEmail(email, contactDto.getSubject(), emailBody);
        return "redirect:/contact";
    }
}
