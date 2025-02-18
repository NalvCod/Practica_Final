package com.example.practica_final.Usuarios

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.practica_final.databinding.ItemUsuariosEventoBinding
import com.google.firebase.database.FirebaseDatabase

class UsuarioAdapter(
    private val lista_usuarios_ids: MutableList<String>,
    eventoId: String
) : RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder>() {

    private lateinit var database: FirebaseDatabase
    private lateinit var contexto: Context
    private lateinit var sharedPreferences: SharedPreferences

    inner class UsuarioViewHolder(val binding: ItemUsuariosEventoBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        contexto = parent.context
        database = FirebaseDatabase.getInstance()
        sharedPreferences = contexto.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val binding =
            ItemUsuariosEventoBinding.inflate(LayoutInflater.from(contexto), parent, false)
        return UsuarioViewHolder(binding)
    }

    override fun getItemCount(): Int = lista_usuarios_ids.size

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        val idUsuarioActual = lista_usuarios_ids[position]  // ID del usuario actual

        database.reference.child("usuarios").child(idUsuarioActual).get()
            .addOnSuccessListener { snapshot ->
                val usuario = snapshot.getValue(Usuario::class.java)

                if (usuario != null) {
                    holder.binding.apply {
                        nombreUsu.text =
                            usuario.nombre  // Cambiar "usuario" por el ID real de la vista

                        // Si el usuario tiene una foto, cargamos la imagen con Glide
                        if (usuario.url_foto.isNotEmpty()) {
                            Glide.with(contexto).load(usuario.url_foto).into(fotoPerfil)
                        }

                    }
                }
            }
    }
}
