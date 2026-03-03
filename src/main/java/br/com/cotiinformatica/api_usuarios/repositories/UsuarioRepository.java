package br.com.cotiinformatica.api_usuarios.repositories;

import br.com.cotiinformatica.api_usuarios.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    @Query("""
            select u from Usuario u
            where u.email = :email
            """)
    Optional<Usuario> findByEmail(
            @Param("email") String email
    );

    @Query("""
            select u from Usuario u
            where u.email = :email
              and u.senha = :senha
    """)
    Optional<Usuario> findByEmailAndSenha(
            @Param("email") String email,
            @Param("senha") String senha);
}
