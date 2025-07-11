package com.example.demo;

import com.example.demo.entidades.Cliente;
import com.example.demo.entidades.Membresia;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.MembresiaRepository;
// Google Guava: para validaciones de cadenas
import com.google.common.base.Strings;
// Logback (vía SLF4J): para registrar información y advertencias
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/membresia")
public class MembresiaController {
	
	@Autowired
	private MembresiaRepository membresiaRepository;

	@Autowired
	private ClienteRepository clienteRepository;

    // Logback: se crea un logger específico para esta clase
    private static final Logger logger = LoggerFactory.getLogger(MembresiaController.class);

    @GetMapping("/asignar")
    public String mostrarFormulario(Model model) {
    	logger.info("Acceso al formulario de asignación de membresía");
        model.addAttribute("membresia", new Membresia());
        model.addAttribute("clientes", clienteRepository.findAll());
        return "Asignar_menbresia";
    }

    @PostMapping("/asignar")
    public String procesarAsignacion(@ModelAttribute Membresia membresia, Model model) {
        if (membresia.getCliente() == null || membresia.getTipo() == null) {
            model.addAttribute("mensaje", "❌ Por favor, complete todos los campos.");
            model.addAttribute("clientes", clienteRepository.findAll());
            // NO crear nueva membresía aquí para mantener datos ingresados
            return "Asignar_menbresia";
        }

        membresiaRepository.save(membresia);
        
        // Mantener los datos para mostrar en el mensaje
        model.addAttribute("mensaje", "✅ Membresía asignada con éxito");
        model.addAttribute("clienteAsignado", membresia.getCliente().getNombreCompleto());
        model.addAttribute("tipoAsignado", membresia.getTipo().toString());
        
        // Resetear el formulario
        model.addAttribute("membresia", new Membresia());
        model.addAttribute("clientes", clienteRepository.findAll());
        
        return "Asignar_menbresia";
    }

}
