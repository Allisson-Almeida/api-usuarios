package br.com.cotiinformatica.api_usuarios.services;

import br.com.cotiinformatica.api_usuarios.components.RabbitMQProducerComponent;
import br.com.cotiinformatica.api_usuarios.dtos.AutenticarUsuarioRequest;
import br.com.cotiinformatica.api_usuarios.dtos.AutenticarUsuarioResponse;
import br.com.cotiinformatica.api_usuarios.dtos.CriarUsuarioRequest;
import br.com.cotiinformatica.api_usuarios.dtos.CriarUsuarioResponse;
import br.com.cotiinformatica.api_usuarios.entities.Usuario;
import br.com.cotiinformatica.api_usuarios.repositories.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class UsuarioService {

    /*
        Atributos
     */
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RabbitMQProducerComponent producerComponent;

    @Autowired
    private ObjectMapper objectMapper;

    /*
        Método para desenvolver o serviço de criaçao de usuário
     */
    public CriarUsuarioResponse criarUsuario(CriarUsuarioRequest request) {

        //Executar as validações
        validarNome(request.nome());
        validarEmail(request.email());
        validarEmailExistente(request.email());
        validarSenha(request.senha());

        //Criando um novo usuário
        var usuario = new Usuario();

        usuario.setNome(request.nome());    //capturando o nome recebido
        usuario.setEmail(request.email());  //capturando o email recebido
        usuario.setSenha(criptografarSenha(request.senha()));  //capturando a senha recebida
        usuario.setDataCriacao(new Date()); //gerando a data e hora atual

        //Salvar no banco de dados
        usuarioRepository.save(usuario);

        //Gerar os dados de resposta do cadastro do usuário
        var response = new CriarUsuarioResponse(
                usuario.getId(), //Id do usuário
                usuario.getNome(), //Nome do usuário
                usuario.getEmail(), //Email do usuário
                new Date() //Data e hora do cadastro
        );

        try {
            //Converter o objeto 'response' em JSON (Serialização)
            var json = objectMapper.writeValueAsString(response);
            //Enviar para a fila (PRODUCER)
            producerComponent.sendMessage(json);
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        //Retornar os dados
        return response;
    }

    /*
         Método para desenvolver o serviço de autenticação de usuário
     */
    public AutenticarUsuarioResponse autenticarUsuario(AutenticarUsuarioRequest request) {

        //Validar o email e a senha recebidos no request
        validarEmail(request.email());
        validarSenha(request.senha());

        //Consultar o usuário no banco de dados através do email e da senha
        var usuario = usuarioRepository.findByEmailAndSenha(request.email(), criptografarSenha(request.senha()));

        //Verificar se o usuário não foi encontrado
        if(usuario.isEmpty()) {
            throw new SecurityException("Acesso negado. Usuário inválido.");
        }

        //Retornar os dados do usuário
        var u = usuario.get(); //Pegando os dados do usuário

        return new AutenticarUsuarioResponse(
                u.getId(), //Id do usuário
                u.getNome(), //Nome do usuário
                u.getEmail(), //Email do usuário
                LocalDateTime.now(), //Data e hora de acesso
                generateToken(u.getEmail()) //Gerando o TOKEN JWT
        );
    }

    /*
        Validações
     */
    private void validarNome(String nome) {
        if(nome == null || nome.trim().length() < 8) {
            throw new IllegalArgumentException
                    ("O nome do usuário deve conter pelo menos 8 caracteres.");
        }
    }

    private void validarEmail(String email) {
        var regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if(email == null || !Pattern.matches(regex, email)) {
            throw new IllegalArgumentException
                    ("O endereço de email informado é inválido.");
        }
    }

    private void validarEmailExistente(String email) {
        var usuario = usuarioRepository.findByEmail(email);
        if(usuario.isPresent()) {
            throw new IllegalArgumentException
                    ("O email informado já está cadastrado para outro usuário.");
        }
    }

    private void validarSenha(String senha) {
        var regex =  "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}$";
        if(senha == null || !Pattern.matches(regex, senha)) {
            throw new IllegalArgumentException
                    ("A senha deve ter pelo menos uma letra maiúscula, uma letra minúscula, um símbolo e no mínimo 8 caracteres..");
        }
    }

    /*
        Criptografia da senha
     */
    private String criptografarSenha(String senha) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(senha.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao criptografar senha com SHA-256", e);
        }
    }

    /*
        Método para gerar os TOKENS JWT
     */
    public String generateToken(String email) {

        //CHAVE PARA CRIPTOGRAFAR O TOKEN (ASSINATURA DO TOKEN)
        var secretKey = "dc18db9b-cf94-4b1d-8921-57946a534066";

        //Gerando o token jwt
        return Jwts.builder()
                .setSubject(email) //identificação do usuário
                .setIssuedAt(new Date()) //data de geração do token
                .signWith(SignatureAlgorithm.HS256, secretKey) //Chave para assinatura
                .compact(); //Gerando o token
    }

}
