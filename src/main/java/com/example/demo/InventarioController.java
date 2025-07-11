package com.example.demo;
<<<<<<< HEAD
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.demo.entidades.Producto;
import com.example.demo.repository.ProductoRepository;
=======

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.demo.entidades.Producto;

import com.example.demo.repository.ProductoRepository;

>>>>>>> fcbb111 (version 3-3 sistema de gimnacios)

@Controller
@RequestMapping("/Inventario")
public class InventarioController {

    private final ProductoRepository productoRepository;
<<<<<<< HEAD

    @Autowired
    public InventarioController(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @GetMapping
    public String mostrarInventario(Model model) {
        model.addAttribute("productos", productoRepository.findAll());
        return "inventario";
=======

    public InventarioController(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @GetMapping
    public String mostrarInventario(Model model) {
        model.addAttribute("productos", productoRepository.findAll());
        return "inventario"; // nombre del archivo .html sin extensión
>>>>>>> fcbb111 (version 3-3 sistema de gimnacios)
    }

    @PostMapping("/agregar")
    public String agregarProducto(@RequestParam("nombre") String nombre,
<<<<<<< HEAD
                                @RequestParam("cantidad") int cantidad,
                                @RequestParam("precio") double precio,
                                RedirectAttributes redirectAttributes) {
=======
                                  @RequestParam("cantidad") int cantidad,
                                  @RequestParam("precio") double precio) {
>>>>>>> fcbb111 (version 3-3 sistema de gimnacios)
        Producto nuevoProducto = new Producto();
        nuevoProducto.setNombre(nombre);
        nuevoProducto.setCantidad(cantidad);
        nuevoProducto.setPrecio(precio);
        productoRepository.save(nuevoProducto);
<<<<<<< HEAD
        redirectAttributes.addFlashAttribute("mensaje", "Producto agregado exitosamente");
=======
>>>>>>> fcbb111 (version 3-3 sistema de gimnacios)
        return "redirect:/Inventario";
    }


}