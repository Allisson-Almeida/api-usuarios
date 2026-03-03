package br.com.cotiinformatica.api_usuarios.components;

import br.com.cotiinformatica.api_usuarios.dtos.CriarUsuarioResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQConsumerComponent {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private ObjectMapper objectMapper;

    /*
        TODO Método para ler e processar
        as mensagens da fila do RabbitMQ
     */
    //]@RabbitListener(queues = "usuarios")
    public void receiveMessages(@Payload String payload) throws Exception {

        //deserializar os dados do JSON para objeto
        var usuario = objectMapper.readValue(payload, CriarUsuarioResponse.class);

        //declarando as variáveis para fazer o envio do email
        var to = usuario.email(); //email do destinatário
        var subject = "Conta de usuário criada com sucesso!";
        var message = "Olá, " + usuario.nome() + "! Sua conta de usuário foi criada com sucesso.";

        //enviando do email para o usuário
        var sender = javaMailSender.createMimeMessage();
        var helper = new MimeMessageHelper(sender, true, "UTF-8");

        //preencho os parametros do email
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(message, true);

        //fazendo o envio
        javaMailSender.send(sender);
    }
}
