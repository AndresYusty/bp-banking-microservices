package com.banking.cuentaservice.infrastructure.controller;

import com.banking.cuentaservice.application.dto.EstadoCuentaDTO;
import com.banking.cuentaservice.application.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping
    public ResponseEntity<EstadoCuentaDTO> generarEstadoCuenta(
            @RequestParam("cliente") String clienteId,
            @RequestParam("fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam("fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        
        EstadoCuentaDTO estadoCuenta = reporteService.generarEstadoCuenta(clienteId, fechaInicio, fechaFin);
        return ResponseEntity.ok(estadoCuenta);
    }
}

