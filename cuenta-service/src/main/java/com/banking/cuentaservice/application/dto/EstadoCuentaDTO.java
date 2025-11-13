package com.banking.cuentaservice.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadoCuentaDTO {
    
    private String clienteId;
    private String nombreCliente;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private List<CuentaDetalleDTO> cuentas;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CuentaDetalleDTO {
        private String numeroCuenta;
        private String tipoCuenta;
        private BigDecimal saldoInicial;
        private BigDecimal saldoDisponible;
        private Boolean estado;
        private List<MovimientoDetalleDTO> movimientos;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MovimientoDetalleDTO {
        private LocalDateTime fecha;
        private String tipoMovimiento;
        private BigDecimal valor;
        private BigDecimal saldo;
    }
}

