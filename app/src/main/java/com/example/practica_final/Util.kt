package com.example.practica_final

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.practica_final.Cartas.Carta_Magic
import com.example.practica_final.Usuarios.Usuario
import com.google.firebase.database.DatabaseReference

class Util {
    companion object{
        fun anadir_usuario(db_ref: DatabaseReference, id: String, usuario: Usuario) {
            db_ref.child("usuarios").child(id).setValue(usuario)
        }

        fun anadir_carta(db_ref: DatabaseReference, id: String, carta: Carta_Magic){
            db_ref.child("cartas").child(id).setValue(carta)
        }

        fun existeUsuario(usuarios: List<Usuario>, nombre: String): Boolean {
            return usuarios.any { it.nombre!!.lowercase() == nombre.lowercase() }
        }

        fun toastCorrutina(activity: AppCompatActivity, contexto: Context, texto: String) {
            activity.runOnUiThread {
                Toast.makeText(contexto, texto, Toast.LENGTH_SHORT).show()
            }
        }
    }

}