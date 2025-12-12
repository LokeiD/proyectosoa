package com.coopac.sistemasoa.expediente.service;

import com.coopac.sistemasoa.expediente.model.dto.*;
import com.coopac.sistemasoa.empleado.repository.TrabajadorRepository;
import com.coopac.sistemasoa.expediente.model.ExpedienteCredito;
import com.coopac.sistemasoa.expediente.repository.*;
import com.coopac.sistemasoa.socio.repository.SocioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GestionExpediente {


    @Autowired private ExpedienteCreditoRepository expedienteRepository;
    @Autowired private SocioRepository socioRepository;
    @Autowired private ProductoCrediticioRepository productoRepository;
    @Autowired private RecurrenciaRepository recurrenciaRepository;
    @Autowired private RiesgoRepository riesgoRepository;
    @Autowired private PeriodoRepository periodoRepository;
    @Autowired private TrabajadorRepository trabajadorRepository;

    public List<ProductoDTO> listarProductos() {
        return productoRepository.findAll().stream()
                .map(entity -> {
                    ProductoDTO dto = new ProductoDTO();
                    dto.setCodProducto(entity.getCodProducto());
                    dto.setNombre(entity.getNombreProducto());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<RecurrenciaDTO> listarRecurrencias() {
        return recurrenciaRepository.findAll().stream()
                .map(entity -> {
                    RecurrenciaDTO dto = new RecurrenciaDTO();
                    dto.setCodRecurrencia(entity.getCodRecurrencia());
                    dto.setNombre(entity.getNombreRecurrencia());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<RiesgoDTO> listarRiesgos() {
        return riesgoRepository.findAll().stream()
                .map(entity -> {
                    RiesgoDTO dto = new RiesgoDTO();
                    dto.setCodRiesgo(entity.getCodRiesgo());
                    dto.setDescripcion(entity.getNombreRiesgo());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<PeriodoDTO> listarPeriodos() {
        return periodoRepository.findAll().stream()
                .map(entity -> {
                    PeriodoDTO dto = new PeriodoDTO();
                    dto.setCodPeriodo(entity.getCodPeriodo());
                    dto.setDescripcion(entity.getNombrePeriodo());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<ExpedienteCredito> listarTodas() {
        return expedienteRepository.findByEstado(false);
    }

}