package com.example.GameVault.service;

import com.example.GameVault.model.Juego;
import com.example.GameVault.repository.JuegoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class JuegoService {

    @Autowired
    private JuegoRepository juegoRepository;

    private static final String UPLOAD_DIR = "src/main/resources/static/uploads";

    public List<Juego> listarTodos() {
        return juegoRepository.findAll();
    }

    public List<Juego> buscarPorTitulo(String titulo) {
        return juegoRepository.findByTituloContainingIgnoreCase(titulo);
    }

    public List<Juego> buscarPorCategoria(String categoria) {
        return juegoRepository.findByCategoria(categoria);
    }

    public List<Juego> buscarPorPrecio(Double precio) {
        return juegoRepository.findByPrecioLessThanEqual(precio);
    }

    public Juego obtenerPorId(Long id) {
        return juegoRepository.findById(id).orElse(null);
    }

    public void guardarJuego(Juego juego, MultipartFile file) {
        String nombreArchivo = "default.png";

        if (file != null && !file.isEmpty()) {
            nombreArchivo = guardarImagenEnLocal(file);
        }

        juego.setPortadaUrl(nombreArchivo);
        juegoRepository.save(juego);
    }

    public void actualizarJuego(Long id, Juego juegoActualizado, MultipartFile file) {
        Juego juegoExistente = obtenerPorId(id);

        if (juegoExistente != null) {
            juegoExistente.setTitulo(juegoActualizado.getTitulo());
            juegoExistente.setDescripcion(juegoActualizado.getDescripcion());
            juegoExistente.setCategoria(juegoActualizado.getCategoria());
            juegoExistente.setPrecio(juegoActualizado.getPrecio());

            if (file != null && !file.isEmpty()) {
                String nombreArchivo = guardarImagenEnLocal(file);
                juegoExistente.setPortadaUrl(nombreArchivo);
            }

            juegoRepository.save(juegoExistente);
        }
    }

    public void eliminarJuego(Long id) {
        juegoRepository.deleteById(id);
    }

    private String guardarImagenEnLocal(MultipartFile file) {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String nombreArchivo = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

            Path filePath = uploadPath.resolve(nombreArchivo);

            Files.copy(file.getInputStream(), filePath);

            return nombreArchivo;

        } catch (IOException e) {
            e.printStackTrace();
            return "default.png";
        }
    }
}



