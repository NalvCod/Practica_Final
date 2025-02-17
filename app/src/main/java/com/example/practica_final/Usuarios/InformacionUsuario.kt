package com.example.practica_final.Usuarios

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.practica_final.Cartas.CartaAdapter
import com.example.practica_final.Eventos.EventoAdapter
import com.example.practica_final.R
import com.example.practica_final.databinding.ActivityInformacionUsuarioBinding
import com.google.firebase.database.DatabaseReference

class InformacionUsuario : AppCompatActivity() {
    val binding = ActivityInformacionUsuarioBinding.inflate(layoutInflater)
    private lateinit var database : DatabaseReference
    private lateinit var adaptador_evento : EventoAdapter
    private lateinit var adaptador_carta : CartaAdapter
    private lateinit var contexto: Context
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(binding.root)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}