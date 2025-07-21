package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.demo.security.EmpleadoUserDetailsService;
import com.example.demo.security.JwtFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final EmpleadoUserDetailsService empleadoUserDetailsService;
    private final JwtFilter jwtFilter;

    public WebSecurityConfig(EmpleadoUserDetailsService empleadoUserDetailsService, 
                             JwtFilter jwtFilter) {
        this.empleadoUserDetailsService = empleadoUserDetailsService;
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/auth", "/css/", "/js/", "/images/").permitAll()

                // RECEPCION: Acceso a cliente, entradas, membresías y página principal
                .requestMatchers(
         
                    "/Registro_Cliente",
                    "/principal",
                    "/Pago_entrada",
                    "/registrarCliente",
                    "/Asignar_menbresia",
                    "/entrada/pagar",
                    "/entrada/buscar",
                    "/membresia/asignar",
                    "pages/Asignar_menbresia.html"
                  
                    
                ).hasAnyRole("RECEPCION", "ADMINISTRATIVO" ) // ADMINISTRATIVO también incluido

                // ENTRENADOR: Acceso a agenda
                .requestMatchers("/agenda/" , "/principal").hasAnyRole("ENTRENADOR", "ADMINISTRATIVO")
                

                // LIMPIEZA: Acceso a inventario
                .requestMatchers("/inventario/").hasAnyRole("LIMPIEZA", "ADMINISTRATIVO")

                // Resto de rutas: solo ADMINISTRATIVO
                .anyRequest().hasRole("ADMINISTRATIVO")
            )
            .formLogin(form -> form
                .loginPage("/login").permitAll()
                .defaultSuccessUrl("/principal", true)
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
            .userDetailsService(empleadoUserDetailsService)
            .passwordEncoder(passwordEncoder())
            .and()
            .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}



// eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI5ODc2NTQzMiIsImlhdCI6MTc1MjU1MzkzNCwiZXhwIjoxNzUyNTU3NTM0fQ.eoXsG3SWZtgiIhcti3P1llDkQBvrue2vJZpU-28bRjE
