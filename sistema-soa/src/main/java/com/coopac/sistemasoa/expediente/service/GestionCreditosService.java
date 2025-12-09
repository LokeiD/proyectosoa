package com.coopac.sistemasoa.expediente.service;

import com.coopac.sistemasoa.dto.CargaComboBoxFormularioDTO;
import com.coopac.sistemasoa.dto.SolicitudCreditoDTO;
import com.coopac.sistemasoa.empleado.repository.TrabajadorRepository;
import com.coopac.sistemasoa.exception.SoaException;
import com.coopac.sistemasoa.expediente.model.ExpedienteCredito;
import com.coopac.sistemasoa.expediente.repository.*;
import com.coopac.sistemasoa.socio.model.Socio;
import com.coopac.sistemasoa.socio.repository.SocioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class GestionCreditosService {


    @Autowired private ExpedienteCreditoRepository expedienteRepository;
    @Autowired private SocioRepository socioRepository;
    @Autowired private ProductoCrediticioRepository productoRepository;
    @Autowired private RecurrenciaRepository recurrenciaRepository;
    @Autowired private RiesgoRepository riesgoRepository;
    @Autowired private PeriodoRepository periodoRepository;
    @Autowired private TrabajadorRepository trabajadorRepository;

    public CargaComboBoxFormularioDTO obtenerDatosCombos() {
        CargaComboBoxFormularioDTO dto = new CargaComboBoxFormularioDTO();
        dto.setProductos(productoRepository.findAll());
        dto.setRecurrencias(recurrenciaRepository.findAll());
        dto.setRiesgos(riesgoRepository.findAll());
        dto.setPeriodos(periodoRepository.findAll());
        return dto;
    }

    @Transactional
    public ExpedienteCredito registrarSolicitud(SolicitudCreditoDTO dto) {

        Socio socio = socioRepository.findByDni(dto.getDniSocio())
                .orElseThrow(() -> new SoaException("404","No se encontró socio con DNI: " + dto.getDniSocio()));

        ExpedienteCredito expediente = new ExpedienteCredito();
        expediente.setSocio(socio);
        expediente.setRecurrencia(recurrenciaRepository.getReferenceById(dto.getCodRecurrencia()));
        expediente.setRiesgo(riesgoRepository.getReferenceById(dto.getCodRiesgo()));
        expediente.setPeriodo(periodoRepository.getReferenceById(dto.getCodPeriodo()));
        expediente.setProducto(productoRepository.getReferenceById(dto.getCodProducto()));
        expediente.setTrabajador(trabajadorRepository.getReferenceById(dto.getCodTrabajador()));
        expediente.setMontoSolicitado(dto.getMontoSolicitado());
        expediente.setActividad(dto.getActividad());
        expediente.setFechaSolicitud(LocalDate.now());

        return expedienteRepository.save(expediente);
    }

    public List<ExpedienteCredito> listarTodas() {
        return expedienteRepository.findAll();
    }
}