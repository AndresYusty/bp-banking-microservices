package com.banking.clienteservice.application.service;

import com.banking.clienteservice.application.dto.ClienteDTO;
import com.banking.clienteservice.application.exception.ClienteAlreadyExistsException;
import com.banking.clienteservice.application.exception.ClienteNotFoundException;
import com.banking.clienteservice.domain.model.Cliente;
import com.banking.clienteservice.domain.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteDTO crearCliente(ClienteDTO clienteDTO) {
        if (clienteRepository.existsByClienteId(clienteDTO.getClienteId())) {
            throw new ClienteAlreadyExistsException(
                "Ya existe un cliente con el clienteId: " + clienteDTO.getClienteId()
            );
        }
        
        if (clienteRepository.existsByIdentificacion(clienteDTO.getIdentificacion())) {
            throw new ClienteAlreadyExistsException(
                "Ya existe un cliente con la identificación: " + clienteDTO.getIdentificacion()
            );
        }

        Cliente cliente = new Cliente(
            clienteDTO.getClienteId(),
            clienteDTO.getContrasena(),
            clienteDTO.getEstado(),
            clienteDTO.getNombre(),
            clienteDTO.getGenero(),
            clienteDTO.getEdad(),
            clienteDTO.getIdentificacion(),
            clienteDTO.getDireccion(),
            clienteDTO.getTelefono()
        );

        Cliente savedCliente = clienteRepository.save(cliente);
        return mapToDTO(savedCliente);
    }

    @Transactional(readOnly = true)
    public ClienteDTO obtenerClientePorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new ClienteNotFoundException("Cliente no encontrado con id: " + id));
        return mapToDTO(cliente);
    }

    @Transactional(readOnly = true)
    public ClienteDTO obtenerClientePorClienteId(String clienteId) {
        Cliente cliente = clienteRepository.findByClienteId(clienteId)
            .orElseThrow(() -> new ClienteNotFoundException("Cliente no encontrado con clienteId: " + clienteId));
        return mapToDTO(cliente);
    }

    @Transactional(readOnly = true)
    public List<ClienteDTO> obtenerTodosLosClientes() {
        return clienteRepository.findAll().stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    public ClienteDTO actualizarCliente(Long id, ClienteDTO clienteDTO) {
        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new ClienteNotFoundException("Cliente no encontrado con id: " + id));

        // Verificar si el clienteId o identificación ya existen en otro cliente
        if (!cliente.getClienteId().equals(clienteDTO.getClienteId()) && 
            clienteRepository.existsByClienteId(clienteDTO.getClienteId())) {
            throw new ClienteAlreadyExistsException(
                "Ya existe un cliente con el clienteId: " + clienteDTO.getClienteId()
            );
        }

        if (!cliente.getIdentificacion().equals(clienteDTO.getIdentificacion()) && 
            clienteRepository.existsByIdentificacion(clienteDTO.getIdentificacion())) {
            throw new ClienteAlreadyExistsException(
                "Ya existe un cliente con la identificación: " + clienteDTO.getIdentificacion()
            );
        }

        // Actualizar campos
        cliente.setClienteId(clienteDTO.getClienteId());
        cliente.setContrasena(clienteDTO.getContrasena());
        cliente.setEstado(clienteDTO.getEstado());
        cliente.setNombre(clienteDTO.getNombre());
        cliente.setGenero(clienteDTO.getGenero());
        cliente.setEdad(clienteDTO.getEdad());
        cliente.setIdentificacion(clienteDTO.getIdentificacion());
        cliente.setDireccion(clienteDTO.getDireccion());
        cliente.setTelefono(clienteDTO.getTelefono());

        Cliente updatedCliente = clienteRepository.save(cliente);
        return mapToDTO(updatedCliente);
    }

    public void eliminarCliente(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new ClienteNotFoundException("Cliente no encontrado con id: " + id);
        }
        clienteRepository.deleteById(id);
    }

    private ClienteDTO mapToDTO(Cliente cliente) {
        ClienteDTO dto = new ClienteDTO();
        dto.setPersonaId(cliente.getPersonaId());
        dto.setClienteId(cliente.getClienteId());
        dto.setContrasena(cliente.getContrasena());
        dto.setEstado(cliente.getEstado());
        dto.setNombre(cliente.getNombre());
        dto.setGenero(cliente.getGenero());
        dto.setEdad(cliente.getEdad());
        dto.setIdentificacion(cliente.getIdentificacion());
        dto.setDireccion(cliente.getDireccion());
        dto.setTelefono(cliente.getTelefono());
        return dto;
    }
}

