package com.example.practica_final.Eventos

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import com.example.practica_final.databinding.ActivityItemEventosBinding
import com.google.firebase.database.FirebaseDatabase

class EventoAdapter(private val lista_eventos: MutableList<Evento>) :
    RecyclerView.Adapter<EventoAdapter.EventoViewHolder>() {
    private lateinit var database: FirebaseDatabase
    private lateinit var contexto: Context
    private lateinit var sharedPreferences: SharedPreferences

    inner class EventoViewHolder(val binding: ActivityItemEventosBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventoViewHolder {
        contexto = parent.context
        database = FirebaseDatabase.getInstance()
        sharedPreferences = contexto.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val binding =
            ActivityItemEventosBinding.inflate(LayoutInflater.from(contexto), parent, false)
        return EventoViewHolder(binding)
    }

    override fun getItemCount(): Int = lista_eventos.size

    override fun onBindViewHolder(holder: EventoViewHolder, position: Int) {
        val evento_actual = lista_eventos[position]
        holder.binding.apply {
            // Asignando los valores a las vistas usando ViewBinding
            eventoNombre.text = evento_actual.nombre
            eventoDescripcion.text = evento_actual.descripcion
            eventoFecha.text = evento_actual.fecha

            // Acciones para los botones
            informacion.setOnClickListener {
                val intent = Intent(contexto, Evento_Detalle::class.java)
                intent.putExtra("id_evento", evento_actual.id)
                contexto.startActivity(intent)
            }

            holder.binding.apuntarse.setOnClickListener {
                val idUsuario = sharedPreferences.getString("id", "")
                Log.d("Eventooooooo", "ID del usuario: $idUsuario")

                if (!idUsuario.isNullOrEmpty()) {
                    //comprobar que el usuario no esté ya registrado
                    if (evento_actual.participantes.contains(idUsuario)) {
                        Toast.makeText(contexto, "Ya estás registrado en este evento", Toast.LENGTH_SHORT).show()

                        return@setOnClickListener
                    }

                    // Concatenamos el nuevo ID al string de participantes
                    Log.d("Eventooooooo", "ID del usuario: $idUsuario")
                    evento_actual.participantes += "$idUsuario "
                    Log.d("Eventooooooo", "Participantes: ${evento_actual.participantes}")

                    // Actualizamos el valor de 'participantes' en Firebase
                    database.reference.child("eventos")
                        .child(evento_actual.id)
                        .child("participantes")
                        .setValue(evento_actual.participantes)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d("Evento", "Participante agregado correctamente")
                            } else {
                                Log.e("Evento", "Error al agregar participante", task.exception)
                            }
                        }
                }
            }



            // Acción para eliminar el evento
            eventoBorrar.setOnClickListener {
                lista_eventos.removeAt(position)
                val db_ref = FirebaseDatabase.getInstance().reference
                db_ref.child("eventos").child(evento_actual.id).removeValue()
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, lista_eventos.size)
            }
        }
    }
}

