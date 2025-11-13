package com.banking.clienteservice.infrastructure.controller;

import com.banking.clienteservice.application.dto.ClienteDTO;
import com.banking.clienteservice.application.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @PostMapping
    public ResponseEntity<ClienteDTO> crearCliente(@Valid @RequestBody ClienteDTO clienteDTO) {
        ClienteDTO clienteCreado = clienteService.crearCliente(clienteDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteCreado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteDTO> obtenerClientePorId(@PathVariable Long id) {
        ClienteDTO cliente = clienteService.obtenerClientePorId(id);
        return ResponseEntity.ok(cliente);
    }

    @GetMapping("/clienteId/{clienteId}")
    public ResponseEntity<ClienteDTO> obtenerClientePorClienteId(@PathVariable String clienteId) {
        ClienteDTO cliente = clienteService.obtenerClientePorClienteId(clienteId);
        return ResponseEntity.ok(cliente);
    }

    @GetMapping
    public ResponseEntity<List<ClienteDTO>> obtenerTodosLosClientes() {
        List<ClienteDTO> clientes = clienteService.obtenerTodosLosClientes();
        return ResponseEntity.ok(clientes);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteDTO> actualizarCliente(
            @PathVariable Long id,
            @Valid @RequestBody ClienteDTO clienteDTO) {
        ClienteDTO clienteActualizado = clienteService.actualizarCliente(id, clienteDTO);
        return ResponseEntity.ok(clienteActualizado);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ClienteDTO> actualizarClienteParcial(
            @PathVariable Long id,
            @RequestBody ClienteDTO clienteDTO) {
        ClienteDTO clienteExistente = clienteService.obtenerClientePorId(id);
        
        // Actualizar solo los campos proporcionados
        if (clienteDTO.getNombre() != null) clienteExistente.setNombre(clienteDTO.getNombre());
        if (clienteDTO.getGenero() != null) clienteExistente.setGenero(clienteDTO.getGenero());
        if (clienteDTO.getEdad() != null) clienteExistente.setEdad(clienteDTO.getEdad());
        if (clienteDTO.getDireccion() != null) clienteExistente.setDireccion(clienteDTO.getDireccion());
        if (clienteDTO.getTelefono() != null) clienteExistente.setTelefono(clienteDTO.getTelefono());
        if (clienteDTO.getEstado() != null) clienteExistente.setEstado(clienteDTO.getEstado());
        if (clienteDTO.getContrasena() != null) clienteExistente.setContrasena(clienteDTO.getContrasena());

        ClienteDTO clienteActualizado = clienteService.actualizarCliente(id, clienteExistente);
        return ResponseEntity.ok(clienteActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable Long id) {
        clienteService.eliminarCliente(id);
        return ResponseEntity.noContent().build();
    }
}

