package com.app.billmanager.configuration;

import com.app.billmanager.model.Bill;
import com.app.billmanager.model.ConfirmationToken;
import com.app.billmanager.model.User;
import com.app.billmanager.repository.BillRepository;
import com.app.billmanager.repository.ConfirmationTokenRepository;
import com.app.billmanager.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Configuration
public class InputConfig {

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CommandLineRunner dataLoader(BillRepository billRepo, UserRepository userRepo,
                                        ConfirmationTokenRepository confirmationTokenRepo, PasswordEncoder encoder)
    {
        return args -> {
            User user = new User( "Borys76@gmail.com", encoder.encode("12345"), "ROLE_USER", "Borys Klawy");
            userRepo.save(user);

            List<Bill> bills = Arrays.asList(
                    new Bill("ham", LocalDate.now(), 3.55, user),
                    new Bill("beacon", LocalDate.now(), 5.75, user),
                    new Bill("cheese", LocalDate.now(), 2.77, user),
                    new Bill("orange juice", LocalDate.now(), 1.20, user),
                    new Bill("apricot", LocalDate.now(), 3.00, user),
                    new Bill("carrot", LocalDate.now(), 0.3, user),
                    new Bill("cheeseburger", LocalDate.now(), 2.5, user),
                    new Bill("bananas", LocalDate.now(), 5.67, user),
                    new Bill("watermelon", LocalDate.now(), 3.0, user)
            );
            billRepo.saveAll(bills);

            ConfirmationToken ct = new ConfirmationToken(user);
            confirmationTokenRepo.save(ct);

        };

    }

}
