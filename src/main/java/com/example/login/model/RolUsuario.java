package com.example.login.model;

/**
 Enumeración que define los únicos roles que están permitidos en el sistema de
 Gestión de Reservas, los nombres deben coincidir exactamente con el rol de
 usuario de la app Android para evitar errores al convertir JSON con Retrofit
 */

public enum RolUsuario {

    //Perfil 'relator' solicitan y gestionan sus propias reservas de salas
    RELATOR,

    //Perfil 'administrador/supervisor' encargado de horarios, salas, auditoría y reportes
    COORDINADOR
}
