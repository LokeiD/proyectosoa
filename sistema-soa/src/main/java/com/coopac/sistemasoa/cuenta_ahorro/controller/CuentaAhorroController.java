package com.coopac.sistemasoa.cuenta_ahorro.controller;

import com.coopac.sistemasoa.cuenta_ahorro.service.CuentaAhorroService;
import com.coopac.sistemasoa.cuentaahorro.model.dto.CuentaAhorroDTO;
import com.coopac.sistemasoa.cuenta_ahorro.model.CuentaAhorro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/cuentas-ahorro")
public class CuentaAhorroController {

    @Autowired
    private CuentaAhorroService cuentaService;


    @PostMapping
    public ResponseEntity<CuentaAhorroDTO> crear(@RequestBody CuentaAhorroDTO dto) {
        CuentaAhorroDTO cuentaCreada = cuentaService.registrar(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(cuentaCreada);
    }

    @GetMapping
    public ResponseEntity<List<CuentaAhorroDTO>> listar() { // <--- Cambio aquí
        return ResponseEntity.ok(cuentaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CuentaAhorro> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(cuentaService.obtenerPorId(id));
    }

    @PostMapping("/{id}/deposito")
    public ResponseEntity<Map<String, String>> registrarDeposito(
            @PathVariable Integer id,
            @RequestParam BigDecimal monto) {

        cuentaService.depositar(id, monto);

        return ResponseEntity.ok(Map.of("mensaje", "Depósito realizado correctamente"));
    }

    @PostMapping("/{id}/retiro")
    public ResponseEntity<Map<String, String>> registrarRetiro(
            @PathVariable Integer id,
            @RequestParam BigDecimal monto) {

        cuentaService.retirar(id, monto);

        return ResponseEntity.ok(Map.of("mensaje", "Retiro realizado correctamente"));
    }
}