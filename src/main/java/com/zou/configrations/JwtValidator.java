package com.zou.configrations;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.List;

public class JwtValidator extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String jwt = request.getHeader(JwtConstant.JWT_HEADER);

        if(jwt!=null){
            jwt=jwt.substring(7);

            try {
                // 1. Tạo Key từ Secret String
                SecretKey key = Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());

                // 2. Parse và Validate Token
                Claims claims = Jwts.parser()
                        .verifyWith(key) // Dùng verifyWith thay vì setSigningKey (bản cũ)
                        .build()
                        .parseSignedClaims(jwt)
                        .getPayload(); // Dùng getPayload() thay vì getBody()

                String email = String.valueOf(claims.get("email"));
                String authorities = String.valueOf(claims.get("authorities"));

                // 3. Chuyển đổi quyền (authorities) và set vào SecurityContext
                List<GrantedAuthority> authoritiesList = AuthorityUtils
                        .commaSeparatedStringToAuthorityList(authorities);
                Authentication auth = new UsernamePasswordAuthenticationToken(
                        email,null, authoritiesList);

                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (Exception e) {
                throw new BadCredentialsException("Token không hợp lệ...");
            }

        }

        // RẤT QUAN TRỌNG: Phải có dòng này để request đi tiếp
        filterChain.doFilter(request, response);

    }
}
