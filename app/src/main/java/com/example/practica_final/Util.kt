package com.example.practica_final

import com.example.practica_final.Usuarios.Usuario
import com.google.firebase.database.DatabaseReference

class Util {
    companion object{
        fun anadir_usuario(db_ref: DatabaseReference, id: String, usuario: Usuario) {
            db_ref.child("usuarios").child(id).setValue(usuario)
        }
    }

}