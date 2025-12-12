package com.coopac.sistemasoa.expediente.service.support;

import com.coopac.sistemasoa.expediente.model.dto.DocumentoExpedienteDTO;
import com.coopac.sistemasoa.exception.SoaException;
import com.coopac.sistemasoa.expediente.model.DocumentoExpediente;
import com.coopac.sistemasoa.expediente.model.ExpedienteCredito;
import com.coopac.sistemasoa.expediente.repository.DocumentoExpedienteRepository;
import com.coopac.sistemasoa.expediente.repository.ExpedienteCreditoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class DocumentoSupportService {

    // Carpeta raíz donde se guardarán los archivos
    private final Path rootLocation = Paths.get("archivos-expedientes");

    @Autowired private DocumentoExpedienteRepository documentoRepository;
    @Autowired private ExpedienteCreditoRepository expedienteRepository;

    // Constructor: Asegura que la carpeta exista al iniciar el servicio
    public DocumentoSupportService() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            // Error fatal si no se puede escribir en disco
            throw new RuntimeException("Error crítico: No se pudo crear el directorio de archivos 'archivos-expedientes'");
        }
    }

    @Transactional
    public DocumentoExpedienteDTO guardarArchivo(MultipartFile file, Integer codExpediente, String tipoDoc) {

        // 1. Validaciones iniciales
        if (file.isEmpty()) {
            throw new SoaException("400", "El archivo proporcionado está vacío.");
        }

        // 2. Buscar expediente (Si no existe, lanzamos 404)
        ExpedienteCredito expediente = expedienteRepository.findById(codExpediente)
                .orElseThrow(() -> new SoaException("404", "No se encontró el expediente con ID: " + codExpediente));

        try {
            // 3. Generar Nombre Único (Lógica de Convención)
            // FORMATO: EXP_{ID}_{TIPO}_{NOMBRE_ORIGINAL}
            // Esto permite al frontend saber qué tipo de documento es solo leyendo el nombre.
            String nombreOriginal = file.getOriginalFilename();
            // Limpiamos el nombre original de espacios raros por seguridad
            if (nombreOriginal != null) nombreOriginal = nombreOriginal.replaceAll("\\s+", "_");

            String nombreFinal = "EXP_" + codExpediente + "_" + tipoDoc + "_" + nombreOriginal;

            // 4. Guardar archivo físico (Sobrescribe si ya existe)
            Files.copy(file.getInputStream(), rootLocation.resolve(nombreFinal), StandardCopyOption.REPLACE_EXISTING);

            // 5. Guardar metadatos en Base de Datos
            // Nota: Aquí podrías verificar si ya existe un registro previo para actualizarlo,
            // pero para simplificar insertamos uno nuevo que apunta al mismo archivo físico.
            DocumentoExpediente doc = new DocumentoExpediente();
            doc.setExpediente(expediente);
            doc.setNombreArchivo(nombreFinal);
            doc.setFechaCarga(LocalDateTime.now());

            DocumentoExpediente guardado = documentoRepository.save(doc);

            // 6. Retornar DTO (Mapeado)
            return mapearADTO(guardado, tipoDoc, codExpediente);

        } catch (IOException e) {
            // Error técnico de I/O -> Lo envolvemos en 500
            throw new SoaException("500", "Error interno al escribir el archivo en disco: " + e.getMessage());
        }
    }

    public Resource cargarRecurso(String filename) {
        try {
            Path file = rootLocation.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new SoaException("404", "El archivo solicitado no existe o no se puede leer: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new SoaException("400", "La ruta del archivo es inválida: " + e.getMessage());
        }
    }

    // --- Método Privado de Mapeo (Entity -> DTO) ---
    private DocumentoExpedienteDTO mapearADTO(DocumentoExpediente entity, String tipoDetectado, Integer codExpediente) {
        DocumentoExpedienteDTO dto = new DocumentoExpedienteDTO();
        dto.setCodDocumento(entity.getCodDocumento());
        dto.setNombreArchivo(entity.getNombreArchivo());

        // Convertimos LocalDateTime a OffsetDateTime (si Swagger generó OffsetDateTime)
        if (entity.getFechaCarga() != null) {
            dto.setFechaCarga(entity.getFechaCarga().atZone(ZoneId.systemDefault()).toOffsetDateTime());
        }

        dto.setCodExpediente(codExpediente);
        dto.setTipoDetectado(tipoDetectado); // Devolvemos el tipo que nos enviaron para confirmar
        return dto;
    }

    // --- MÉTODO PARA INTEGRACIONES (Guarda archivos desde memoria/bytes) ---
    @Transactional
    public DocumentoExpedienteDTO guardarArchivoDesdeBytes(byte[] contenido, String nombreArchivo, Integer codExpediente, String tipoDoc) {
        if (contenido == null || contenido.length == 0) {
            throw new SoaException("400", "El documento recibido de la integración está vacío.");
        }

        // 1. Validar Expediente
        ExpedienteCredito expediente = expedienteRepository.findById(codExpediente)
                .orElseThrow(() -> new SoaException("404", "No se encontró el expediente: " + codExpediente));

        try {
            // 2. Generar nombre físico (Convención: EXP_ID_TIPO_NOMBRE)
            String nombreFinal = "EXP_" + codExpediente + "_" + tipoDoc + "_" + nombreArchivo;

            // 3. Escribir los bytes en el disco (Sobrescribe si existe)
            Files.write(rootLocation.resolve(nombreFinal), contenido, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            // 4. Guardar en BD (Registro)
            DocumentoExpediente doc = new DocumentoExpediente();
            doc.setExpediente(expediente);
            doc.setNombreArchivo(nombreFinal);
            doc.setFechaCarga(LocalDateTime.now());

            DocumentoExpediente guardado = documentoRepository.save(doc);

            // 5. Mapear a DTO para devolver la respuesta
            return mapearADTO(guardado, tipoDoc, codExpediente);

        } catch (IOException e) {
            throw new SoaException("500", "Error interno al guardar el archivo de integración: " + e.getMessage());
        }
    }
}

