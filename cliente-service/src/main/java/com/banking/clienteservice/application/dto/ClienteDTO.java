package com.banking.clienteservice.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {
    
    private Long personaId;
    
    @NotBlank(message = "El clienteId es obligatorio")
    private String clienteId;
    
    @NotBlank(message = "La contraseña es obligatoria")
    private String contrasena;
    
    @NotNull(message = "El estado es obligatorio")
    private Boolean estado;
    
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    
    private String genero;
    
    private Integer edad;
    
    @NotBlank(message = "La identificación es obligatoria")
    private String identificacion;
    
    private String direccion;
    
    private String telefono;
}

