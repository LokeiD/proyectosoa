package com.coopac.sistemasoa.expediente.service.support;

// DTO generado por el nuevo YAML
import com.coopac.sistemasoa.expediente.model.dto.SolicitudCreditoRequest;
import com.coopac.sistemasoa.expediente.model.ExpedienteCredito; // Modelo de tu BD

// Repositorios de las Entidades
import com.coopac.sistemasoa.expediente.repository.ExpedienteCreditoRepository;
import com.coopac.sistemasoa.expediente.repository.ProductoCrediticioRepository;
import com.coopac.sistemasoa.expediente.repository.RecurrenciaRepository;
import com.coopac.sistemasoa.expediente.repository.RiesgoRepository;
import com.coopac.sistemasoa.expediente.repository.PeriodoRepository;
import com.coopac.sistemasoa.empleado.repository.TrabajadorRepository; // Asumo esta ubicación

import com.coopac.sistemasoa.socio.model.Socio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class ExpedienteSupportService {

    // Inyección de Repositorios NECESARIOS para las FKs
    @Autowired private ExpedienteCreditoRepository expedienteRepository;
    @Autowired private ProductoCrediticioRepository productoRepository;
    @Autowired private RecurrenciaRepository recurrenciaRepository;
    @Autowired private RiesgoRepository riesgoRepository;
    @Autowired private PeriodoRepository periodoRepository;
    @Autowired private TrabajadorRepository trabajadorRepository;


    /**
     * SS-1: Generar expediente de crédito
     * Se encarga únicamente de la persistencia de la Entidad ExpedienteCredito.
     */
    @Transactional
    public void generarExpediente(SolicitudCreditoRequest request, Socio socio) {

        ExpedienteCredito expediente = new ExpedienteCredito();

        // Mapeo de datos validados y directos
        expediente.setSocio(socio); // Socio ya validado por SE-1

        // El DTO trae Double/float, pero la Entidad usa BigDecimal
        if(request.getMontoSolicitado() != null) {
            expediente.setMontoSolicitado(BigDecimal.valueOf(request.getMontoSolicitado()));
        } else {
            expediente.setMontoSolicitado(BigDecimal.ZERO);
        }

        expediente.setActividad(request.getActividad());
        expediente.setFechaSolicitud(LocalDate.now());
        expediente.setEstado(false); // Estado inicial: Pendiente/Borrador

        // Mapeo de FKs: Usamos getReferenceById para optimizar la inserción (No hace SELECT previo)

        if (request.getCodProducto() != null) {
            expediente.setProducto(productoRepository.getReferenceById(request.getCodProducto()));
        }

        if (request.getCodRecurrencia() != null) {
            expediente.setRecurrencia(recurrenciaRepository.getReferenceById(request.getCodRecurrencia()));
        }

        if (request.getCodRiesgo() != null) {
            expediente.setRiesgo(riesgoRepository.getReferenceById(request.getCodRiesgo()));
        }

        if (request.getCodPeriodo() != null) {
            expediente.setPeriodo(periodoRepository.getReferenceById(request.getCodPeriodo()));
        }

        if (request.getCodTrabajador() != null) {
            expediente.setTrabajador(trabajadorRepository.getReferenceById(request.getCodTrabajador()));
        }

        // Persistencia Final
        expedienteRepository.save(expediente);
    }

    @Transactional
    public void registrarEstadoEvaluacion(ExpedienteCredito expediente) {

        // El estado TRUE significa "En Evaluación"
        expediente.setEstado(true);

        // No necesitamos buscarlo de nuevo, ya que la entidad viene adjunta del ORM
        // o es la que se recuperó en la capa de Entidad (SE-3).
        expedienteRepository.save(expediente);
    }
}