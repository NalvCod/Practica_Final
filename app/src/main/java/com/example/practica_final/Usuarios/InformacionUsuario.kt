package com.example.practica_final.Usuarios

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.practica_final.Cartas.Carta
import com.example.practica_final.Cartas.CartaAdapter
import com.example.practica_final.Eventos.Evento
import com.example.practica_final.Eventos.EventoAdapter
import com.example.practica_final.R
import com.example.practica_final.databinding.ActivityInformacionUsuarioBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class InformacionUsuario : AppCompatActivity() {
    private lateinit var binding: ActivityInformacionUsuarioBinding
    private lateinit var db_ref: DatabaseReference
    private lateinit var listaEventos: MutableList<Evento>
    private lateinit var listaCartas: MutableList<Carta>
    private lateinit var adaptador_evento: EventoAdapter
    private lateinit var adaptador_carta: CartaAdapter
    private lateinit var contexto: Context
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the binding after calling super.onCreate()
        binding = ActivityInformacionUsuarioBinding.inflate(layoutInflater)

        // Now set the content view
        setContentView(binding.root)

        // Initialize other properties or perform actions after setContentView
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun obtenerEventosDeUsuario() {
        val usuarioId = sharedPreferences.getString("id", "")
        if (!usuarioId.isNullOrEmpty()) {
            db_ref.child("eventos").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    listaEventos.clear()

                    snapshot.children.forEach { hijo ->
                        val evento = hijo.getValue(Evento::class.java)
                        if (evento != null && evento.participantes.contains(usuarioId)) {
                            listaEventos.add(evento)  // Solo agregar eventos donde el usuario es participante
                        }
                    }

                    // Notificar al adaptador que los datos han cambiado
                    adaptador_evento.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("PantallaPrincipal", "Error al obtener los eventos: ${error.message}")
                }
            })
        }
    }
}
