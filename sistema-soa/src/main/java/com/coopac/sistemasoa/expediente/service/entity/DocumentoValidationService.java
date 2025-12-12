package com.coopac.sistemasoa.expediente.service.entity;

import com.coopac.sistemasoa.expediente.model.ExpedienteCredito;
import com.coopac.sistemasoa.expediente.repository.ExpedienteCreditoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentoValidationService {

    @Autowired
    private ExpedienteCreditoRepository expedienteRepository;

    // Constante que define la regla de negocio: ¿Cuántos documentos son obligatorios?
    // Basado en tu JS (8 documentos requeridos)
    private static final int MIN_DOCUMENTOS_REQUERIDOS = 8;

    /**
     * SE-3: Validar Documentos Completos.
     * Esta es una Regla de Entidad, llamada por el Orquestador SN-2.
     * * @param idExpediente El código del expediente a validar.
     * @return El ExpedienteCredito si la validación es exitosa.
     */
    public ExpedienteCredito validarExpedienteCompleto(Integer idExpediente) {

        // 1. Buscar Expediente. Asumimos que la Entidad ExpedienteCredito tiene FetchType.EAGER
        // para su lista de documentos o que la Entidad ya está en contexto.
        ExpedienteCredito expediente = expedienteRepository.findById(idExpediente)
                .orElseThrow(() -> new RuntimeException("Error SE-3: Expediente no encontrado."));

        List<?> documentos = expediente.getDocumentos(); // Usamos List<?> para seguridad

        if (documentos == null || documentos.size() < MIN_DOCUMENTOS_REQUERIDOS) {

            // Lanza excepción si la regla SE-3 falla
            throw new RuntimeException(
                    "Error SE-3: No se puede enviar a evaluación. Faltan documentos obligatorios (" +
                            (documentos != null ? documentos.size() : 0) + " de " + MIN_DOCUMENTOS_REQUERIDOS + " cargados)."
            );
        }

        return expediente; // Retorna la entidad para ser usada por la Capa de Soporte (SS-2)
    }
}