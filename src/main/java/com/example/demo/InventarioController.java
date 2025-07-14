package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entidades.Producto;
import com.example.demo.repository.ProductoRepository;


@Controller
@RequestMapping("/Inventario")
public class InventarioController {

    private final ProductoRepository productoRepository;

    public InventarioController(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

	@GetMapping
    public String mostrarInventario(Model model) {
        model.addAttribute("productos", productoRepository.findAll());
        return "inventario"; // nombre del archivo .html sin extensión
    }

    @PostMapping("/agregar")
    public String agregarProducto1(@RequestParam("nombre") String nombre,
                                  @RequestParam("cantidad") int cantidad,
                                  @RequestParam("precio") double precio) {
        Producto nuevoProducto = new Producto();
        nuevoProducto.setNombre(nombre);
        nuevoProducto.setCantidad(cantidad);
        nuevoProducto.setPrecio(precio);
        productoRepository.save(nuevoProducto);
        return "redirect:/Inventario";
    }
}