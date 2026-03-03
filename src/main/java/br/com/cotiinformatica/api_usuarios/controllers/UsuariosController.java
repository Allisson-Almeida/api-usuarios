package br.com.cotiinformatica.api_usuarios.controllers;

import br.com.cotiinformatica.api_usuarios.dtos.AutenticarUsuarioRequest;
import br.com.cotiinformatica.api_usuarios.dtos.CriarUsuarioRequest;
import br.com.cotiinformatica.api_usuarios.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuariosController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("criar")
    public ResponseEntity<?> criar(@RequestBody CriarUsuarioRequest request) {

        try {

            //Executando a camada de serviço e capturando a resposta
            var response = usuarioService.criarUsuario(request);

            //HTTP 201 (CREATED)
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        catch(IllegalArgumentException e) {
            //HTTP 400 (BAD REQUEST)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch(Exception e) {
            //HTTP 500 (INTERNAL SERVER ERROR)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("autenticar")
    public ResponseEntity<?> autenticar(@RequestBody AutenticarUsuarioRequest request) {

        try {
            //Executando a autenticação do usuário
            var response = usuarioService.autenticarUsuario(request);

            //HTTP 200 (OK)
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        catch(IllegalArgumentException e) {
            //HTTP 400 (BAD REQUEST)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch(SecurityException e) {
            //HTTP 401 (UNAUTHORIZED)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
        catch(Exception e) {
            //HTTP 500 (INTERNAL SERVER ERROR)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
