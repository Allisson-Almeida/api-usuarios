package br.com.cotiinformatica.api_usuarios.dtos;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

/*
    Objeto que representa os dados que a API deverá
    retornar após cadastrar um novo usuário.
 */
public record CriarUsuarioResponse(
        UUID id,  //Id do usuário
        String nome, //Nome do usuário
        String email, //Email do usuário
        Date dataHoraCriacao //Data e hora de criação do usuário
) {
}
