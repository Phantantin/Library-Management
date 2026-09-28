package com.zou.service.impl;
import com.zou.domain.UserRole;
import com.zou.modal.User;
import com.zou.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
@Component @RequiredArgsConstructor
public class DataInitializationComponent implements CommandLineRunner {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    @Value("${app.bootstrap.email:}") private String email;
    @Value("${app.bootstrap.password:}") private String password;
    @Override public void run(String... args) {
        if (!email.isBlank() && password.length()>=12 && users.findByEmail(email)==null) {
            User user = new User(); user.setEmail(email); user.setFullName("Library administrator");
            user.setRole(UserRole.ROLE_ADMIN); user.setPassword(encoder.encode(password)); users.save(user);
        }
    }
}
