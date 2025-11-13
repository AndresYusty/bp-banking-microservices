package com.banking.cuentaservice.infrastructure.controller;

import com.banking.cuentaservice.application.dto.MovimientoDTO;
import com.banking.cuentaservice.application.service.MovimientoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movimientos")
@RequiredArgsConstructor
public class MovimientoController {

    private final MovimientoService movimientoService;

    @PostMapping
    public ResponseEntity<MovimientoDTO> crearMovimiento(@Valid @RequestBody MovimientoDTO movimientoDTO) {
        MovimientoDTO movimientoCreado = movimientoService.crearMovimiento(movimientoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(movimientoCreado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovimientoDTO> obtenerMovimientoPorId(@PathVariable Long id) {
        MovimientoDTO movimiento = movimientoService.obtenerMovimientoPorId(id);
        return ResponseEntity.ok(movimiento);
    }

    @GetMapping
    public ResponseEntity<List<MovimientoDTO>> obtenerTodosLosMovimientos() {
        List<MovimientoDTO> movimientos = movimientoService.obtenerTodosLosMovimientos();
        return ResponseEntity.ok(movimientos);
    }

    @GetMapping("/cuenta/{cuentaId}")
    public ResponseEntity<List<MovimientoDTO>> obtenerMovimientosPorCuenta(@PathVariable Long cuentaId) {
        List<MovimientoDTO> movimientos = movimientoService.obtenerMovimientosPorCuenta(cuentaId);
        return ResponseEntity.ok(movimientos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovimientoDTO> actualizarMovimiento(
            @PathVariable Long id,
            @Valid @RequestBody MovimientoDTO movimientoDTO) {
        MovimientoDTO movimientoActualizado = movimientoService.actualizarMovimiento(id, movimientoDTO);
        return ResponseEntity.ok(movimientoActualizado);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MovimientoDTO> actualizarMovimientoParcial(
            @PathVariable Long id,
            @RequestBody MovimientoDTO movimientoDTO) {
        MovimientoDTO movimientoExistente = movimientoService.obtenerMovimientoPorId(id);
        
        if (movimientoDTO.getTipoMovimiento() != null) {
            movimientoExistente.setTipoMovimiento(movimientoDTO.getTipoMovimiento());
        }
        if (movimientoDTO.getValor() != null) {
            movimientoExistente.setValor(movimientoDTO.getValor());
        }
        if (movimientoDTO.getSaldo() != null) {
            movimientoExistente.setSaldo(movimientoDTO.getSaldo());
        }

        MovimientoDTO movimientoActualizado = movimientoService.actualizarMovimiento(id, movimientoExistente);
        return ResponseEntity.ok(movimientoActualizado);
    }
}

