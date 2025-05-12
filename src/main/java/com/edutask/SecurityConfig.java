package com.edutask;

import com.edutask.service.CustomUserDetailsService;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable).authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/register", "index.html", "/register.html", "assets/css/**", "assets/images/**", "assets/js/**", "/index").permitAll()
                .requestMatchers("/telegram/webhook").permitAll()
                .requestMatchers("/administrador.html", "/").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            //Codigo para logearse
            .formLogin(login -> login
                .loginPage("/index#login") // Define la URL de tu propio formulario de login
                .loginProcessingUrl("/login") // URL que manejará la autenticación
                .usernameParameter("email") // Nombre del parámetro del formulario
                .passwordParameter("password")
                .successHandler(customSuccessHandler())
                .failureHandler(customFailureHandler())
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessHandler((request, response, authentication) -> {
                    request.getSession().invalidate();
                    response.sendRedirect("/login?logout");
                })
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );
        return http.build();
    }

    //Codificación
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public AuthenticationSuccessHandler customSuccessHandler() {
        return (request, response, authentication) -> {
            CustomUserDetailsService.resetContador();

            String redirectUrl = "/dashboard.html";
            if (AuthorityUtils.authorityListToSet(authentication.getAuthorities()).contains("ROLE_ADMIN")) {
                redirectUrl = "/administrador";
            }
            response.sendRedirect(redirectUrl);
        };
    }

    @Bean
    public AuthenticationFailureHandler customFailureHandler() {
        return (request, response, exception) -> {
            String errorMessage = "Error de autenticación.";
            if (exception instanceof UsernameNotFoundException) {
                errorMessage = "Usuario no encontrado.";
            } else if (exception instanceof BadCredentialsException) {
                errorMessage = "Credenciales incorrectas.";
            }

            request.getSession().setAttribute("loginError", errorMessage);
            response.sendRedirect("/index");
        };
    }
}
