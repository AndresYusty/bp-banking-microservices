package com.banking.cuentaservice.application.service;

import com.banking.cuentaservice.application.dto.EstadoCuentaDTO;
import com.banking.cuentaservice.domain.model.Cuenta;
import com.banking.cuentaservice.domain.model.Movimiento;
import com.banking.cuentaservice.domain.repository.CuentaRepository;
import com.banking.cuentaservice.domain.repository.MovimientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReporteService {

    private final CuentaRepository cuentaRepository;
    private final MovimientoRepository movimientoRepository;
    private final RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.exchange.cliente}")
    private String clienteExchange;

    @Value("${spring.rabbitmq.routing-key.cliente-info}")
    private String clienteInfoRoutingKey;

    @Value("${spring.rabbitmq.routing-key.cliente-info-response}")
    private String clienteInfoResponseRoutingKey;

    public EstadoCuentaDTO generarEstadoCuenta(String clienteId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        // Obtener información del cliente desde el microservicio de clientes
        String nombreCliente = obtenerNombreCliente(clienteId);

        // Obtener cuentas del cliente
        List<Cuenta> cuentas = cuentaRepository.findByClienteIdAndEstado(clienteId, true);

        // Construir el DTO de respuesta
        EstadoCuentaDTO estadoCuenta = new EstadoCuentaDTO();
        estadoCuenta.setClienteId(clienteId);
        estadoCuenta.setNombreCliente(nombreCliente);
        estadoCuenta.setFechaInicio(fechaInicio);
        estadoCuenta.setFechaFin(fechaFin);

        // Obtener movimientos por cuenta en el rango de fechas
        List<EstadoCuentaDTO.CuentaDetalleDTO> cuentasDetalle = cuentas.stream()
            .map(cuenta -> {
                List<Movimiento> movimientos = movimientoRepository.findByNumeroCuentaAndFechaBetween(
                    cuenta.getNumeroCuenta(), fechaInicio, fechaFin
                );

                List<EstadoCuentaDTO.MovimientoDetalleDTO> movimientosDetalle = movimientos.stream()
                    .map(m -> {
                        EstadoCuentaDTO.MovimientoDetalleDTO detalle = new EstadoCuentaDTO.MovimientoDetalleDTO();
                        detalle.setFecha(m.getFecha());
                        detalle.setTipoMovimiento(m.getTipoMovimiento());
                        detalle.setValor(m.getValor());
                        detalle.setSaldo(m.getSaldo());
                        return detalle;
                    })
                    .collect(Collectors.toList());

                EstadoCuentaDTO.CuentaDetalleDTO cuentaDetalle = new EstadoCuentaDTO.CuentaDetalleDTO();
                cuentaDetalle.setNumeroCuenta(cuenta.getNumeroCuenta());
                cuentaDetalle.setTipoCuenta(cuenta.getTipoCuenta());
                cuentaDetalle.setSaldoInicial(cuenta.getSaldoInicial());
                cuentaDetalle.setSaldoDisponible(cuenta.getSaldoDisponible());
                cuentaDetalle.setEstado(cuenta.getEstado());
                cuentaDetalle.setMovimientos(movimientosDetalle);

                return cuentaDetalle;
            })
            .collect(Collectors.toList());

        estadoCuenta.setCuentas(cuentasDetalle);
        return estadoCuenta;
    }

    private String obtenerNombreCliente(String clienteId) {
        try {
            // Enviar mensaje al microservicio de clientes para obtener información
            Object response = rabbitTemplate.convertSendAndReceive(
                clienteExchange,
                clienteInfoRoutingKey,
                clienteId
            );
            return response != null ? response.toString() : "Cliente no encontrado";
        } catch (Exception e) {
            return "Cliente no encontrado";
        }
    }
}

