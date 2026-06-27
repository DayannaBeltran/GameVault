package com.example.GameVault.controller;


import com.example.GameVault.model.Juego;
import com.example.GameVault.service.JuegoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ui.Model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
public class GameController {

    @Autowired
    private JuegoService juegoService;

    @GetMapping("/fragments-demo")
    public String fragmentsDemo() {
        return "fragments-demo";
    }

    @GetMapping({"/", "/juegos"})
    public String listarJuegos(@RequestParam(required = false) String titulo,
                               @RequestParam(required = false) String categoria,
                               @RequestParam(required = false) Double precioMax,
                               Model model) {

        List<Juego> juegos;

        if (titulo != null && !titulo.isBlank()) {
            juegos = juegoService.buscarPorTitulo(titulo);
        } else if (categoria != null && !categoria.isBlank()) {
            juegos = juegoService.buscarPorCategoria(categoria);
        } else if (precioMax != null) {
            juegos = juegoService.buscarPorPrecio(precioMax);
        } else {
            juegos = juegoService.listarTodos();
        }

        model.addAttribute("juegos", juegos);
        return "juegos";
    }

    @GetMapping("/mis-juegos")
    public String misJuegos(Model model) {
        model.addAttribute("juegos", juegoService.listarTodos());
        return "mis-juegos";
    }

    @GetMapping("/juegos/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("juego", new Juego());
        return "formulario";
    }

    @PostMapping("/juegos")
    public String guardarJuego(@RequestParam("titulo") String titulo,
                               @RequestParam("descripcion") String descripcion,
                               @RequestParam("categoria") String categoria,
                               @RequestParam("precio") Double precio,
                               @RequestParam("portada") MultipartFile portada) {

        Juego nuevoJuego = new Juego();
        nuevoJuego.setTitulo(titulo);
        nuevoJuego.setDescripcion(descripcion);
        nuevoJuego.setCategoria(categoria);
        nuevoJuego.setPrecio(precio);

        juegoService.guardarJuego(nuevoJuego, portada);

        return "redirect:/mis-juegos";
    }

    @GetMapping("/juegos/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Juego juego = juegoService.obtenerPorId(id);

        if (juego == null) {
            return "redirect:/mis-juegos";
        }

        model.addAttribute("juego", juego);
        return "formulario";
    }

    @PostMapping("/juegos/actualizar/{id}")
    public String actualizarJuego(@PathVariable Long id,
                                  @RequestParam("titulo") String titulo,
                                  @RequestParam("descripcion") String descripcion,
                                  @RequestParam("categoria") String categoria,
                                  @RequestParam("precio") Double precio,
                                  @RequestParam("portada") MultipartFile portada) {

        Juego juegoActualizado = new Juego();
        juegoActualizado.setTitulo(titulo);
        juegoActualizado.setDescripcion(descripcion);
        juegoActualizado.setCategoria(categoria);
        juegoActualizado.setPrecio(precio);

        juegoService.actualizarJuego(id, juegoActualizado, portada);

        return "redirect:/mis-juegos";
    }

    @PostMapping("/juegos/eliminar/{id}")
    public String eliminarJuego(@PathVariable Long id) {
        juegoService.eliminarJuego(id);
        return "redirect:/mis-juegos";
    }
}


