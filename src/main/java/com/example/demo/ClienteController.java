package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entidades.Cliente;
import com.example.demo.repository.ClienteRepository;

@Controller
public class ClienteController {

	@Autowired
	private ClienteRepository clienteRepository;

	
    /**
     * Muestra el formulario de registro de cliente
     */
	@GetMapping("/Registro_Cliente")
	public String mostrarFormularioRegistro(Model model) {
	    model.addAttribute("cliente", new Cliente());
	    return "Registro_Cliente";
	}
    
    /**
     * Procesa el formulario de registro de cliente
     */
	@PostMapping("/registrarCliente")
	public String registrarCliente(@ModelAttribute Cliente cliente, RedirectAttributes redirectAttributes) {
	    clienteRepository.save(cliente);
	    redirectAttributes.addFlashAttribute("mensaje", "Cliente registrado correctamente");
	    redirectAttributes.addFlashAttribute("exito", true);
	    return "redirect:/Registro_Cliente";
	}

}