package com.example.practica_final

data class Usuario (var nombre: String = "Usuario", var contrasena: String = "contraseña") {
    override fun toString(): String {
        return "Usuario(nombre='$nombre', contrasena='$contrasena')"
    }


}