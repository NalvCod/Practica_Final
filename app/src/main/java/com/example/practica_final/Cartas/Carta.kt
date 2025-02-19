package com.example.practica_final.Cartas

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Carta (var id: String = "", var nombre:String = "carta", var precio: Float = 0f, var color: String = "color", var descripcion : String = "", var unidades: Int = 0, var url_imagen: String ="") :
    Parcelable {
}