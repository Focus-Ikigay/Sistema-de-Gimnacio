package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class LoginErrorLogger {

    private static final String LOG_FILE = "login_errors.log";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void logFailedAttempt(String username, String ipAddress) {
        String logEntry = String.format("[%s] Intento fallido - Usuario: %s, IP: %s%n",
                LocalDateTime.now().format(DATE_FORMATTER),
                username,
                ipAddress);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            writer.write(logEntry);
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo de log: " + e.getMessage());
        }
    }
}