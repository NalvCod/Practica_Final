package com.example.practica_final.Usuarios

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.practica_final.Cartas.VerCartasActivity
import com.example.practica_final.Eventos.Evento
import com.example.practica_final.Eventos.EventoAdapter
import com.example.practica_final.Eventos.RegistrarEvento
import com.example.practica_final.Eventos.VerEventoActivity
import com.example.practica_final.R
import com.example.practica_final.databinding.ActivityPantallaPrincipalBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PantallaPrincipalActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var recycler: RecyclerView
    private lateinit var listaEventos: MutableList<Evento>
    private lateinit var adaptador: EventoAdapter
    private lateinit var db_ref: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        val binding = ActivityPantallaPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configurar el perfil de usuario
        var perfilUrl = sharedPreferences.getString("imagen", "")
        if (!perfilUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(perfilUrl)  // Aquí cargamos la URL de la imagen
                .placeholder(R.drawable.magic_tras)
                .into(binding.perfil)
        } else {
            // Imagen por defecto si no hay URL
            binding.perfil.setImageResource(R.drawable.baseline_supervised_user_circle_24)
        }

        // Inicializar RecyclerView
        recycler = binding.listaEventos
        recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recycler.setHasFixedSize(true)

        // Inicializar lista de eventos y adaptador
        listaEventos = mutableListOf()
        adaptador = EventoAdapter(listaEventos)
        recycler.adapter = adaptador

        // Referencia a la base de datos
        db_ref = FirebaseDatabase.getInstance().reference

        // Obtener eventos a los que el usuario está registrado
        obtenerEventosDeUsuario()

        binding.gestionar.setOnClickListener {
            val intent = Intent(this, RegistrarEvento::class.java)
            startActivity(intent)
        }

        binding.verEventos.setOnClickListener {
            val intent = Intent(this, VerEventoActivity::class.java)
            startActivity(intent)
        }

        binding.verCartas.setOnClickListener {
            val intent = Intent(this, VerCartasActivity::class.java)
            startActivity(intent)
        }

        binding.perfil.setOnClickListener {
            val intent = Intent(this, InformacionUsuario::class.java)
            startActivity(intent)
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
                    adaptador.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("PantallaPrincipal", "Error al obtener los eventos: ${error.message}")
                }
            })
        }
    }
}
