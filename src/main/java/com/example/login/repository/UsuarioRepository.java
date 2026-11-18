package com.example.login.repository;

import com.example.login.model.RolUsuario;
import com.example.login.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 Repositorio JPA para la gestión de persistencia de la clase 'usuario'
 en la base de datos PostgreSQL (Supabase)
 */

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    //Busca un usuario por correo sin importar mayúsculas o minúsculas (necesario para el login)
    Optional<Usuario> findByCorreoIgnoreCase(String correo);

    //Verifica si un correo ya está registrado (evita duplicados en el registro)
    boolean existsByCorreoIgnoreCase(String correo);

    //Verifica si el correo ya pertenece a OTRO usuario (clave al actualizar en el PUT)
    boolean existsByCorreoIgnoreCaseAndIdNot(String correo, Long id);

    //Busca un usuario activo por correo (útil si se desactivan cuentas sin borrarlas)
    Optional<Usuario> findByCorreoIgnoreCaseAndActivoTrue(String correo);

    //Lista usuarios filtrados por rol (RELATOR / COORDINADOR)
    List<Usuario> findByRol(RolUsuario rol);

    //Lista solo los usuarios activos en el sistema
    List<Usuario> findByActivoTrue();
}
