package com.example.login.dto;

import com.example.login.model.RolUsuario;
import com.example.login.model.Usuario;
import java.time.LocalDateTime;

/**
 * DTO de salida para las operaciones CRUD de usuarios (/api/usuarios).
 * Expone los datos públicos del usuario y OCULTA intencionalmente el campo password por seguridad.
 */
public class UsuarioResponseDTO {

    private Long id;
    private String nombre;
    private String correo;
    private RolUsuario rol;
    private Boolean activo;
    private LocalDateTime fechaCreacion;

    //Constructor vacío
    public UsuarioResponseDTO() {
    }

    //Constructor con todos los campos
    public UsuarioResponseDTO(Long id, String nombre, String correo, RolUsuario rol, Boolean activo, LocalDateTime fechaCreacion) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.rol = rol;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
    }

    /**
     Método de fábrica estático para convertir una
     entidad 'usuario' en UsuarioResponse
     de forma limpia y reutilizable desde el Service
     */
    public static UsuarioResponseDTO fromEntity(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getRol(),
                usuario.getActivo(),
                usuario.getFechaCreacion()
        );
    }


    //Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public RolUsuario getRol() {
        return rol;
    }

    public void setRol(RolUsuario rol) {
        this.rol = rol;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
