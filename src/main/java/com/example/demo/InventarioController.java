package com.example.demo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.demo.entidades.Producto;
import com.example.demo.repository.ProductoRepository;

@Controller
@RequestMapping("/Inventario")
public class InventarioController {

    private final ProductoRepository productoRepository;

    @Autowired
    public InventarioController(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @GetMapping
    public String mostrarInventario(Model model) {
        model.addAttribute("productos", productoRepository.findAll());
        return "inventario";
    }

    @PostMapping("/agregar")
    public String agregarProducto(@RequestParam("nombre") String nombre,
                                @RequestParam("cantidad") int cantidad,
                                @RequestParam("precio") double precio,
                                RedirectAttributes redirectAttributes) {
        Producto nuevoProducto = new Producto();
        nuevoProducto.setNombre(nombre);
        nuevoProducto.setCantidad(cantidad);
        nuevoProducto.setPrecio(precio);
        productoRepository.save(nuevoProducto);
        redirectAttributes.addFlashAttribute("mensaje", "Producto agregado exitosamente");
        return "redirect:/Inventario";
    }


}