package com.example.practica_final.Eventos

import com.example.practica_final.Usuarios.Usuario

data class Evento (
    var id : String = "",
    var nombre : String = "",
    var descripcion : String = "",
    var fecha : String = "",
    var aforo: Int = 0,
    var precio: Float = 0f,
    var participantes : MutableList<String> = mutableListOf(),
    var url_imagen: String = ""
        )
    {

    }