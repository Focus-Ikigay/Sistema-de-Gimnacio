package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.entidades.MovimientoFinanciero;
import com.example.demo.repository.MovimientoFinancieroRepository;

import java.util.List;

@Controller
public class FinanzasController {

    @Autowired
    private MovimientoFinancieroRepository movimientoFinancieroRepository;

    @GetMapping("/finanzas")
    public String verFinanzas(Model model) {
        List<MovimientoFinanciero> movimientos = movimientoFinancieroRepository.findAll();

        // Calcular totales
        double totalIngresos = movimientos.stream()
                .filter(m -> m.getTipo().equals("Ingreso"))
                .mapToDouble(MovimientoFinanciero::getMonto)
                .sum();

        double totalGastos = movimientos.stream()
                .filter(m -> m.getTipo().equals("Gasto"))
                .mapToDouble(MovimientoFinanciero::getMonto)
                .sum();

        double balance = totalIngresos - totalGastos;

        // Pasar datos a la vista
        model.addAttribute("movimientos", movimientos);
        model.addAttribute("totalIngresos", totalIngresos);
        model.addAttribute("totalGastos", totalGastos);
        model.addAttribute("balance", balance);

        return "finanzas";  // nombre del HTML sin extensión
    }
    
    @PostMapping("/guardarMovimiento")
    public String guardarMovimiento(@ModelAttribute MovimientoFinanciero movimiento) {
        movimientoFinancieroRepository.save(movimiento);
        return "redirect:/finanzas";
    }


}
