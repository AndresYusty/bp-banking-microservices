package com.banking.cuentaservice.application.service;

import com.banking.cuentaservice.application.dto.MovimientoDTO;
import com.banking.cuentaservice.application.exception.CuentaNotFoundException;
import com.banking.cuentaservice.application.exception.SaldoNoDisponibleException;
import com.banking.cuentaservice.domain.model.Cuenta;
import com.banking.cuentaservice.domain.model.Movimiento;
import com.banking.cuentaservice.domain.repository.CuentaRepository;
import com.banking.cuentaservice.domain.repository.MovimientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final CuentaRepository cuentaRepository;

    public MovimientoDTO crearMovimiento(MovimientoDTO movimientoDTO) {
        Cuenta cuenta = cuentaRepository.findByNumeroCuenta(movimientoDTO.getNumeroCuenta())
            .orElseThrow(() -> new CuentaNotFoundException(
                "Cuenta no encontrada con número: " + movimientoDTO.getNumeroCuenta()
            ));

        if (!cuenta.getEstado()) {
            throw new RuntimeException("La cuenta está inactiva");
        }

        BigDecimal valor = movimientoDTO.getValor();
        BigDecimal saldoActual = cuenta.getSaldoDisponible();
        BigDecimal nuevoSaldo;

        // Determinar si es depósito (positivo) o retiro (negativo)
        if ("RETIRO".equalsIgnoreCase(movimientoDTO.getTipoMovimiento())) {
            // Para retiro, el valor debe ser positivo pero se resta del saldo
            if (valor.compareTo(BigDecimal.ZERO) < 0) {
                valor = valor.abs(); // Convertir a positivo si viene negativo
            }
            
            nuevoSaldo = saldoActual.subtract(valor);
            
            if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
                throw new SaldoNoDisponibleException("Saldo no disponible");
            }
        } else if ("DEPOSITO".equalsIgnoreCase(movimientoDTO.getTipoMovimiento())) {
            // Para depósito, el valor debe ser positivo
            if (valor.compareTo(BigDecimal.ZERO) < 0) {
                valor = valor.abs();
            }
            nuevoSaldo = saldoActual.add(valor);
        } else {
            throw new RuntimeException("Tipo de movimiento no válido. Use DEPOSITO o RETIRO");
        }

        // Actualizar saldo disponible de la cuenta
        cuenta.setSaldoDisponible(nuevoSaldo);
        cuentaRepository.save(cuenta);

        // Crear el movimiento
        Movimiento movimiento = new Movimiento();
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setTipoMovimiento(movimientoDTO.getTipoMovimiento().toUpperCase());
        movimiento.setValor(valor);
        movimiento.setSaldo(nuevoSaldo);
        movimiento.setCuenta(cuenta);

        Movimiento savedMovimiento = movimientoRepository.save(movimiento);
        return mapToDTO(savedMovimiento);
    }

    @Transactional(readOnly = true)
    public MovimientoDTO obtenerMovimientoPorId(Long id) {
        Movimiento movimiento = movimientoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Movimiento no encontrado con id: " + id));
        return mapToDTO(movimiento);
    }

    @Transactional(readOnly = true)
    public List<MovimientoDTO> obtenerTodosLosMovimientos() {
        return movimientoRepository.findAll().stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MovimientoDTO> obtenerMovimientosPorCuenta(Long cuentaId) {
        return movimientoRepository.findByCuenta_CuentaId(cuentaId).stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    public MovimientoDTO actualizarMovimiento(Long id, MovimientoDTO movimientoDTO) {
        Movimiento movimiento = movimientoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Movimiento no encontrado con id: " + id));

        movimiento.setTipoMovimiento(movimientoDTO.getTipoMovimiento());
        movimiento.setValor(movimientoDTO.getValor());
        movimiento.setSaldo(movimientoDTO.getSaldo());

        Movimiento updatedMovimiento = movimientoRepository.save(movimiento);
        return mapToDTO(updatedMovimiento);
    }

    private MovimientoDTO mapToDTO(Movimiento movimiento) {
        MovimientoDTO dto = new MovimientoDTO();
        dto.setMovimientoId(movimiento.getMovimientoId());
        dto.setFecha(movimiento.getFecha());
        dto.setTipoMovimiento(movimiento.getTipoMovimiento());
        dto.setValor(movimiento.getValor());
        dto.setSaldo(movimiento.getSaldo());
        dto.setNumeroCuenta(movimiento.getCuenta().getNumeroCuenta());
        dto.setCuentaId(movimiento.getCuenta().getCuentaId());
        return dto;
    }
}

