package vn.nextcore.device.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import vn.nextcore.device.dto.resp.ErrorResponse;
import vn.nextcore.device.enums.ErrorCodeEnum;
import vn.nextcore.device.enums.PathEnum;
import vn.nextcore.device.exception.HandlerException;
import vn.nextcore.device.security.jwt.JwtRequestFilter;

import java.io.IOException;

@Component
@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtRequestFilter jwtRequestFilter) {
        try {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configure(http))
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/auth/**", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html",
                            "/images/**", "/api/forgotPassword/verifyMail/**", "/api/forgotPassword/verifyOtp/**", "/ws/**").permitAll()
                    .requestMatchers("/api/request/**", "/api/group/**", "/api/project").hasAnyAuthority("Employee", "Back Office", "Manager")
                    .requestMatchers("/api/device/**", "/api/delivery/**", "/api/specification/**", "/api/provider/**").hasAnyAuthority("Back Office", "Manager")
                    .requestMatchers("/api/user/**").hasAuthority("Manager")
                    .anyRequest().authenticated()
            )
            .exceptionHandling(exceptionHandling -> exceptionHandling
                    .authenticationEntryPoint((request, response, ex) -> {
                        handleException(response, ErrorCodeEnum.ER102.getCode(), ErrorCodeEnum.ER102.getMessage(), request.getRequestURI(), HttpStatus.UNAUTHORIZED);
                    })
                    .accessDeniedHandler((request, response, accessDeniedException) -> {
                        handleException(response, ErrorCodeEnum.ER103.getCode(), ErrorCodeEnum.ER103.getMessage(), request.getRequestURI(), HttpStatus.FORBIDDEN);
                    })
            );

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.DEVICE_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    private void handleException(HttpServletResponse response, String errCode, String message, String path, HttpStatus status) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");

        ErrorResponse errorResponse = new ErrorResponse(errCode, message, path);

        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
