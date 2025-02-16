package com.example.practica_final.Usuarios

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.practica_final.Cartas.Ver_Cartas
import com.example.practica_final.Eventos.RegistrarEvento
import com.example.practica_final.Eventos.Ver_Eventos
import com.example.practica_final.R
import com.example.practica_final.databinding.ActivityPantallaPrincipalBinding

class Pantalla_Principal : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        val binding = ActivityPantallaPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        var perfilUrl = sharedPreferences.getString("imagen", "")
        if (!perfilUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(perfilUrl)  // Aquí es donde cargamos la URL de la imagen
                .placeholder(R.drawable.magic_tras)
                .into(binding.perfil)
        } else {
            // Si no hay URL, podemos establecer una imagen por defecto
            binding.perfil.setImageResource(R.drawable.baseline_supervised_user_circle_24)
        }

        binding.gestionar.setOnClickListener {
            val intent = Intent(this, RegistrarEvento::class.java)
            startActivity(intent)
        }
        binding.verEventos.setOnClickListener {
            val intent = Intent(this, Ver_Eventos::class.java)
            startActivity(intent)
        }
        binding.verCartas.setOnClickListener {
            val intent = Intent(this, Ver_Cartas::class.java)
            startActivity(intent)
        }
    }
}