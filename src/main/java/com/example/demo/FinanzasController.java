package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entidades.MovimientoFinanciero;
import com.example.demo.entidades.MovimientoFinanciero.Tipo;
import com.example.demo.repository.MovimientoFinancieroRepository;

import java.time.LocalDate;
import java.util.List;

@Controller
public class FinanzasController {

    @Autowired
    private MovimientoFinancieroRepository movimientoFinancieroRepository;

    @GetMapping("/finanzas")
    public String verFinanzas(
            @RequestParam(name = "fecha", required = false) String fecha,
            @RequestParam(name = "tipo", required = false) Tipo tipo,
            Model model) {
        
        List<MovimientoFinanciero> movimientos;
        
        if (fecha != null && !fecha.isEmpty() && tipo != null) {
            // Filtrar por fecha y tipo
            LocalDate fechaFiltro = LocalDate.parse(fecha);
            movimientos = movimientoFinancieroRepository.findByFechaAndTipo(fechaFiltro, tipo);
        } else if (fecha != null && !fecha.isEmpty()) {
            // Filtrar solo por fecha
            LocalDate fechaFiltro = LocalDate.parse(fecha);
            movimientos = movimientoFinancieroRepository.findByFecha(fechaFiltro);
        } else if (tipo != null) {
            // Filtrar solo por tipo
            movimientos = movimientoFinancieroRepository.findByTipo(tipo);
        } else {
            // Mostrar todos
            movimientos = movimientoFinancieroRepository.findAll();
        }

        // Calcular totales
        double totalIngresos = movimientos.stream()
                .filter(m -> m.getTipo() == Tipo.Ingreso)
                .mapToDouble(MovimientoFinanciero::getMonto)
                .sum();

        double totalGastos = movimientos.stream()
                .filter(m -> m.getTipo() == Tipo.Gasto)
                .mapToDouble(MovimientoFinanciero::getMonto)
                .sum();

        double balance = totalIngresos - totalGastos;

        // Pasar datos a la vista
        model.addAttribute("movimientos", movimientos);
        model.addAttribute("totalIngresos", totalIngresos);
        model.addAttribute("totalGastos", totalGastos);
        model.addAttribute("balance", balance);
        model.addAttribute("fechaFiltro", fecha);
        model.addAttribute("tipoFiltro", tipo);
        model.addAttribute("tiposMovimiento", Tipo.values());

        return "finanzas";
    }
    
    @PostMapping("/guardarMovimiento")
    public String guardarMovimiento(@ModelAttribute MovimientoFinanciero movimiento) {
        movimientoFinancieroRepository.save(movimiento);
        return "redirect:/finanzas";
    }
}