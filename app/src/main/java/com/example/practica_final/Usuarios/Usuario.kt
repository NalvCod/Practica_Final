package com.example.practica_final.Usuarios

data class Usuario (
    var nombre: String = "Usuario",
    var contrasena: String = "contraseña",
    var email: String,
    var esAdmin: Boolean,
    var url_foto: String
    )

{
    override fun toString(): String {
        return "Usuario(nombre='$nombre', contrasena='$contrasena')"
    }


}