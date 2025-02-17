package com.example.practica_final.Usuarios

import android.net.Uri

data class Usuario (
    var id : String? = "",
    var nombre: String? = "Usuario",
    var contrasena: String? = "contraseña",
    var email: String? ="correo",
    var esAdmin: Boolean? = false,
    var url_foto: String = "",
    var cartas_compradas: MutableList<String> = mutableListOf()
    )

{

    override fun toString(): String {
        return "Usuario(nombre='$nombre', contrasena='$contrasena')"
    }


}