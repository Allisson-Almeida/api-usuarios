package br.com.cotiinformatica.api_usuarios.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public record AutenticarUsuarioResponse(
        UUID id,
        String nome,
        String email,
        LocalDateTime dataHoraAcesso,
        String token
) {
}
