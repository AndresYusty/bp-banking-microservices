package com.banking.cuentaservice.application.service;

import com.banking.cuentaservice.application.dto.CuentaDTO;
import com.banking.cuentaservice.application.dto.MovimientoDTO;
import com.banking.cuentaservice.application.exception.SaldoNoDisponibleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Pruebas de integración para MovimientoService")
class MovimientoServiceIntegrationTest {

    @Autowired
    private MovimientoService movimientoService;

    @Autowired
    private CuentaService cuentaService;

    private String numeroCuenta;
    private Long cuentaId;

    @BeforeEach
    void setUp() {
        // Crear una cuenta de prueba
        CuentaDTO cuentaDTO = new CuentaDTO();
        cuentaDTO.setNumeroCuenta("478758");
        cuentaDTO.setTipoCuenta("Ahorros");
        cuentaDTO.setSaldoInicial(new BigDecimal("2000.00"));
        cuentaDTO.setEstado(true);
        cuentaDTO.setClienteId("CLI001");

        CuentaDTO cuentaCreada = cuentaService.crearCuenta(cuentaDTO);
        numeroCuenta = cuentaCreada.getNumeroCuenta();
        cuentaId = cuentaCreada.getCuentaId();
    }

    @Test
    @DisplayName("Debería crear un depósito y actualizar el saldo correctamente")
    void deberiaCrearDepositoYActualizarSaldo() {
        // Arrange
        MovimientoDTO movimientoDTO = new MovimientoDTO();
        movimientoDTO.setNumeroCuenta(numeroCuenta);
        movimientoDTO.setTipoMovimiento("DEPOSITO");
        movimientoDTO.setValor(new BigDecimal("500.00"));

        // Act
        MovimientoDTO movimientoCreado = movimientoService.crearMovimiento(movimientoDTO);

        // Assert
        assertNotNull(movimientoCreado);
        assertEquals("DEPOSITO", movimientoCreado.getTipoMovimiento());
        assertEquals(new BigDecimal("500.00"), movimientoCreado.getValor());
        assertEquals(new BigDecimal("2500.00"), movimientoCreado.getSaldo());

        // Verificar que el saldo de la cuenta se actualizó
        CuentaDTO cuentaActualizada = cuentaService.obtenerCuentaPorNumero(numeroCuenta);
        assertEquals(new BigDecimal("2500.00"), cuentaActualizada.getSaldoDisponible());
    }

    @Test
    @DisplayName("Debería crear un retiro y actualizar el saldo correctamente")
    void deberiaCrearRetiroYActualizarSaldo() {
        // Arrange
        MovimientoDTO movimientoDTO = new MovimientoDTO();
        movimientoDTO.setNumeroCuenta(numeroCuenta);
        movimientoDTO.setTipoMovimiento("RETIRO");
        movimientoDTO.setValor(new BigDecimal("575.00"));

        // Act
        MovimientoDTO movimientoCreado = movimientoService.crearMovimiento(movimientoDTO);

        // Assert
        assertNotNull(movimientoCreado);
        assertEquals("RETIRO", movimientoCreado.getTipoMovimiento());
        assertEquals(new BigDecimal("575.00"), movimientoCreado.getValor());
        assertEquals(new BigDecimal("1425.00"), movimientoCreado.getSaldo());

        // Verificar que el saldo de la cuenta se actualizó
        CuentaDTO cuentaActualizada = cuentaService.obtenerCuentaPorNumero(numeroCuenta);
        assertEquals(new BigDecimal("1425.00"), cuentaActualizada.getSaldoDisponible());
    }

    @Test
    @DisplayName("Debería lanzar SaldoNoDisponibleException cuando el saldo es insuficiente")
    void deberiaLanzarExcepcionCuandoSaldoInsuficiente() {
        // Arrange
        MovimientoDTO movimientoDTO = new MovimientoDTO();
        movimientoDTO.setNumeroCuenta(numeroCuenta);
        movimientoDTO.setTipoMovimiento("RETIRO");
        movimientoDTO.setValor(new BigDecimal("2500.00")); // Más que el saldo disponible

        // Act & Assert
        SaldoNoDisponibleException exception = assertThrows(
            SaldoNoDisponibleException.class,
            () -> movimientoService.crearMovimiento(movimientoDTO)
        );

        assertEquals("Saldo no disponible", exception.getMessage());

        // Verificar que el saldo no cambió
        CuentaDTO cuenta = cuentaService.obtenerCuentaPorNumero(numeroCuenta);
        assertEquals(new BigDecimal("2000.00"), cuenta.getSaldoDisponible());
    }

    @Test
    @DisplayName("Debería permitir múltiples movimientos y mantener el saldo correcto")
    void deberiaPermitirMultiplesMovimientos() {
        // Primer depósito
        MovimientoDTO deposito1 = new MovimientoDTO();
        deposito1.setNumeroCuenta(numeroCuenta);
        deposito1.setTipoMovimiento("DEPOSITO");
        deposito1.setValor(new BigDecimal("300.00"));
        movimientoService.crearMovimiento(deposito1);

        // Segundo retiro
        MovimientoDTO retiro1 = new MovimientoDTO();
        retiro1.setNumeroCuenta(numeroCuenta);
        retiro1.setTipoMovimiento("RETIRO");
        retiro1.setValor(new BigDecimal("100.00"));
        movimientoService.crearMovimiento(retiro1);

        // Verificar saldo final
        CuentaDTO cuenta = cuentaService.obtenerCuentaPorNumero(numeroCuenta);
        assertEquals(new BigDecimal("2200.00"), cuenta.getSaldoDisponible());
    }
}

