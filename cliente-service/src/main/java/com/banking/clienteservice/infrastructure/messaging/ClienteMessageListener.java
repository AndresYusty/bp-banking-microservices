package com.banking.clienteservice.infrastructure.messaging;

import com.banking.clienteservice.application.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClienteMessageListener {

    private final ClienteService clienteService;
    private final RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.exchange.cliente}")
    private String clienteExchange;

    @Value("${spring.rabbitmq.routing-key.cliente-info-response}")
    private String clienteInfoResponseRoutingKey;

    @RabbitListener(queues = "${spring.rabbitmq.queue.cliente-info-request}")
    public void recibirSolicitudInfoCliente(String clienteId, Message message, 
                                           @Header(required = false, name = AmqpHeaders.REPLY_TO) String replyTo,
                                           @Header(required = false, name = AmqpHeaders.CORRELATION_ID) String correlationId) {
        try {
            var cliente = clienteService.obtenerClientePorClienteId(clienteId);
            String nombreCliente = cliente.getNombre();
            
            // Si hay replyTo, responder directamente (para convertSendAndReceive)
            if (replyTo != null && correlationId != null) {
                // Enviar directamente a la cola de respuesta temporal
                rabbitTemplate.convertAndSend("", replyTo, nombreCliente, msg -> {
                    msg.getMessageProperties().setCorrelationId(correlationId);
                    return msg;
                });
            } else {
                // Si no hay replyTo, usar el routing key (para mensajes asíncronos)
                rabbitTemplate.convertAndSend(
                    clienteExchange,
                    clienteInfoResponseRoutingKey,
                    nombreCliente
                );
            }
        } catch (Exception e) {
            String respuesta = "Cliente no encontrado";
            if (replyTo != null && correlationId != null) {
                // Enviar directamente a la cola de respuesta temporal
                rabbitTemplate.convertAndSend("", replyTo, respuesta, msg -> {
                    msg.getMessageProperties().setCorrelationId(correlationId);
                    return msg;
                });
            } else {
                rabbitTemplate.convertAndSend(
                    clienteExchange,
                    clienteInfoResponseRoutingKey,
                    respuesta
                );
            }
        }
    }
}

