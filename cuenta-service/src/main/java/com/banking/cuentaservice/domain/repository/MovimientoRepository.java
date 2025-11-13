package com.banking.cuentaservice.domain.repository;

import com.banking.cuentaservice.domain.model.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {
    
    List<Movimiento> findByCuenta_CuentaId(Long cuentaId);
    
    @Query("SELECT m FROM Movimiento m WHERE m.cuenta.clienteId = :clienteId " +
           "AND m.fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY m.fecha DESC")
    List<Movimiento> findByClienteIdAndFechaBetween(
        @Param("clienteId") String clienteId,
        @Param("fechaInicio") LocalDateTime fechaInicio,
        @Param("fechaFin") LocalDateTime fechaFin
    );
    
    @Query("SELECT m FROM Movimiento m WHERE m.cuenta.numeroCuenta = :numeroCuenta " +
           "AND m.fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY m.fecha DESC")
    List<Movimiento> findByNumeroCuentaAndFechaBetween(
        @Param("numeroCuenta") String numeroCuenta,
        @Param("fechaInicio") LocalDateTime fechaInicio,
        @Param("fechaFin") LocalDateTime fechaFin
    );
}

