package com.banking.cuentaservice.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${spring.rabbitmq.exchange.cliente}")
    private String clienteExchange;

    @Value("${spring.rabbitmq.queue.cliente-info-response}")
    private String clienteInfoResponseQueue;

    @Value("${spring.rabbitmq.routing-key.cliente-info}")
    private String clienteInfoRoutingKey;

    @Value("${spring.rabbitmq.routing-key.cliente-info-response}")
    private String clienteInfoResponseRoutingKey;

    @Bean
    public TopicExchange clienteExchange() {
        return new TopicExchange(clienteExchange);
    }

    @Bean
    public Queue clienteInfoResponseQueue() {
        return new Queue(clienteInfoResponseQueue, true);
    }

    @Bean
    public Binding clienteInfoResponseBinding() {
        return BindingBuilder
            .bind(clienteInfoResponseQueue())
            .to(clienteExchange())
            .with(clienteInfoResponseRoutingKey);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}

