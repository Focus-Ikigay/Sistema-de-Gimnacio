package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.demo.service.LoginErrorLogger;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class LoginController {

    private final LoginErrorLogger loginErrorLogger;

    public LoginController(LoginErrorLogger loginErrorLogger) {
        this.loginErrorLogger = loginErrorLogger;
    }

    @GetMapping("/login")
    public String mostrarLogin(@RequestParam(value = "error", required = false) String error,
                              HttpServletRequest request) {
        if (error != null) {
            String username = request.getParameter("username");
            String ipAddress = request.getRemoteAddr();
            loginErrorLogger.logFailedAttempt(username, ipAddress);
        }
        return "login";
    }
}