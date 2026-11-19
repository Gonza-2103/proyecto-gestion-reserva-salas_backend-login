package com.example.login.exception;

/**
 Excepción personalizada que se lanza cuando se intenta registrar o actualizar
 un usuario con un correo institucional que ya pertenece a otra cuenta:
 Se traduce a un código HTTP 409 (Conflict).
 */
public class CorreoDuplicadoException extends RuntimeException {

    //Constructor que recibe el mensaje indicando que el correo ya está en uso
    public CorreoDuplicadoException(String mensaje) {
        super(mensaje);
    }
}