package com.example.practica_final.Eventos

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import com.example.practica_final.databinding.ActivityItemEventosBinding
import com.google.firebase.database.FirebaseDatabase

class EventoAdapter(private val lista_eventos: MutableList<Evento>) : RecyclerView.Adapter<EventoAdapter.EventoViewHolder>() {
    private lateinit var contexto: Context

    // ViewHolder con ViewBinding
    inner class EventoViewHolder(val binding: ActivityItemEventosBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventoViewHolder {
        contexto = parent.context
        val binding = ActivityItemEventosBinding.inflate(LayoutInflater.from(contexto), parent, false)
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
            eventoDetallesButton.setOnClickListener {
                val intent = Intent(contexto, Evento_Detalle::class.java)
                intent.putExtra("id_evento", evento_actual.nombre)
                contexto.startActivity(intent)
            }

            eventoApuntarse.setOnClickListener {
                // Acción para apuntarse al evento (implementa lo que necesites)
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
