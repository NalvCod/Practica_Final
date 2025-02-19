package com.example.practica_final.Usuarios

import java.io.Serializable


data class Usuario (
    var id : String? = "",
    var nombre: String? = "Usuario",
    var contrasena: String? = "contraseña",
    var email: String? ="correo",
    var esAdmin: Boolean? = false,
    var url_foto: String = "",
    var dinero: Float = 0f
): Serializable {
    constructor() : this("")
}