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
import java.time.LocalDate;
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

    // =============================================================
    // ====================== C R E A T E ==========================
    // ============ CREATE - Crear nueva entrada ===================
    @GetMapping("/pagar") // Mostrar formulario de registro de entrada
    public String mostrarFormularioPago() {
        return "Pago_entrada";
    }

    @PostMapping("/pagar") // Procesar formulario y guardar nueva entrada
    public String procesarPago(
            @RequestParam("dni") String dni,
            @RequestParam("monto") double monto,
            Model model,
            HttpServletResponse response) {

        try {
            // Validaciones del formulario
            Preconditions.checkNotNull(dni, "DNI no debe ser nulo");
            Preconditions.checkArgument(!dni.trim().isEmpty(), "DNI no debe estar vacío");

            dni = dni.trim();

            if (!StringUtils.isNumeric(dni) || dni.length() != 8) {
                model.addAttribute("mensaje", "❌ DNI inválido. Debe tener exactamente 8 dígitos numéricos.");
                return "Pago_entrada";
            }

            if (monto <= 0 || monto > 1000) {
                model.addAttribute("mensaje", "❌ Monto inválido. Debe estar entre S/ 0.01 y S/ 1000.");
                return "Pago_entrada";
            }

            // Crear objeto Entrada y guardar en la base de datos
            Entrada entrada = new Entrada();
            entrada.setDniCliente(dni);
            entrada.setMonto(monto);
            entrada.setFecha(LocalDateTime.now());

            Entrada entradaGuardada = entradaRepository.save(entrada); // --> Operación CREATE

            logger.info("Entrada creada: ID={}, DNI={}, Monto={}", 
                       entradaGuardada.getId(), dni, monto);

            generarTxtComprobante(response, dni, monto, entradaGuardada.getId());

            model.addAttribute("mensaje", "✅ Entrada pagada con éxito. ID: " + entradaGuardada.getId());
            model.addAttribute("dni", dni);
            model.addAttribute("monto", monto);

        } catch (Exception e) {
            logger.error("Error al crear entrada: ", e);
            model.addAttribute("mensaje", "❌ Error al procesar el pago: " + e.getMessage());
        }

        return "Pago_entrada";
    }

    // =============================================================
    // ======================= R E A D =============================
    // ========== READ - Listar todas las entradas ================
    @GetMapping("/listar") // Mostrar lista completa de entradas
    public String listarEntradas(Model model) {
        try {
            List<Entrada> entradas = entradaRepository.findAllByOrderByFechaDesc(); // READ general
            model.addAttribute("entradas", entradas);
            model.addAttribute("totalEntradas", entradas.size());

            // Calcular total de ingresos
            double totalIngresos = entradas.stream()
                .mapToDouble(Entrada::getMonto)
                .sum();
            model.addAttribute("totalIngresos", totalIngresos);

            logger.info("Listando {} entradas", entradas.size());

        } catch (Exception e) {
            logger.error("Error al listar entradas: ", e);
            model.addAttribute("mensaje", "❌ Error al cargar las entradas.");
        }

        return "listar_entradas";
    }

    // ========== READ - Buscar entradas por DNI ================
    @GetMapping("/buscar") // Mostrar formulario de búsqueda por DNI
    public String mostrarFormularioBusqueda() {
        return "buscar_entradas";
    }

    @PostMapping("/buscar") // Buscar entradas por DNI
    public String buscarPorDni(
            @RequestParam("dni") String dni,
            Model model) {

        try {
            if (dni == null || dni.trim().isEmpty()) {
                model.addAttribute("mensaje", "❌ Debe ingresar un DNI para buscar.");
                return "buscar_entradas";
            }

            dni = dni.trim();

            // Consulta JPA personalizada #1 - Buscar entradas por DNI
            List<Entrada> entradas = entradaRepository.findByDniCliente(dni);

            // Consulta JPA personalizada #2 - Contar entradas por DNI
            long cantidadEntradas = entradaRepository.countByDniCliente(dni);

            model.addAttribute("entradas", entradas);
            model.addAttribute("dniBuscado", dni);
            model.addAttribute("cantidadEntradas", cantidadEntradas);

            if (entradas.isEmpty()) {
                model.addAttribute("mensaje", "ℹ️ No se encontraron entradas para el DNI: " + dni);
            } else {
                double totalGastado = entradas.stream()
                    .mapToDouble(Entrada::getMonto)
                    .sum();
                model.addAttribute("totalGastado", totalGastado);
                model.addAttribute("mensaje", "✅ Se encontraron " + entradas.size() + " entradas.");
            }

            logger.info("Búsqueda por DNI {}: {} resultados", dni, entradas.size());

        } catch (Exception e) {
            logger.error("Error al buscar por DNI: ", e);
            model.addAttribute("mensaje", "❌ Error en la búsqueda: " + e.getMessage());
        }

        return "buscar_entradas";
    }

    // ========== READ - Consultar entradas del día ================
    @GetMapping("/hoy") // Buscar entradas de la fecha actual
    public String entradasDeHoy(Model model) {
        try {
            // Consulta JPA personalizada #3 - Obtener entradas de hoy
            List<Entrada> entradasHoy = entradaRepository.findEntradasDeHoy();

            // Consulta JPA personalizada #4 - Contar entradas por fecha
            long cantidadHoy = entradaRepository.countByFecha(LocalDate.now());

            model.addAttribute("entradas", entradasHoy);
            model.addAttribute("cantidadHoy", cantidadHoy);
            model.addAttribute("fechaHoy", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

            if (!entradasHoy.isEmpty()) {
                double ingresosDiarios = entradasHoy.stream()
                    .mapToDouble(Entrada::getMonto)
                    .sum();
                model.addAttribute("ingresosDiarios", ingresosDiarios);
            }

            logger.info("Consulta entradas de hoy: {} resultados", entradasHoy.size());

        } catch (Exception e) {
            logger.error("Error al consultar entradas de hoy: ", e);
            model.addAttribute("mensaje", "❌ Error al cargar entradas del día.");
        }

        return "entradas_hoy";
    }

    // =============================================================
    // ====================== U P D A T E ==========================
    // ========== UPDATE - Mostrar formulario edición =============
    @GetMapping("/editar/{id}") // Mostrar formulario con datos existentes
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model) {
        try {
            Optional<Entrada> entradaOpt = entradaRepository.findById(id);

            if (entradaOpt.isPresent()) {
                model.addAttribute("entrada", entradaOpt.get());
                return "editar_entrada";
            } else {
                model.addAttribute("mensaje", "❌ Entrada no encontrada con ID: " + id);
                return "redirect:/entrada/listar";
            }

        } catch (Exception e) {
            logger.error("Error al cargar entrada para editar: ", e);
            return "redirect:/entrada/listar";
        }
    }

    // ========== UPDATE - Procesar edición ========================
    @PostMapping("/editar/{id}")
    public String actualizarEntrada(
            @PathVariable Long id,
            @RequestParam("dni") String dni,
            @RequestParam("monto") double monto,
            Model model) {

        try {
            Optional<Entrada> entradaOpt = entradaRepository.findById(id);

            if (!entradaOpt.isPresent()) {
                model.addAttribute("mensaje", "❌ Entrada no encontrada.");
                return "redirect:/entrada/listar";
            }

            // Validaciones básicas
            dni = dni.trim();
            if (!StringUtils.isNumeric(dni) || dni.length() != 8) {
                model.addAttribute("mensaje", "❌ DNI inválido.");
                model.addAttribute("entrada", entradaOpt.get());
                return "editar_entrada";
            }

            if (monto <= 0 || monto > 1000) {
                model.addAttribute("mensaje", "❌ Monto inválido.");
                model.addAttribute("entrada", entradaOpt.get());
                return "editar_entrada";
            }

            // Actualizar y guardar entrada
            Entrada entrada = entradaOpt.get();
            entrada.setDniCliente(dni);
            entrada.setMonto(monto);

            entradaRepository.save(entrada); // --> Operación UPDATE

            logger.info("Entrada actualizada: ID={}", id);
            model.addAttribute("mensaje", "✅ Entrada actualizada correctamente.");

        } catch (Exception e) {
            logger.error("Error al actualizar entrada: ", e);
            model.addAttribute("mensaje", "❌ Error al actualizar: " + e.getMessage());
        }

        return "redirect:/entrada/listar";
    }

    // =============================================================
    // ===================== D E L E T E ===========================
    // ========== DELETE - Eliminar entrada ========================
    @PostMapping("/eliminar/{id}")
    public String eliminarEntrada(@PathVariable Long id, Model model) {
        try {
            Optional<Entrada> entradaOpt = entradaRepository.findById(id);

            if (entradaOpt.isPresent()) {
                entradaRepository.deleteById(id); // --> Operación DELETE
                logger.info("Entrada eliminada: ID={}", id);
                model.addAttribute("mensaje", "✅ Entrada eliminada correctamente.");
            } else {
                model.addAttribute("mensaje", "❌ Entrada no encontrada con ID: " + id);
            }

        } catch (Exception e) {
            logger.error("Error al eliminar entrada: ", e);
            model.addAttribute("mensaje", "❌ Error al eliminar entrada: " + e.getMessage());
        }

        return "redirect:/entrada/listar";
    }

    // =============================================================
    // ========== Generación de comprobante en .txt ================
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
