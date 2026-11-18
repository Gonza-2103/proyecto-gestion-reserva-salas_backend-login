package com.example.login.dto;

import com.example.login.model.RolUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 DTO de entrada para el endpoint PUT /api/usuarios/{id}:
 Permite actualizar los datos del usuario desde la app móvil
 */
public class ActualizarUsuarioRequestDTO {

    @NotBlank(message = "El nombre completo es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String nombre;

    @NotBlank(message = "El correo institucional es obligatorio")
    @Email(message = "Formato de correo no válido")
    @Pattern(
            regexp = "(?i)^[A-Za-z0-9._%+-]+@(duocuc\\.cl|therionlabs\\.cl)$",
            message = "Debe ser un correo institucional (@duocuc.cl o @therionlabs.cl)"
    )
    private String correo;

    /**
     * Campo opcional al actualizar:
     * - Si viene null o vacío (""), se mantiene la contraseña actual en BD.
     * - Si viene con texto, debe cumplir la política de seguridad (6+ caracteres, mayúscula, minúscula y número).
     */
    @Pattern(
            regexp = "^$|^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,}$",
            message = "Si cambia la contraseña, debe tener al menos 6 caracteres, una mayúscula, una minúscula y un número"
    )
    private String password;

    @NotNull(message = "El rol es obligatorio (RELATOR o COORDINADOR)")
    private RolUsuario rol;

    //Permite activar o desactivar la cuenta (si viene null, el Service conserva el estado actual)
    private Boolean activo;

    //Constructor vacío
    public ActualizarUsuarioRequestDTO() {
    }

    //Constructor con parámetros para pruebas unitarias
    public ActualizarUsuarioRequestDTO(String nombre, String correo, String password, RolUsuario rol, Boolean activo) {
        this.nombre = nombre;
        this.correo = correo;
        this.password = password;
        this.rol = rol;
        this.activo = activo;
    }


    //Getters & Setters
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

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
