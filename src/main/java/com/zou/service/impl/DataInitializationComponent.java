package com.zou.service.impl;


import com.zou.domain.UserRole;
import com.zou.modal.User;
import com.zou.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializationComponent implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args){
        initializeAdminUser();
    }

    private void initializeAdminUser(){
        String adminEmail ="tantincva@gmail.com";
        String adminPassword = "tantin12345";

        if(userRepository.findByEmail(adminEmail)==null){
            User user = new User().builder()
                    .password(passwordEncoder.encode(adminPassword))
                    .email(adminEmail)
                    .fullName("Code with Zou")
                    .role(UserRole.ROLE_ADMIN)
                    .build();

            User admin = userRepository.save(user);
        }
    }
}
