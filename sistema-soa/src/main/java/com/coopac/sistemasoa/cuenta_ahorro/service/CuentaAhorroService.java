package com.coopac.sistemasoa.cuenta_ahorro.service;

import com.coopac.sistemasoa.cuenta_ahorro.model.CuentaAhorro;
import com.coopac.sistemasoa.cuentaahorro.model.dto.CuentaAhorroDTO;
import com.coopac.sistemasoa.cuenta_ahorro.repository.CuentaAhorroRepository;
import com.coopac.sistemasoa.empleado.model.Trabajador;
import com.coopac.sistemasoa.empleado.repository.TrabajadorRepository;
import com.coopac.sistemasoa.socio.model.Socio;
import com.coopac.sistemasoa.socio.repository.SocioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.stream.Collectors;
import com.coopac.sistemasoa.cuentaahorro.model.dto.CuentaAhorroDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class CuentaAhorroService {

    @Autowired
    private CuentaAhorroRepository cuentaRepository;

    @Autowired
    private SocioRepository socioRepository;

    @Autowired
    private TrabajadorRepository trabajadorRepository;

    @Transactional
    public CuentaAhorroDTO registrar(CuentaAhorroDTO dto) {
        // 1. Convertir DTO -> Entidad (Mapping de Entrada)
        CuentaAhorro entidad = new CuentaAhorro();
        Socio socioEncontrado = socioRepository.findById(dto.getCodSocio())
                .orElseThrow(() -> new RuntimeException("No existe el socio con ID: " + dto.getCodSocio()));
        entidad.setSocio(socioEncontrado); // ¡Ahora sí pasamos un Objeto Socio!

        // B. Buscamos al Trabajador (Manejando que pueda ser nulo si el DTO lo permite)
        if (dto.getCodTrabajador() != null) {
            Trabajador trabajadorEncontrado = trabajadorRepository.findById(dto.getCodTrabajador())
                    .orElseThrow(() -> new RuntimeException("No existe trabajador con ID: " + dto.getCodTrabajador()));
            entidad.setTrabajador(trabajadorEncontrado);
        }

        entidad.setNumeroCuenta(dto.getNumeroCuenta());

        if (dto.getSaldo() != null) {
            entidad.setSaldo(BigDecimal.valueOf(dto.getSaldo()));
        } else {
            entidad.setSaldo(BigDecimal.ZERO);
        }

        entidad.setFechaApertura(LocalDate.now());

        CuentaAhorro cuentaGuardada = cuentaRepository.save(entidad);
        dto.setCodCuenta(cuentaGuardada.getCodCuenta());
        dto.setFechaApertura(cuentaGuardada.getFechaApertura());

        return dto;
    }

    public List<CuentaAhorroDTO> listarTodas() {
        List<CuentaAhorro> entidades = cuentaRepository.findAll();

        return entidades.stream().map(entidad -> {
            CuentaAhorroDTO dto = new CuentaAhorroDTO();

            // 1. Datos básicos
            dto.setCodCuenta(entidad.getCodCuenta());
            dto.setNumeroCuenta(entidad.getNumeroCuenta());
            dto.setFechaApertura(entidad.getFechaApertura()); // LocalDate correcto
            dto.setSaldo(entidad.getSaldo() != null ? entidad.getSaldo().doubleValue() : 0.0);

            // 2. Mapeo del SOCIO (Aquí está la clave para que se vea el DNI y Nombre)
            if (entidad.getSocio() != null) {
                // Llenamos el ID
                dto.setCodSocio(entidad.getSocio().getCodSocio());

                // Llenamos el DNI
                dto.setDni(entidad.getSocio().getDni());

                // Llenamos el Nombre Completo
                String nombreArmado = entidad.getSocio().getNombres() + " " +
                        entidad.getSocio().getApellidoPaterno() + " " +
                        entidad.getSocio().getApellidoMaterno();
                dto.setNombreCompleto(nombreArmado.trim());
            }

            // 3. Mapeo del Trabajador
            if (entidad.getTrabajador() != null) {
                dto.setCodTrabajador(entidad.getTrabajador().getCodTrabajador());
            }

            return dto;
        }).collect(Collectors.toList());
    }

    public CuentaAhorro obtenerPorId(Integer id) {
        return cuentaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada con ID: " + id));
    }

    @Transactional
    public void depositar(Integer id, BigDecimal monto) {
        CuentaAhorro cuenta = obtenerPorId(id);

        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("El monto debe ser mayor a 0");
        }

        BigDecimal saldoActual = cuenta.getSaldo() != null ? cuenta.getSaldo() : BigDecimal.ZERO;
        cuenta.setSaldo(saldoActual.add(monto));

        cuentaRepository.save(cuenta);
    }

    @Transactional
    public void retirar(Integer id, BigDecimal monto) {
        CuentaAhorro cuenta = obtenerPorId(id);
        BigDecimal saldoActual = cuenta.getSaldo() != null ? cuenta.getSaldo() : BigDecimal.ZERO;

        if (saldoActual.compareTo(monto) < 0) {
            throw new RuntimeException("Saldo insuficiente para realizar el retiro.");
        }

        cuenta.setSaldo(saldoActual.subtract(monto));
        cuentaRepository.save(cuenta);
    }
}