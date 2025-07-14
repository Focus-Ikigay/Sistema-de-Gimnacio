package com.example.demo;

import com.example.demo.entidades.Entrada;
import com.example.demo.repository.EntradaRepository;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/entrada")
public class EntradaController {

    private static final Logger logger = LoggerFactory.getLogger(EntradaController.class);

    @Autowired
    private EntradaRepository entradaRepository;

    @GetMapping("/pagar")
    public String mostrarFormularioPago(Model model) {
        model.addAttribute("entradas", null);
        model.addAttribute("dniBuscado", "");
        model.addAttribute("mensajeBusqueda", "");
        return "Pago_entrada";
    }

    @PostMapping("/pagar")
    public String procesarPago(
            @RequestParam("dni") String dni,
            @RequestParam("monto") double monto,
            Model model,
            HttpServletResponse response) {

        try {
            // Validaciones
            Preconditions.checkNotNull(dni, "DNI no debe ser nulo");
            Preconditions.checkArgument(!dni.trim().isEmpty(), "DNI no debe estar vacío");
            dni = dni.trim();

            if (!StringUtils.isNumeric(dni) || dni.length() != 8) {
                model.addAttribute("mensaje", "❌ DNI inválido. Debe tener 8 dígitos numéricos.");
                return "Pago_entrada";
            }

            if (monto <= 0 || monto > 1000) {
                model.addAttribute("mensaje", "❌ Monto inválido. Debe estar entre S/ 0.01 y S/ 1000.");
                return "Pago_entrada";
            }

            // Crear y guardar entrada
            Entrada entrada = new Entrada();
            entrada.setDniCliente(dni);
            entrada.setMonto(monto);
            entrada.setFecha(LocalDateTime.now());

            Entrada entradaGuardada = entradaRepository.save(entrada);
            logger.info("Entrada creada: ID={}, DNI={}, Monto={}", entradaGuardada.getId(), dni, monto);

            // Generar comprobante
            generarTxtComprobante(response, dni, monto, entradaGuardada.getId());

            // Configurar modelo
            model.addAttribute("mensaje", "✅ Entrada pagada con éxito. ID: " + entradaGuardada.getId());
            model.addAttribute("dni", dni);
            model.addAttribute("monto", monto);
            model.addAttribute("id", entradaGuardada.getId());

        } catch (Exception e) {
            logger.error("Error al crear entrada: ", e);
            model.addAttribute("mensaje", "❌ Error al procesar el pago: " + e.getMessage());
        }

        // Limpiar búsqueda previa
        model.addAttribute("entradas", null);
        model.addAttribute("dniBuscado", "");
        model.addAttribute("mensajeBusqueda", "");
        
        return "Pago_entrada";
    }

    @PostMapping("/buscar")
    public String buscarPorDni(
            @RequestParam("dni") String dni,
            Model model) {

        try {
            if (dni == null || dni.trim().isEmpty()) {
                model.addAttribute("mensajeBusqueda", "❌ Debe ingresar un DNI para buscar.");
                return "Pago_entrada";
            }

            dni = dni.trim();
            List<Entrada> entradas = entradaRepository.findByDniCliente(dni);
            long cantidadEntradas = entradaRepository.countByDniCliente(dni);

            model.addAttribute("entradas", entradas);
            model.addAttribute("dniBuscado", dni);
            model.addAttribute("cantidadEntradas", cantidadEntradas);

            if (entradas.isEmpty()) {
                model.addAttribute("mensajeBusqueda", "ℹ️ No se encontraron entradas para el DNI: " + dni);
            } else {
                double totalGastado = entradas.stream()
                    .mapToDouble(Entrada::getMonto)
                    .sum();
                model.addAttribute("totalGastado", totalGastado);
                model.addAttribute("mensajeBusqueda", "✅ Se encontraron " + entradas.size() + " entradas.");
            }

            logger.info("Búsqueda por DNI {}: {} resultados", dni, entradas.size());

        } catch (Exception e) {
            logger.error("Error al buscar por DNI: ", e);
            model.addAttribute("mensajeBusqueda", "❌ Error en la búsqueda: " + e.getMessage());
        }

        return "Pago_entrada";
    }

    @PostMapping("/eliminar")
    public String eliminarEntrada(
            @RequestParam("id") Long id,
            @RequestParam("dniBuscado") String dniBuscado,
            Model model) {
        
        try {
            Optional<Entrada> entradaOpt = entradaRepository.findById(id);

            if (entradaOpt.isPresent()) {
                entradaRepository.deleteById(id);
                logger.info("Entrada eliminada: ID={}", id);
                
                // Volver a cargar los resultados de búsqueda
                List<Entrada> entradas = entradaRepository.findByDniCliente(dniBuscado);
                model.addAttribute("entradas", entradas);
                model.addAttribute("dniBuscado", dniBuscado);
                model.addAttribute("mensajeBusqueda", "✅ Entrada eliminada correctamente.");
                
                if (!entradas.isEmpty()) {
                    double totalGastado = entradas.stream()
                        .mapToDouble(Entrada::getMonto)
                        .sum();
                    model.addAttribute("totalGastado", totalGastado);
                }
            } else {
                model.addAttribute("mensajeBusqueda", "❌ Entrada no encontrada con ID: " + id);
            }

        } catch (Exception e) {
            logger.error("Error al eliminar entrada: ", e);
            model.addAttribute("mensajeBusqueda", "❌ Error al eliminar entrada: " + e.getMessage());
        }

        return "Pago_entrada";
    }

    private void generarTxtComprobante(HttpServletResponse response, String dni, double monto, Long entradaId) throws IOException {
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String fechaHora = ahora.format(formato);

        response.setContentType("text/plain; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=comprobante_entrada_" + entradaId + ".txt");

        try (PrintWriter writer = response.getWriter()) {
            writer.println("===============================================");
            writer.println("           COMPROBANTE DE PAGO ENTRADA        ");
            writer.println("               HOME FITNESS GYM               ");
            writer.println("===============================================");
            writer.println();
            writer.println("Fecha y hora: " + fechaHora);
            writer.println("DNI Cliente: " + dni);
            writer.println("Monto: S/ " + String.format("%.2f", monto));
            writer.println("ID Transacción: " + entradaId);
            writer.println();
            writer.println("-----------------------------------------------");
            writer.println("¡Gracias por confiar en nosotros!");
            writer.println("===============================================");
        }
    }
}
