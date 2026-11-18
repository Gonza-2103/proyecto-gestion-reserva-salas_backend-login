package com.example.login.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 Clase que almacena las credenciales y el perfil de cada usuario del sistema
 */

@Entity
@Table(name = "usuarios")
public class Usuario {

    //ID, nombre, correo, password, rol, activo y fechaCreacion son los atributos obligatorios de la clase 'usuario'
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, unique = true, length = 120)
    private String correo;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RolUsuario rol;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    //Constructor vacío obligatorio para que JPA/Hibernate pueda instanciar la entidad al leer desde la BD
    public Usuario() {
    }

    //Constructor con parámetros para crear nuevos usuarios fácilmente desde 'service'
    public Usuario(String nombre, String correo, String password, RolUsuario rol) {
        this.nombre = nombre;
        this.correo = correo;
        this.password = password;
        this.rol = rol;
        this.activo = true;
    }

    /**
     Método de ciclo de vida JPA que se ejecuta automáticamente justo
     antes de guardar un nuevo usuario en la base de datos por 1ra vez
     */
    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        if (this.activo == null) {
            this.activo = true;
        }
    }

    //Getters & Setters (permiten leer y modificar los atributos de forma segura)
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

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
