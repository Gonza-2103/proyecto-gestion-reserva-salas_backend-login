package com.example.login.dto;

import com.example.login.model.RolUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 DTO de entrada para el endpoint POST /api/auth/login:
 Recibe las credenciales y el perfil seleccionado en LoginScreen
 */

public class LoginRequestDTO {

    @NotBlank(message = "El correo institucional es obligatorio")
    @Email(message = "Formato de correo no válido")
    @Pattern(
            regexp = "(?i)^[A-Za-z0-9._%+-]+@(duocuc\\.cl|therionlabs\\.cl)$",
            message = "Debe ser un correo institucional (@duocuc.cl o @therionlabs.cl)"
    )
    private String correo;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    @NotNull(message = "Debe seleccionar un perfil (RELATOR o COORDINADOR)")
    private RolUsuario rol;

    //Constructor vacío para deserializar el JSON entrante
    public LoginRequestDTO() {
    }

    //Constructor con parámetros útil para pruebas unitarias
    public LoginRequestDTO(String correo, String password, RolUsuario rol) {
        this.correo = correo;
        this.password = password;
        this.rol = rol;
    }


    //Getters & Setters
    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public RolUsuario getRol() {
        return rol;
    }

    public void setRol(RolUsuario rol) {
        this.rol = rol;
    }
}
