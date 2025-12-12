package com.coopac.sistemasoa.expediente.service;

import com.coopac.sistemasoa.credito.model.Credito;
import com.coopac.sistemasoa.credito.repository.CreditoRepository;
import com.coopac.sistemasoa.evaluacion.model.dto.ExpedienteDetalleDTO;
import com.coopac.sistemasoa.evaluacion.model.dto.ExpedienteResumenDTO;
import com.coopac.sistemasoa.evaluacion.model.dto.RegistroEvaluacionRequest;
import com.coopac.sistemasoa.expediente.model.DocumentoExpediente;
import com.coopac.sistemasoa.expediente.model.Evaluacion;
import com.coopac.sistemasoa.expediente.model.ExpedienteCredito;
import com.coopac.sistemasoa.expediente.model.TipoDecision;
import com.coopac.sistemasoa.expediente.repository.EvaluacionRepository;
import com.coopac.sistemasoa.expediente.repository.ExpedienteCreditoRepository;
import com.coopac.sistemasoa.expediente.repository.TipoDecisionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EvaluacionService {

    @Autowired
    private ExpedienteCreditoRepository expedienteRepository;
    @Autowired
    private EvaluacionRepository evaluacionRepository;
    @Autowired
    private TipoDecisionRepository tipoDecisionRepository;
    @Autowired
    private CreditoRepository creditoRepository;

    public List<ExpedienteResumenDTO> listarPendientes() {
        // CORRECCIÓN: Usamos findByEstado(true) porque 'true' significa "En Evaluación"
        List<ExpedienteCredito> lista = expedienteRepository.findByEstado(true);

        return lista.stream()
                .map(this::mapToResumen)
                .collect(Collectors.toList());
    }

    // --- 2. VER DETALLE ---
    public ExpedienteDetalleDTO obtenerDetalle(Integer id) {
        ExpedienteCredito exp = expedienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expediente no encontrado"));
        return mapToDetalle(exp);
    }

    // --- 3. REGISTRAR EVALUACIÓN ---
    @Transactional
    public void registrarEvaluacion(RegistroEvaluacionRequest request) {

        ExpedienteCredito expediente = expedienteRepository.findById(request.getCodExpediente())
                .orElseThrow(() -> new RuntimeException("Expediente no encontrado"));

        TipoDecision tipoDecision = tipoDecisionRepository.findById(request.getCodTipoDecision())
                .orElseThrow(() -> new RuntimeException("Tipo de decisión inválido"));

        Evaluacion evaluacion = new Evaluacion();
        evaluacion.setExpediente(expediente);
        evaluacion.setTipoDecision(tipoDecision);
        evaluacion.setDescripcion(request.getObservacion());
        evaluacion.setFechaEnvio(LocalDateTime.now());


        evaluacionRepository.save(evaluacion);

        // B. Lógica de Negocio según la decisión
        // ID 1 = APROBADO (Verifica que en tu BD el ID 1 sea "APROBADO")
        if (tipoDecision.getCodTipoDecision() == 1) {
            crearCreditoAprobado(expediente, request);
            expediente.setEstado(true);
        } else if (tipoDecision.getCodTipoDecision() == 3) {
            expediente.setEstado(false);
        }

        expedienteRepository.save(expediente);
    }

    private void crearCreditoAprobado(ExpedienteCredito exp, RegistroEvaluacionRequest req) {
        Credito credito = new Credito();
        credito.setExpediente(exp);

        // Asignamos los datos financieros que vienen del DTO (request) a la entidad CREDITO
        credito.setMontoAprobado(req.getMontoAprobado());
        credito.setNumeroCuotas(req.getCuotasAprobadas());

        // Mapeo de tasas (TEM y TEA)
        if(req.getTemAprobado() != null) credito.setTasaMensual(req.getTemAprobado());
        if(req.getTeaAprobada() != null) credito.setTasaAnual(req.getTeaAprobada());

        credito.setDescripcionResolucion(req.getObservacion());
        credito.setEstado(true); // Activo

        // Reutilizamos el periodo del expediente original
        credito.setPeriodo(exp.getPeriodo());

        creditoRepository.save(credito);
    }

    private ExpedienteResumenDTO mapToResumen(ExpedienteCredito e) {
        ExpedienteResumenDTO dto = new ExpedienteResumenDTO();
        dto.setIdExpediente(e.getCodExpediente());
        dto.setNumeroExpediente("EXP-" + e.getCodExpediente());

        dto.setMontoSolicitado(e.getMontoSolicitado());
        if(e.getFechaSolicitud() != null) dto.setFechaSolicitud(e.getFechaSolicitud());

        if (e.getSocio() != null) {
            dto.setDni(e.getSocio().getDni());
            dto.setNombreCompleto(e.getSocio().getNombres() + " " + e.getSocio().getApellidoPaterno());
        }

        if (e.getProducto() != null) {
            dto.setProducto(e.getProducto().getNombreProducto());
        }
        if (e.getDocumentos() != null) {
            List<String> nombresArchivos = e.getDocumentos().stream()
                    .map(DocumentoExpediente::getNombreArchivo)
                    .collect(Collectors.toList());
            dto.setDocumentos(nombresArchivos);
        }

        return dto;
    }

    private ExpedienteDetalleDTO mapToDetalle(ExpedienteCredito e) {
        ExpedienteResumenDTO base = mapToResumen(e);
        ExpedienteDetalleDTO dto = new ExpedienteDetalleDTO();

        // Copia manual de propiedades
        dto.setIdExpediente(base.getIdExpediente());
        dto.setNumeroExpediente(base.getNumeroExpediente());
        dto.setMontoSolicitado(base.getMontoSolicitado());
        dto.setFechaSolicitud(base.getFechaSolicitud());
        dto.setDni(base.getDni());
        dto.setNombreCompleto(base.getNombreCompleto());
        dto.setProducto(base.getProducto());

        dto.setActividad(e.getActividad());


        if (e.getPeriodo() != null) {
            dto.setPeriodo(e.getPeriodo().getNombrePeriodo());
        }
        if (e.getRecurrencia() != null) {
            dto.setRecurrencia(e.getRecurrencia().getNombreRecurrencia());
        }
        if (e.getRiesgo() != null) {
            dto.setRiesgo(e.getRiesgo().getNombreRiesgo());
        }
        return dto;
    }
}