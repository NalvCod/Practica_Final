package com.example.practica_final.Eventos

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.practica_final.R
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
    private lateinit var lista: MutableList<Usuario>
    private lateinit var db_ref: DatabaseReference
    private lateinit var adaptador: UsuarioAdapter
    private lateinit var eventoId: String
    private lateinit var participantesString: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleEventoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lista = mutableListOf()
        db_ref = FirebaseDatabase.getInstance().reference

        eventoId = intent.getStringExtra("id_evento") ?: ""

        adaptador = UsuarioAdapter(lista)
        binding.participantes.adapter = adaptador
        binding.participantes.layoutManager = LinearLayoutManager(applicationContext)

        obtenerParticipantesDelEvento()
    }

    private fun obtenerParticipantesDelEvento() {

        db_ref.child("eventos").child(eventoId)  // Obtener el evento por su ID
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Obtener el objeto Evento completo
                    val evento = snapshot.getValue(Evento::class.java)
                    if (evento != null) {
                        Log.d("DetalleEventoActivity", "Evento obtenido: ${evento.nombre}")

                    }

                    evento?.let {
                        val participantesString = it.participantes ?: ""
                        val participantesIds = participantesString.split(",").map { it.trim() }

                        Log.d("DetalleEventoActivity", "Participantes IDs: $participantesIds")
                        lista.clear()

                        // Obtener los datos de cada usuario usando los IDs
                        obtenerDatosUsuarios(participantesIds)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }


    private fun obtenerDatosUsuarios(participantesIds: List<String>) {
        participantesIds.forEach { usuarioId ->
            db_ref.child("usuarios").child(usuarioId).get()
                .addOnSuccessListener { usuarioSnapshot ->
                    val usuario = usuarioSnapshot.getValue(Usuario::class.java)
                    usuario?.let {
                        // Agregar el usuario a la lista
                        lista.add(it)
                        adaptador.notifyDataSetChanged()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("DetalleEventoActivity", "Error al obtener los datos del usuario", exception)
                }
        }
    }
}
