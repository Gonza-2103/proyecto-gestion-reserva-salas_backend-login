package com.example.login.dto;

import java.time.LocalDateTime;
import java.util.Map;

/**
 DTO estándar para devolver errores HTTP en formato JSON limpio y predecible:
 Evita que la app Android reciba trazas internas de Spring y facilita mostrar mensajes claros en pantalla
 */
public class ErrorResponseDTO {

    private int status;  // Código de estado HTTP
    private String error;  // Nombre corto del error
    private String mensaje;
    private LocalDateTime timestamp;
    private Map<String, String> detalles;

    //Constructor vacío
    public ErrorResponseDTO() {
    }

    //Constructor para errores generales (sin mapa de detalles por campo)
    public ErrorResponseDTO(int status, String error, String mensaje) {
        this.status = status;
        this.error = error;
        this.mensaje = mensaje;
        this.timestamp = LocalDateTime.now();
    }

    //Constructor para errores de validación de formularios (con detalle campo por campo)
    public ErrorResponseDTO(int status, String error, String mensaje, Map<String, String> detalles) {
        this.status = status;
        this.error = error;
        this.mensaje = mensaje;
        this.timestamp = LocalDateTime.now();
        this.detalles = detalles;
    }


    //Getters & Setters
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, String> getDetalles() {
        return detalles;
    }

    public void setDetalles(Map<String, String> detalles) {
        this.detalles = detalles;
    }
}
