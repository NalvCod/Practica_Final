package com.example.practica_final.Usuarios

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.practica_final.R
import com.example.practica_final.databinding.ActivityPantallaInicioBinding


class Pantalla_Inicio : AppCompatActivity() {
    //binding
    private lateinit var binding: ActivityPantallaInicioBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityPantallaInicioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.registrarse.setOnClickListener {
            intent = Intent(this, Registro_Usuario::class.java)
            startActivity(intent)
        }

        binding.iniciarSesion.setOnClickListener {
            intent = Intent(this, Pantalla_Iniciar_Sesion::class.java)
            startActivity(intent)
        }

    }
}