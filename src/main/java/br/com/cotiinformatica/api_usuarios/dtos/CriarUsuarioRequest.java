package br.com.cotiinformatica.api_usuarios.dtos;

/*
    Objeto que representa os dados que a API deverá
    receber para cadastrar um novo usuário.
 */
public record CriarUsuarioRequest(
        String nome,    //Nome do usuário
        String email,   //Email do usuário
        String senha    //Senha do usuário
) {
}
