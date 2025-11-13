package com.banking.cuentaservice.domain.repository;

import com.banking.cuentaservice.domain.model.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Long> {
    
    Optional<Cuenta> findByNumeroCuenta(String numeroCuenta);
    
    List<Cuenta> findByClienteId(String clienteId);
    
    List<Cuenta> findByClienteIdAndEstado(String clienteId, Boolean estado);
    
    boolean existsByNumeroCuenta(String numeroCuenta);
}

