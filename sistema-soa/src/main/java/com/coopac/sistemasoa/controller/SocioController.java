package com.coopac.sistemasoa.controller;

import com.coopac.sistemasoa.dto.SocioDTO;
import com.coopac.sistemasoa.service.SocioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/socios")
@CrossOrigin(origins = "*")
public class SocioController {

    @Autowired
    private SocioService socioService;

    @GetMapping("/buscar/{dni}")
    public ResponseEntity<?> buscarSocioPorDni(@PathVariable String dni) {
        try {
            SocioDTO info = socioService.obtenerInformacionParaSolicitud(dni);
            return ResponseEntity.ok(info);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error interno: " + e.getMessage());
        }
    }
}