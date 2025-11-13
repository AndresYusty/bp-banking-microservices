package com.banking.clienteservice.infrastructure.config;

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

    @Value("${spring.rabbitmq.queue.cliente-info-request}")
    private String clienteInfoRequestQueue;

    @Value("${spring.rabbitmq.queue.cliente-info-response}")
    private String clienteInfoResponseQueue;

    @Value("${spring.rabbitmq.routing-key.cliente-info}")
    private String clienteInfoRoutingKey;

    @Bean
    public TopicExchange clienteExchange() {
        return new TopicExchange(clienteExchange);
    }

    @Bean
    public Queue clienteInfoRequestQueue() {
        return new Queue(clienteInfoRequestQueue, true);
    }

    @Bean
    public Queue clienteInfoResponseQueue() {
        return new Queue(clienteInfoResponseQueue, true);
    }

    @Bean
    public Binding clienteInfoRequestBinding() {
        return BindingBuilder
            .bind(clienteInfoRequestQueue())
            .to(clienteExchange())
            .with(clienteInfoRoutingKey);
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

