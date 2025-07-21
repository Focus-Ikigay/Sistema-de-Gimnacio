package com.example.demo;

import com.example.demo.entidades.Producto;
import com.example.demo.repository.ProductoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequestMapping("/Inventario")
public class InventarioController {

    private final ProductoRepository productoRepository;

    public InventarioController(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    // Mostrar todos los productos
    @GetMapping
    public String mostrarInventario(Model model) {
        model.addAttribute("productos", productoRepository.findAll());
        model.addAttribute("producto", new Producto()); // Necesario para el formulario modal
        return "inventario";
    }

    // Agregar nuevo producto
    @PostMapping("/agregar")
    public String agregarProducto(@ModelAttribute Producto producto) {
        if (producto.getFechaIngreso() == null) {
            producto.setFechaIngreso(LocalDate.now());
        }
        productoRepository.save(producto);
        return "redirect:/Inventario";
    }

    // Eliminar producto
    @PostMapping("/eliminar")
    public String eliminarProducto(@RequestParam("id") Long id) {
        productoRepository.deleteById(id);
        return "redirect:/Inventario";
    }

    // Ver detalles
    @GetMapping("/detalles")
    public String verDetalles(@RequestParam Long id, Model model) {
        Optional<Producto> producto = productoRepository.findById(id);
        if (producto.isPresent()) {
            model.addAttribute("producto", producto.get());
            return "detalle_producto"; // Asegúrate de tener este HTML
        } else {
            return "redirect:/Inventario";
        }
    }

    // Mostrar formulario de edición
    @GetMapping("/editar")
    public String mostrarEdicion(@RequestParam Long id, Model model) {
        Optional<Producto> producto = productoRepository.findById(id);
        if (producto.isPresent()) {
            model.addAttribute("producto", producto.get());
            return "editar_producto"; // Asegúrate de tener este HTML
        } else {
            return "redirect:/Inventario";
        }
    }

    // Editar producto (opcional, si lo haces en otra vista)
    @PostMapping("/editar")
    public String editarProducto(@RequestParam("id") Long id,
                                 @RequestParam("nombre") String nombre,
                                 @RequestParam("cantidad") int cantidad,
                                 @RequestParam("precio") double precio) {
        Producto producto = productoRepository.findById(id).orElse(null);
        if (producto != null) {
            producto.setNombre(nombre);
            producto.setCantidad(cantidad);
            producto.setPrecio(precio);
            productoRepository.save(producto);
        }
        return "redirect:/Inventario";
    }

    // Actualizar producto con todos los campos (por si usas un formulario con @ModelAttribute)
    @PostMapping("/actualizar")
    public String actualizarProducto(@ModelAttribute Producto producto) {
        productoRepository.save(producto);
        return "redirect:/Inventario";
    }
}