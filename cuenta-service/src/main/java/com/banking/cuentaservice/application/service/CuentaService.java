package com.banking.cuentaservice.application.service;

import com.banking.cuentaservice.application.dto.CuentaDTO;
import com.banking.cuentaservice.application.exception.CuentaNotFoundException;
import com.banking.cuentaservice.domain.model.Cuenta;
import com.banking.cuentaservice.domain.repository.CuentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CuentaService {

    private final CuentaRepository cuentaRepository;

    public CuentaDTO crearCuenta(CuentaDTO cuentaDTO) {
        if (cuentaRepository.existsByNumeroCuenta(cuentaDTO.getNumeroCuenta())) {
            throw new RuntimeException("Ya existe una cuenta con el número: " + cuentaDTO.getNumeroCuenta());
        }

        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta(cuentaDTO.getNumeroCuenta());
        cuenta.setTipoCuenta(cuentaDTO.getTipoCuenta());
        cuenta.setSaldoInicial(cuentaDTO.getSaldoInicial());
        cuenta.setSaldoDisponible(cuentaDTO.getSaldoInicial());
        cuenta.setEstado(cuentaDTO.getEstado());
        cuenta.setClienteId(cuentaDTO.getClienteId());

        Cuenta savedCuenta = cuentaRepository.save(cuenta);
        return mapToDTO(savedCuenta);
    }

    @Transactional(readOnly = true)
    public CuentaDTO obtenerCuentaPorId(Long id) {
        Cuenta cuenta = cuentaRepository.findById(id)
            .orElseThrow(() -> new CuentaNotFoundException("Cuenta no encontrada con id: " + id));
        return mapToDTO(cuenta);
    }

    @Transactional(readOnly = true)
    public CuentaDTO obtenerCuentaPorNumero(String numeroCuenta) {
        Cuenta cuenta = cuentaRepository.findByNumeroCuenta(numeroCuenta)
            .orElseThrow(() -> new CuentaNotFoundException("Cuenta no encontrada con número: " + numeroCuenta));
        return mapToDTO(cuenta);
    }

    @Transactional(readOnly = true)
    public List<CuentaDTO> obtenerTodasLasCuentas() {
        return cuentaRepository.findAll().stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CuentaDTO> obtenerCuentasPorCliente(String clienteId) {
        return cuentaRepository.findByClienteId(clienteId).stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    public CuentaDTO actualizarCuenta(Long id, CuentaDTO cuentaDTO) {
        Cuenta cuenta = cuentaRepository.findById(id)
            .orElseThrow(() -> new CuentaNotFoundException("Cuenta no encontrada con id: " + id));

        if (!cuenta.getNumeroCuenta().equals(cuentaDTO.getNumeroCuenta()) && 
            cuentaRepository.existsByNumeroCuenta(cuentaDTO.getNumeroCuenta())) {
            throw new RuntimeException("Ya existe una cuenta con el número: " + cuentaDTO.getNumeroCuenta());
        }

        cuenta.setNumeroCuenta(cuentaDTO.getNumeroCuenta());
        cuenta.setTipoCuenta(cuentaDTO.getTipoCuenta());
        cuenta.setSaldoInicial(cuentaDTO.getSaldoInicial());
        cuenta.setEstado(cuentaDTO.getEstado());
        cuenta.setClienteId(cuentaDTO.getClienteId());

        Cuenta updatedCuenta = cuentaRepository.save(cuenta);
        return mapToDTO(updatedCuenta);
    }

    private CuentaDTO mapToDTO(Cuenta cuenta) {
        CuentaDTO dto = new CuentaDTO();
        dto.setCuentaId(cuenta.getCuentaId());
        dto.setNumeroCuenta(cuenta.getNumeroCuenta());
        dto.setTipoCuenta(cuenta.getTipoCuenta());
        dto.setSaldoInicial(cuenta.getSaldoInicial());
        dto.setSaldoDisponible(cuenta.getSaldoDisponible());
        dto.setEstado(cuenta.getEstado());
        dto.setClienteId(cuenta.getClienteId());
        return dto;
    }
}

