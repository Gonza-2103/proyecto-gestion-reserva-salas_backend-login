package com.example.login.exception;

/**
 Excepción personalizada que se lanza cuando falla la autenticación en el login
 cuando haya un correo no registrado, contraseña incorrecta o cuenta
 desactivada: Se traduce a un código HTTP 401 (Unauthorized).
 */
public class CredencialesInvalidasException extends RuntimeException {

    //Constructor que recibe el motivo del rechazo de autenticación
    public CredencialesInvalidasException(String mensaje) {
        super(mensaje);
    }
}