package br.com.cotiinformatica.api_usuarios.dtos;

public record AutenticarUsuarioRequest(
        String email,   //Email do usuário
        String senha    //Senha do usuário
) {
}
