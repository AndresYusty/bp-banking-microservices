package com.banking.clienteservice.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas unitarias para la entidad Cliente")
class ClienteTest {

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setClienteId("CLI001");
        cliente.setContrasena("password123");
        cliente.setEstado(true);
        cliente.setNombre("Juan Pérez");
        cliente.setGenero("Masculino");
        cliente.setEdad(30);
        cliente.setIdentificacion("1234567890");
        cliente.setDireccion("Calle Principal 123");
        cliente.setTelefono("0987654321");
    }

    @Test
    @DisplayName("Debería crear un cliente con todos los campos correctamente")
    void deberiaCrearClienteConTodosLosCampos() {
        assertNotNull(cliente);
        assertEquals("CLI001", cliente.getClienteId());
        assertEquals("password123", cliente.getContrasena());
        assertTrue(cliente.getEstado());
        assertEquals("Juan Pérez", cliente.getNombre());
        assertEquals("Masculino", cliente.getGenero());
        assertEquals(30, cliente.getEdad());
        assertEquals("1234567890", cliente.getIdentificacion());
        assertEquals("Calle Principal 123", cliente.getDireccion());
        assertEquals("0987654321", cliente.getTelefono());
    }

    @Test
    @DisplayName("Debería actualizar el estado del cliente")
    void deberiaActualizarEstadoCliente() {
        cliente.setEstado(false);
        assertFalse(cliente.getEstado());
    }

    @Test
    @DisplayName("Debería actualizar la contraseña del cliente")
    void deberiaActualizarContrasena() {
        String nuevaContrasena = "newPassword456";
        cliente.setContrasena(nuevaContrasena);
        assertEquals(nuevaContrasena, cliente.getContrasena());
    }

    @Test
    @DisplayName("Debería crear un cliente usando el constructor completo")
    void deberiaCrearClienteConConstructorCompleto() {
        Cliente nuevoCliente = new Cliente(
            "CLI002",
            "pass456",
            true,
            "María García",
            "Femenino",
            25,
            "0987654321",
            "Avenida Central 456",
            "0999888777"
        );

        assertNotNull(nuevoCliente);
        assertEquals("CLI002", nuevoCliente.getClienteId());
        assertEquals("María García", nuevoCliente.getNombre());
        assertEquals("Femenino", nuevoCliente.getGenero());
        assertEquals(25, nuevoCliente.getEdad());
    }

    @Test
    @DisplayName("Debería heredar correctamente de Persona")
    void deberiaHeredarDePersona() {
        assertTrue(cliente instanceof Persona);
        assertNotNull(cliente.getPersonaId());
    }
}

