package com.example.practica_final.Usuarios

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.practica_final.databinding.ItemUsuariosEventoBinding
import com.google.firebase.database.FirebaseDatabase

class UsuarioAdapter(private val lista_usuarios: MutableList<Usuario>) :
    RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder>() {

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
        val usuario_actual = lista_usuarios[position]
        Log.d("UsuarioAdapter", "Usuario actual: ${usuario_actual.nombre}")
        Log.d("UsuarioAdapter", "Lista de usuarios: $lista_usuarios")

        holder.binding.apply {
            usuario.text = usuario_actual.nombre

            if (usuario_actual.url_foto.isNotEmpty()) {
                Glide.with(contexto).load(usuario_actual.url_foto).into(fotoPerfil)
            }


            borrar.setOnClickListener {
                val idUsuario = sharedPreferences.getString("id", "")
                if (usuario_actual.id == idUsuario || usuario_actual.esAdmin == true) {
                    database.reference.child("usuarios").child(usuario_actual.id ?: "").removeValue()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                lista_usuarios.removeAt(position)
                                notifyItemRemoved(position)
                                notifyItemRangeChanged(position, lista_usuarios.size)
                                Toast.makeText(contexto, "Usuario eliminado correctamente", Toast.LENGTH_SHORT).show()

                            } else {
                                Toast.makeText(contexto, "Error al eliminar el usuario", Toast.LENGTH_SHORT).show()
                                Log.e("Usuario", "Error al eliminar el usuario", task.exception)
                            }
                        }
                } else {
                    Toast.makeText(contexto, "No tienes permisos para eliminar este usuario", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
