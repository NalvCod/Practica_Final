package com.example.practica_final.Pedidos

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.practica_final.Eventos.Evento
import com.example.practica_final.Eventos.EventoAdapter
import com.example.practica_final.R
import com.example.practica_final.databinding.ActivityGestionarPedidoBinding
import com.google.firebase.database.FirebaseDatabase

class GestionarPedidoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGestionarPedidoBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var lista_eventos: MutableList<Evento>
    private lateinit var adapter: EventoAdapter
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityGestionarPedidoBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        binding.listaEventos.adapter = EventoAdapter(lista_eventos)
        binding.listaEventos.layoutManager = LinearLayoutManager(this)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }
}