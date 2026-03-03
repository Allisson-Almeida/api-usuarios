package br.com.cotiinformatica.api_usuarios.components;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQProducerComponent{

    //Injeção de dependência
    @Autowired
    private RabbitTemplate rabbitTemplate;

    //Injeção de dependência
    @Autowired
    private Queue queue;

    /*
        Método para enviar mensagens para a fila
     */
    public void sendMessage(String message) throws Exception {
        //Gravar a mensagem na fila
        rabbitTemplate.convertAndSend(queue.getName(), message);
    }
}
