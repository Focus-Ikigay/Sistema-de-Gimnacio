package com.example.demo;

import com.example.demo.entidades.Empleado;
import com.example.demo.repository.EmpleadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

import java.util.List;

@Controller
@RequestMapping("/empleados")
public class EmpleadoController {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @GetMapping
    public String listarEmpleados(
            @RequestParam(name = "rol", required = false) String rol, // Solución aplicada
            @RequestParam(name = "estado", required = false) String estado, // Solución aplicada
            @RequestParam(name = "busqueda", required = false) String busqueda, // Solución aplicada
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            // Convertir parámetros a enum
            Empleado.Rol rolEnum = null;
            Empleado.Estado estadoEnum = null;
            
            if (rol != null && !rol.isEmpty()) {
                try {
                    rolEnum = Empleado.Rol.valueOf(rol.toUpperCase());
                } catch (IllegalArgumentException e) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Rol no válido: " + rol);
                    return "redirect:/empleados";
                }
            }
            
            if (estado != null && !estado.isEmpty()) {
                try {
                    estadoEnum = Empleado.Estado.valueOf(estado.toUpperCase());
                } catch (IllegalArgumentException e) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Estado no válido: " + estado);
                    return "redirect:/empleados";
                }
            }

            // Realizar la consulta según los parámetros
            List<Empleado> empleados;
            if (busqueda != null && !busqueda.trim().isEmpty()) {
                empleados = empleadoRepository.filtrarPorRolEstadoYBusqueda(
                    rolEnum, estadoEnum, busqueda.trim().toLowerCase());
            } else {
                empleados = empleadoRepository.filtrarPorRolYEstado(rolEnum, estadoEnum);
            }

            model.addAttribute("empleados", empleados);
            model.addAttribute("empleado", new Empleado());
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error al filtrar empleados: " + e.getMessage());
            return "redirect:/empleados";
        }
        
        return "empleados";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("empleado", new Empleado());
        return "empleados"; // Redirige a la misma página con modal
    }
    

    @PostMapping("/guardar")
    public String guardarEmpleado(
            @Valid @ModelAttribute Empleado empleado,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {

        // --- INICIO: Manejo de errores de validación ---
        if (result.hasErrors()) {
            // Añadir un mensaje de error general al modelo
            model.addAttribute("errorMessage", "Por favor corrige los errores en el formulario.");
            
            // Recargar la lista completa de empleados para que la vista principal se muestre correctamente
            // Esto es necesario porque si no se redirige, el modelo 'empleados' no se refresca.
            model.addAttribute("empleados", empleadoRepository.findAll()); 
            
            // Añadir el objeto 'empleado' con los errores de validación al modelo.
            // Esto permite que Thymeleaf (si se usa th:object y th:field)
            // y JavaScript (si se implementa lógica para reabrir el modal)
            // puedan acceder a los datos ingresados y a los mensajes de error específicos.
            model.addAttribute("empleado", empleado); 
            
            // Retornar la misma vista ("empleados") para que el modal no se cierre
            // y el usuario pueda ver los errores y corregirlos.
            return "empleados"; 
        }
        // --- FIN: Manejo de errores de validación ---

        // --- INICIO: Lógica de guardado si no hay errores de validación ---
        try {
            // Si es una actualización, mantener los datos existentes que no vienen del formulario
            // (Esta lógica ya estaba en tu código original y es buena práctica si hay campos no editables en el formulario)
            if (empleado.getId() != null) {
                Empleado empleadoExistente = empleadoRepository.findById(empleado.getId()).orElse(null);
                if (empleadoExistente != null) {
                    // Aquí podrías copiar campos que no están en el formulario si los hay
                    // Por ejemplo: empleado.setFoto(empleadoExistente.getFoto());
                }
            }
            
            // Guardar el empleado (inserta si id es null, actualiza si id existe)
            empleadoRepository.save(empleado);
            
            // Añadir un mensaje de éxito para mostrar después de la redirección
            redirectAttributes.addFlashAttribute("successMessage", 
                empleado.getId() != null ? "Empleado actualizado exitosamente" : "Empleado guardado exitosamente");
            
        } catch (Exception e) {
            // Capturar cualquier excepción durante el proceso de guardado y añadir un mensaje de error
            redirectAttributes.addFlashAttribute("errorMessage", "Error al guardar el empleado: " + e.getMessage());
        }
        // --- FIN: Lógica de guardado ---

        // Redirigir a la página de empleados después de un guardado exitoso o un error en el try-catch.
        // Esto evita el reenvío del formulario al recargar la página.
        return "redirect:/empleados";
    }
    
    @GetMapping("/obtener/{id}")
    @ResponseBody
    public Empleado obtenerEmpleado(@PathVariable("id") Long id) { // Solución aplicada
        return empleadoRepository.findById(id).orElse(null);
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable("id") Long id, Model model) { // Solución aplicada
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de empleado inválido: " + id));
        
        List<Empleado> empleados = empleadoRepository.findAll();
        model.addAttribute("empleados", empleados);
        model.addAttribute("empleado", empleado);
        
        return "empleados"; // Retorna a la misma vista con el modal abierto
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarEmpleado(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) { // Solución aplicada
        try {
            if (empleadoRepository.existsById(id)) {
                empleadoRepository.deleteById(id);
                redirectAttributes.addFlashAttribute("successMessage", "Empleado eliminado exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "El empleado no existe");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar el empleado: " + e.getMessage());
        }
        
        return "redirect:/empleados";
    }
    
    @GetMapping("/empleados/guardar")
    @ResponseBody
    public String testGuardar() {
        return "Estoy activo";
    }

}
