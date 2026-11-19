package com.example.login.exception;

/**
 Excepción personalizada que impide que un usuario registrado
 como RELATOR inicie sesión seleccionando COORDINADOR (o viceversa):
 Se traduce a un código HTTP 403 (Forbidden).
 */
public class RolNoCoincidenteException extends RuntimeException {

    // Constructor que recibe el mensaje explicando qué perfil corresponde realmente a la cuenta
    public RolNoCoincidenteException(String mensaje) {
        super(mensaje);
    }
}