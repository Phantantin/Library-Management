package com.zou.configrations;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
@RequiredArgsConstructor
public class JwtValidator extends OncePerRequestFilter {
    private final JwtProvider provider;
    private final com.zou.repository.UserRepository users;
    @Override protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null) {
            try {
                String email = provider.getEmailFromJwtToken(header);
                var user = users.findByEmail(email);
                if (user == null) throw new IllegalArgumentException();
                SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(email, null,
                    AuthorityUtils.createAuthorityList(user.getRole().name())));
            } catch (Exception ex) {
                SecurityContextHolder.clearContext();
                response.sendError(401, "Session expired or invalid"); return;
            }
        }
        chain.doFilter(request, response);
    }
}
