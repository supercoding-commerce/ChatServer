package com.github.chatserver.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class RabbitMQConfig {
    public static final String EXCHANGE_NAME = "exchange";
    // 여러 개의 큐와 라우팅 키를 리스트로 관리
    public static final List<String> QUEUE_NAMES = Arrays.asList("postRoom", "postChat1", "postChat2", "postChat3", "postChat4", "postChat5");
    public static final List<String> ROUTING_KEYS = Arrays.asList("postRoom", "postChat", "postChat", "postChat", "postChat", "postChat");


    @Value("${rabbitmq.host}")
    private String rmqHost;

    @Value("${rabbitmq.username}")
    private String rmqUsername;

    @Value("${rabbitmq.password}")
    private String rmqPassword;


    @Bean
    public CachingConnectionFactory cachingConnectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(rmqHost);
        connectionFactory.setUsername(rmqUsername);
        connectionFactory.setPassword(rmqPassword);
        // 다양한 설정 (캐싱 옵션, 포트, 가상 호스트 등)을 설정할 수 있습니다.
        return connectionFactory;
    }


    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public List<Queue> queues() {
        // 큐들을 생성하고 리스트로 반환
        List<Queue> queues = new ArrayList<>();
        for (String queueName : QUEUE_NAMES) {
            queues.add(new Queue(queueName));
        }
        return queues;
    }


    @Bean
    public List<Binding> bindings(List<Queue> queues, DirectExchange exchange) {
        // 바인딩들을 생성하고 리스트로 반환
        List<Binding> bindings = new ArrayList<>();
        for (int i = 0; i < QUEUE_NAMES.size(); i++) {
            bindings.add(BindingBuilder.bind(queues.get(i)).to(exchange).with(ROUTING_KEYS.get(i)));
        }
        return bindings;
    }


    Jackson2JsonMessageConverter messageConverter(ObjectMapper mapper){
        var converter = new Jackson2JsonMessageConverter(mapper);
        converter.setCreateMessageIds(true); //create a unique message id for every message
        return converter;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory factory, ObjectMapper objectMapper){
        RabbitTemplate template = new RabbitTemplate();
        template.setConnectionFactory(factory);
        template.setMessageConverter(messageConverter(objectMapper));
        return template;
    }

}