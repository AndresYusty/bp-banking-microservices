package com.banking.clienteservice.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "clientes")
@PrimaryKeyJoinColumn(name = "persona_id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cliente extends Persona {

    @Column(name = "cliente_id", unique = true, nullable = false)
    private String clienteId;

    @Column(name = "contrasena", nullable = false, length = 255)
    private String contrasena;

    @Column(name = "estado", nullable = false)
    private Boolean estado;

    public Cliente(String clienteId, String contrasena, Boolean estado, 
                   String nombre, String genero, Integer edad, 
                   String identificacion, String direccion, String telefono) {
        super(nombre, genero, edad, identificacion, direccion, telefono);
        this.clienteId = clienteId;
        this.contrasena = contrasena;
        this.estado = estado;
    }
}

