package com.coopac.sistemasoa.expediente.service.business;

// Importaciones de las capas inferiores
import com.coopac.sistemasoa.expediente.model.ExpedienteCredito;
import com.coopac.sistemasoa.expediente.service.entity.DocumentoValidationService; // SE-3
import com.coopac.sistemasoa.expediente.service.support.ExpedienteSupportService; // SS-2

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExpedienteService { // SN-2 Orquestador de Envío a Evaluación

    // Inyecta las capas Entity y Support
    @Autowired
    private DocumentoValidationService documentoValidationService; // Regla SE-3

    @Autowired
    private ExpedienteSupportService expedienteSupportService; // Persistencia SS-2

    /**
     * SN-2: Registrar expediente para evaluación
     * Orquesta el flujo: SE-3 -> SS-2.
     */
    public void enviarAEvaluacion(Integer idExpediente) {

        // 1. Invoca SE-3: Validar Documentos Completos.
        // Si no están, lanza RuntimeException (el flujo se detiene).
        ExpedienteCredito expedienteValidado = documentoValidationService.validarExpedienteCompleto(idExpediente);

        // 2. Si la validación pasa, invoca SS-2: Actualizar Estado a 'En Evaluación'.
        expedienteSupportService.registrarEstadoEvaluacion(expedienteValidado);
    }
}