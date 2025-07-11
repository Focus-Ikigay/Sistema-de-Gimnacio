package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.ProductoRepository;

@Controller
public class PrincipalController {

    @Autowired
    private ClienteRepository clienteRepository;
    
    @Autowired
    private ProductoRepository productoRepository;

    @GetMapping("/principal")
    public String principal(Model model) {
        model.addAttribute("clientesRecientes", clienteRepository.findTop5ByOrderByFechaRegistroDesc());
        // Obtener productos con menos de 10 unidades
        model.addAttribute("productosBajoStock", productoRepository.findByCantidadLessThan(10));
        return "principal";
    }
}