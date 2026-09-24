package com.zou.configrations;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import java.util.List;
@Configuration @RequiredArgsConstructor
public class SecurityConfig {
    private final JwtProvider jwtProvider;
    private final com.zou.repository.UserRepository users;
    @Value("${app.cors.origins:http://localhost:3000,http://localhost:5173}") private List<String> origins;
    @Bean public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(a -> a
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/api/admin/**", "/api/subscription-plans/admin/**", "/api/subscriptions/admin/**", "/api/book-loans/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/books/**", "/api/genres/**", "/api/subscription-plans", "/api/reviews/book/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/books/search").permitAll()
                .requestMatchers("/api/books/**", "/api/genres/**").hasRole("ADMIN")
                .requestMatchers("/api/users/list", "/api/book-loans/search", "/api/book-loans/checkout/user/**", "/api/reservations/user/**", "/api/reservations/*/fulfill").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/reservations", "/api/fines", "/api/payments").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/fines", "/api/fines/waive").hasRole("ADMIN")
                .requestMatchers("/api/subscriptions/activate").hasRole("ADMIN")
                .requestMatchers("/api/**").authenticated()
                .requestMatchers("/auth/**", "/", "/error").permitAll().anyRequest().denyAll())
            .addFilterBefore(new JwtValidator(jwtProvider, users), BasicAuthenticationFilter.class)
            .exceptionHandling(e -> e
                .authenticationEntryPoint((q,r,x) -> r.sendError(401, "Please sign in"))
                .accessDeniedHandler((q,r,x) -> r.sendError(403, "Access denied")))
            .csrf(AbstractHttpConfigurer::disable)
            .cors(c -> c.configurationSource(request -> {
                CorsConfiguration cfg = new CorsConfiguration();
                cfg.setAllowedOrigins(origins);
                cfg.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
                cfg.setAllowedHeaders(List.of("Authorization","Content-Type","Accept"));
                cfg.setAllowCredentials(true); cfg.setMaxAge(3600L); return cfg;
            })).build();
    }
    @Bean public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
}
