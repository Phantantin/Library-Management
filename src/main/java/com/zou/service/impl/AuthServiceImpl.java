package com.zou.service.impl;

import com.zou.configrations.JwtProvider;
import com.zou.domain.AuthProvider;
import com.zou.domain.UserRole;
import com.zou.exception.UserException;
import com.zou.mapper.UserMapper;
import com.zou.modal.PasswordResetToken;
import com.zou.modal.User;
import com.zou.payload.dto.UserDTO;
import com.zou.payload.response.AuthResponse;
import com.zou.repository.PasswordResetTokenRepository;
import com.zou.repository.UserRepository;
import com.zou.service.AuthService;
import com.zou.service.EmailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final CustomerUserServiceImplementation customerUserServiceImplementation;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;
    @org.springframework.beans.factory.annotation.Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    @Override
    public AuthResponse login(String userName, String password) throws UserException {
        Authentication authentication = authenticate(userName, password);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String role = authorities.iterator().next().getAuthority();
        String token = jwtProvider.generateToken(authentication);

        User user = userRepository.findByEmail(userName);

        // update last login
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        AuthResponse response = new AuthResponse();
        response.setTitle("Login Success");
        response.setMessage("Welcome Back " + userName);
        response.setJwt(token);
        response.setUser(UserMapper.toDTO(user));

        return response;
    }

    private Authentication authenticate(String userName, String password) throws UserException {
        UserDetails userDetails = customerUserServiceImplementation.loadUserByUsername(userName);

        if(userDetails == null){
            throw new UserException("Invalid email or password");

        }
        if(!passwordEncoder.matches(password, userDetails.getPassword())){
            throw new UserException("Invalid email or password");
        }

        return new UsernamePasswordAuthenticationToken(userName, null, userDetails.getAuthorities());

    }

    @Override
    public AuthResponse signup(UserDTO req) throws UserException {
        User user = userRepository.findByEmail(req.getEmail());

        if(user!=null){
            throw new UserException("Email id already register");
        }
        User createdUser = new User();
        createdUser.setEmail(req.getEmail());
        createdUser.setPassword(passwordEncoder.encode(req.getPassword()));
        createdUser.setPhone(req.getPhone());
        createdUser.setFullName(req.getFullName());
        createdUser.setLastLogin(LocalDateTime.now());
        createdUser.setRole(UserRole.ROLE_USER);

        User savedUser = userRepository.save(createdUser);

        Authentication auth = new UsernamePasswordAuthenticationToken(
                savedUser.getEmail(),
                savedUser.getPassword(),
                List.of(new SimpleGrantedAuthority(savedUser.getRole().name()))
        );

        SecurityContextHolder.getContext().setAuthentication(auth);

        String jwt = jwtProvider.generateToken(auth);

        AuthResponse response = new AuthResponse();
        response.setTitle("Welcome " +createdUser.getFullName());
        response.setMessage("Register success");
        response.setJwt(jwt);
        response.setUser(UserMapper.toDTO(savedUser));
        return response;
    }

    @Override
    public void createPasswordResetToken(String email) throws UserException {

        User user = userRepository.findByEmail(email);

        if(user == null){
            return;
        }

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(5))
                .build();
        passwordResetTokenRepository.save(resetToken);
        String resetLink = frontendUrl + "/reset-password?token=" + token;
        String subject = "Password Reset Request";
        String body = "You requested reset your password. Use this link (valid 5 minutes): "+ resetLink;

        // sent email

        emailService.sendEmail(user.getEmail(), subject, body);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) throws Exception {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(
                        ()-> new Exception("Token not valid!")
                );
        if(resetToken.isExpired()){
            passwordResetTokenRepository.delete(resetToken);
            throw new Exception("Token expired");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        passwordResetTokenRepository.delete(resetToken);
    }
}
