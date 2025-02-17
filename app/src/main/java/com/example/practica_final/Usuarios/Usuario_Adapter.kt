package com.example.practica_final.Usuarios

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.practica_final.Eventos.Evento_Detalle
import com.example.practica_final.databinding.ItemUsuariosEventoBinding
import com.google.firebase.database.FirebaseDatabase

class UsuarioAdapter(
    private val lista_usuarios: MutableList<String>,  // Ahora es una lista de IDs de usuario
    private val id_evento_actual: String
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

    override fun getItemCount(): Int = lista_usuarios.size

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        val idUsuarioActual = lista_usuarios[position]

        // Obtén los detalles del usuario usando su ID
        database.reference.child("usuarios").child(idUsuarioActual).get()
            .addOnSuccessListener { snapshot ->
                val usuarios = snapshot.getValue(Usuario::class.java)

                if (usuarios != null) {
                    Log.d("UsuarioAdapter", "Usuario actual: ${usuarios.nombre}")

                    holder.binding.apply {
                        usuario.text = usuarios.nombre

                        // Si el usuario tiene una foto, cargamos la imagen con Glide
                        if (usuarios.url_foto.isNotEmpty()) {
                            Glide.with(contexto).load(usuarios.url_foto).into(fotoPerfil)
                        }

                        // Acción para eliminar el usuario del evento
                        borrar.setOnClickListener {
                            val idUsuario = sharedPreferences.getString("id", "")

                            // Solo se puede eliminar si el ID del usuario actual coincide con el ID del usuario
                            // o si el usuario es un administrador
                            if (idUsuario != null && (idUsuario == idUsuarioActual || usuarios.esAdmin == true)) {
                                val refParticipantes =
                                    database.reference.child("eventos").child(id_evento_actual)
                                        .child("participantes")

                                // Eliminar el usuario de la lista de participantes
                                refParticipantes.get().addOnSuccessListener { snapshot ->
                                    val participantes = snapshot.children.map { it.getValue(String::class.java) }
                                    val participantesActualizados = participantes.filterNot { it == idUsuarioActual }

                                    // Actualizar la lista de participantes en Firebase
                                    refParticipantes.setValue(participantesActualizados)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                lista_usuarios.removeAt(position)
                                                notifyItemRemoved(position)
                                                Toast.makeText(
                                                    contexto,
                                                    "Usuario eliminado correctamente",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } else {
                                                Toast.makeText(
                                                    contexto,
                                                    "Error al eliminar el usuario",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                Log.e(
                                                    "Usuario",
                                                    "Error al eliminar el usuario",
                                                    task.exception
                                                )
                                            }
                                        }
                                }
                            } else {
                                Toast.makeText(
                                    contexto,
                                    "No tienes permisos para eliminar este usuario",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                } else {
                    Log.e("UsuarioAdapter", "No se encontró el usuario con ID: $idUsuarioActual")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("UsuarioAdapter", "Error al obtener datos del usuario", exception)
            }
    }
}
