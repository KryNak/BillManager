package com.app.billmanager.controller;

import com.app.billmanager.model.ConfirmationToken;
import com.app.billmanager.model.User;
import com.app.billmanager.repository.ConfirmationTokenRepository;
import com.app.billmanager.repository.UserRepository;
import com.app.billmanager.service.EmailSenderService;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Objects;

@Controller
@RequestMapping("/signup")
public class RegistrationController {

    private UserRepository userRepo;
    private PasswordEncoder encoder;
    private ConfirmationTokenRepository tokenRepo;
    private EmailSenderService emailSenderService;

    public RegistrationController(UserRepository userRepo, PasswordEncoder encoder,
                                  ConfirmationTokenRepository tokenRepo, EmailSenderService emailSenderService)
    {
        this.userRepo = userRepo;
        this.encoder = encoder;
        this.tokenRepo = tokenRepo;
        this.emailSenderService = emailSenderService;
    }

    @ModelAttribute("user")
    public User user(){
        return new User();
    }

    @GetMapping("/confirm-account")
    public String getConfirmAccount(Model model, @Param("token") String token){
        model.addAttribute("step", 2);

        ConfirmationToken confirmationToken = tokenRepo.findByConfirmationToken(token);
        if(!Objects.isNull(confirmationToken)){
            User user = userRepo.findUserByEmail(confirmationToken.getUser().getEmail());
            user.setEnabled(true);
            userRepo.save(user);

            model.addAttribute("success", "You have successfully signed up.");
        }

        return "signup";
    }

    @GetMapping("/create-account")
    public String getCreateAccount(Model model){
        model.addAttribute("step", 1);
        return "signup";
    }

    @PostMapping("/create-account")
    public String postRegistration(@Valid User user, String passwordConfirmation, RedirectAttributes redirectAttributes, Errors errors) {

        if(!user.getPassword().equals(passwordConfirmation)){
            redirectAttributes.addFlashAttribute("passErr", "Password does not match");
            return "redirect:/signup/create-account";
        }

        User userConfirmation = userRepo.findUserByEmail(user.getEmail());
        if(!Objects.isNull(userConfirmation)){
            redirectAttributes.addFlashAttribute("error", "Username is already existing");
            return "redirect:/signup/create-account";
        }

        user.setRole("ROLE_USER");
        user.setPassword(encoder.encode(user.getPassword()));
        userRepo.save(user);

        ConfirmationToken confirmationToken = new ConfirmationToken(user);
        tokenRepo.save(confirmationToken);

        String msg = "To confirm your account, please click here: http://localhost:8080/signup/confirm-account?token=%s";
        SimpleMailMessage smm = new SimpleMailMessage();
        smm.setTo(user.getEmail());
        smm.setFrom("MIBI@gmail.com");
        smm.setText(String.format(msg, confirmationToken.getConfirmationToken()));
        emailSenderService.sendEmail(smm);

        return "redirect:/signup/confirm-account";
    }
}
