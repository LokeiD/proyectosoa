package com.coopac.sistemasoa.expediente.controller;

import com.coopac.sistemasoa.credito.model.dto.DocumentoExpedienteDTO;
import com.coopac.sistemasoa.expediente.service.DocumentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;

@RestController
// --- CORRECCIÓN AQUÍ ---
// Antes tenías: "/credito/documentos"
// Ahora ponemos la ruta completa que usa tu JS:
@RequestMapping("/api/v1/creditos/documentos")
@CrossOrigin("*")
public class DocumentoController {

    @Autowired
    private DocumentoService documentoService;

    @PostMapping(value = "/subir", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentoExpedienteDTO> subirDocumento(
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("codExpediente") Integer codExpediente,
            @RequestParam("tipoDoc") String tipoDoc) {

        DocumentoExpedienteDTO response = documentoService.guardarArchivo(archivo, codExpediente, tipoDoc);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ver/{filename:.+}")
    public ResponseEntity<Resource> verDocumento(@PathVariable String filename) throws IOException {

        Resource file = documentoService.cargarRecurso(filename);
        String contentType = Files.probeContentType(file.getFile().toPath());
        if (contentType == null) contentType = "application/octet-stream";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .body(file);
    }
}