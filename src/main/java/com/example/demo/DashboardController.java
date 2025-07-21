package com.example.demo;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.entidades.MovimientoFinanciero;
import com.example.demo.repository.*;
import com.example.demo.repository.ClienteRepository;

@Controller
public class DashboardController {
    
    @Autowired
    private MembresiaRepository membresiaRepository;
    
    @Autowired
    private EntradaRepository entradaRepository;
    
    @Autowired
    private MovimientoFinancieroRepository finanzasRepository;
    
    @Autowired
    private ProductoRepository productoRepository;
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    // Maneja tanto la raíz como /principal
    @GetMapping({"/"})
    public String mostrarDashboard(Model model) {
        // Resumen General
        model.addAttribute("ingresosMensuales", finanzasRepository.sumMontoByTipoAndFechaBetween(
            MovimientoFinanciero.Tipo.Ingreso, 
            LocalDate.now().withDayOfMonth(1), 
            LocalDate.now()
        ));
        
        model.addAttribute("sociosActivos", membresiaRepository.countActiveMemberships(LocalDateTime.now()));
        model.addAttribute("visitasHoy", entradaRepository.countByFecha(LocalDate.now()));
        model.addAttribute("renovacionesPendientes", membresiaRepository.countRenovacionesPendientes(LocalDateTime.now()));
        
        // Clientes Recientes
        model.addAttribute("clientesRecientes", clienteRepository.findTop5ByOrderByFechaRegistroDesc());
        
        // Entradas Recientes
        model.addAttribute("entradasRecientes", entradaRepository.findTop5ByOrderByFechaDesc());
        
        // Productos con Stock Bajo
        model.addAttribute("productosBajoStock", productoRepository.findByCantidadLessThan(10));
        
        return "principal";
    }
}