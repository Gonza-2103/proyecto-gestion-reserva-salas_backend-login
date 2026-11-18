package com.example.login.dto;

import com.example.login.model.RolUsuario;

/**
 DTO de salida para los endpoints de autenticación (/api/auth/login y /api/auth/register):
 Entrega el token JWT y los datos esenciales del usuario (incluyendo idUsuario) para navegar en Android
 */

public class AutenticacionResponseDTO {

    private String token;  //Token JWT firmado con HS256 para autenticar peticiones futuras
    private String tipo = "Bearer";  //Esquema estándar de autorización HTTP ("Bearer <token>")
    private Long idUsuario;
    private String nombre;
    private String correo;
    private RolUsuario rol;
    private String mensaje;

    //Constructor vacío requerido por Jackson
    public AutenticacionResponseDTO() {
    }

    //Constructor completo para construir la respuesta desde el Service
    public AutenticacionResponseDTO(String token, Long idUsuario, String nombre, String correo, RolUsuario rol, String mensaje) {
        this.token = token;
        this.tipo = "Bearer";
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.correo = correo;
        this.rol = rol;
        this.mensaje = mensaje;
    }


    //Getters & Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
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

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
