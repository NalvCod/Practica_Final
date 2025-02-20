package com.example.practica_final.Usuarios

import android.content.Context
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
import com.example.practica_final.Cartas.Carta
import com.example.practica_final.Cartas.CartaAdapter
import com.example.practica_final.Eventos.Evento
import com.example.practica_final.Eventos.EventoAdapter
import com.example.practica_final.Pedidos.Pedido
import com.example.practica_final.R
import com.example.practica_final.databinding.ActivityInformacionUsuarioBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class InformacionUsuario : AppCompatActivity() {
    private lateinit var binding: ActivityInformacionUsuarioBinding
    private lateinit var db_ref: DatabaseReference
    private lateinit var listaEventos: MutableList<Evento>
    private lateinit var listaCartasCompradas: MutableList<Carta>
    private lateinit var adaptador_evento: EventoAdapter
    private lateinit var adaptador_carta: CartaAdapter
    private lateinit var recyclerEventos: RecyclerView
    private lateinit var recyclerCartas: RecyclerView
    private lateinit var contexto: Context
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE)
        binding = ActivityInformacionUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Cargar la imagen de perfil del usuario
        var perfilUrl = sharedPreferences.getString("imagen", "")
        Log.d("URL_IMAGEN", perfilUrl ?: "")
        if (!perfilUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(perfilUrl)
                .placeholder(R.drawable.magic_tras)
                .into(binding.fotoPerfil)
        } else {
            binding.fotoPerfil.setImageResource(R.drawable.baseline_supervised_user_circle_24)
        }

        // Configuración de RecyclerView para los eventos
        recyclerEventos = binding.listaEventos
        recyclerEventos.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerEventos.setHasFixedSize(true)

        listaEventos = mutableListOf()
        adaptador_evento = EventoAdapter(listaEventos)
        recyclerEventos.adapter = adaptador_evento

        // Configuración de RecyclerView para las cartas compradas
        recyclerCartas = binding.listaCartas
        recyclerCartas.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerCartas.setHasFixedSize(true)

        listaCartasCompradas = mutableListOf()
        adaptador_carta = CartaAdapter(this, listaCartasCompradas)
        recyclerCartas.adapter = adaptador_carta

        // Inicializar la referencia a la base de datos de Firebase
        db_ref = FirebaseDatabase.getInstance().reference

        // Obtener los eventos y las cartas del usuario
        obtenerEventosDeUsuario()
        obtenerCartasCompradasDeUsuario()
    }

    private fun obtenerCartasCompradasDeUsuario() {
        val usuarioId = sharedPreferences.getString("id", "")
        if (!usuarioId.isNullOrEmpty()) {
            db_ref.child("pedidos").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    listaCartasCompradas.clear() // Limpiar la lista antes de agregar los nuevos pedidos

                    snapshot.children.forEach { hijo ->
                        val pedido = hijo.getValue(Pedido::class.java)
                        if (pedido != null && pedido.id_usuario == usuarioId) {
                            // Si el pedido es del usuario, obtener la carta correspondiente
                            db_ref.child("cartas").child(pedido.id_carta).get().addOnSuccessListener { cartaSnapshot ->
                                val carta = cartaSnapshot.getValue(Carta::class.java)
                                if (carta != null) {
                                    listaCartasCompradas.add(carta)  // Agregar la carta comprada a la lista
                                }
                                // Notificar al adaptador que los datos han cambiado
                                adaptador_carta.notifyDataSetChanged()
                            }.addOnFailureListener {
                                Log.e("PantallaPrincipal", "Error al obtener la carta: ${it.message}")
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("PantallaPrincipal", "Error al obtener los pedidos: ${error.message}")
                }
            })
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

