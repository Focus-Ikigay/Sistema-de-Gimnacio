package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.entidades.Membresia;
import com.example.demo.repository.ClienteRepository;

@Controller
public class ControllerDemo {
   
 
    
    @GetMapping("/finanzasDemo")
    public String mostrarFinanzas() {
        return "FinanzasDemo";
    }
     
    @GetMapping("/Registro_Cliente-nuevo")
    public String mostrarRegistro() {
        return "Registro_Cliente"; 
    }
    
   
    @GetMapping("/Asignar_menbresia")
    public String mostrarAsignacionMembresia(Model model) {
        model.addAttribute("membresia", new Membresia());
        model.addAttribute("clientes", clienteRepository.findAll());
        return "Asignar_menbresia";
    }
 
    @GetMapping("/InventarioDEMO")
    public String mostrarInventario() {
        return "InventarioDEMO"; 
    }
    


    @GetMapping("/Pago_entrada")
    public String mostrarPagoEntrada() {
        return "Pago_entrada"; 
    }
    
    @GetMapping("/Empleados")
    public String mostrarEmpleados() {
        return "Empleados"; 
    }
    

    @Autowired
    private ClienteRepository clienteRepository;
}
