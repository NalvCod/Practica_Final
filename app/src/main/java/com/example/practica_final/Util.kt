package com.example.practica_final

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.practica_final.Cartas.Carta
import com.example.practica_final.Eventos.Evento
import com.example.practica_final.Usuarios.Usuario
import com.google.firebase.database.DatabaseReference

class Util {
    companion object{
        fun anadir_usuario(db_ref: DatabaseReference, id: String, usuario: Usuario) {
            db_ref.child("usuarios").child(id).setValue(usuario)
        }

        fun anadir_carta(db_ref: DatabaseReference, id: String, carta: Carta){
            db_ref.child("cartas").child(id).setValue(carta)
        }

        fun anadir_evento(db_ref: DatabaseReference, id: String, evento: Evento){
            db_ref.child("eventos").child(id).setValue(evento)
        }

        fun existe_evento(lista_eventos: List<Evento>, nombre: String): Boolean{
            return lista_eventos.any{ it.nombre!!.lowercase() == nombre.lowercase()}
        }

        fun existeUsuario(usuarios: List<Usuario>, nombre: String): Boolean {
            return usuarios.any { it.nombre!!.lowercase() == nombre.lowercase() }
        }

        fun toastCorrutina(activity: AppCompatActivity, contexto: Context, texto: String) {
            activity.runOnUiThread {
                Toast.makeText(contexto, texto, Toast.LENGTH_SHORT).show()
            }
        }

        fun opcionesGlide(context: Context): RequestOptions {
            val options = RequestOptions()
                .placeholder(animacion_carga(context))
                .error(R.drawable.magic_tras)
            return options
        }

        val transicion = DrawableTransitionOptions.withCrossFade(500)

        fun animacion_carga(contexto: Context): CircularProgressDrawable {
            val animacion = CircularProgressDrawable(contexto)
            animacion.strokeWidth = 5f
            animacion.centerRadius = 30f
            animacion.start()

            return animacion
        }

    }

}