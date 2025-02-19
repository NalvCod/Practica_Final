package com.example.practica_final.Eventos

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.practica_final.Usuarios.Usuario
import com.example.practica_final.Usuarios.UsuarioAdapter
import com.example.practica_final.databinding.ActivityDetalleEventoBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Evento_Detalle : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleEventoBinding
    private lateinit var lista: MutableList<String>
    private lateinit var db_ref: DatabaseReference
    private lateinit var adaptador: UsuarioAdapter
    private lateinit var eventoId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configurar la vista de la actividad
        binding = ActivityDetalleEventoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lista = mutableListOf()
        db_ref = FirebaseDatabase.getInstance().reference

        // Obtener el ID del evento pasado como extra en el Intent
        eventoId = intent.getStringExtra("id_evento") ?: ""

        // Inicializar el adaptador para el RecyclerView
        adaptador = UsuarioAdapter(lista, eventoId)

        // Configurar RecyclerView
        binding.participantes.adapter = adaptador
        binding.participantes.layoutManager = LinearLayoutManager(applicationContext)

        // Obtener los participantes del evento desde la base de datos
        obtenerParticipantesDelEvento()
    }

    // Función para obtener los participantes del evento
    private fun obtenerParticipantesDelEvento() {
        lista.clear()
        db_ref.child("eventos").child(eventoId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Obtener el objeto Evento completo
                val evento = snapshot.getValue(Evento::class.java)

                evento?.let {
                    Log.d("DetalleEventoActivity", "Evento obtenido: ${evento.nombre}")
                    val participantesIds = evento.participantes // Lista de IDs de participantes
                    Log.d("DetalleEventoActivity", "Participantes IDs: $participantesIds")

                    // Obtener los datos de cada usuario usando los IDs
                    obtenerDatosUsuarios(participantesIds)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DetalleEventoActivity", "Error al obtener los datos del evento", error.toException())
            }
        })
    }

    // Función para obtener los datos de los usuarios participantes
    private fun obtenerDatosUsuarios(participantesIds: List<String>) {
        db_ref.child("usuarios").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Limpiar la lista de usuarios antes de agregar los nuevos
                lista.clear()

                snapshot.children.forEach { hijo: DataSnapshot ->
                    // Obtener el objeto Usuario de cada hijo
                    val usuario = hijo.getValue(Usuario::class.java)
                    Log.d("DetalleEventoActivity", "Usuario obtenido: ${usuario?.nombre}")

                    // Verificar si el ID del usuario está en la lista de participantes
                    if (usuario != null && participantesIds.contains(usuario.id)) {
                        // Agregar el ID del usuario a la lista
                        lista.add(usuario.id!!)
                    }
                }

                // Notificar al adaptador que los datos han cambiado
                adaptador.notifyDataSetChanged()
                Log.d("DetalleEventoActivity", "Lista de participantes actualizada: $lista")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DetalleEventoActivity", "Error al obtener los datos de los usuarios", error.toException())
            }
        })
    }
}


