package br.com.cotiinformatica.api_usuarios.configurations;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {

    /*
        Método para conexão com o RabbitMQ
        e criar / acessar a fila
     */
    @Bean
    Queue queue() {
        //Nome da fila
        return new Queue("usuarios");
    }

}