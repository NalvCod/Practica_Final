package com.example.practica_final.Usuarios

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import com.example.practica_final.R
import com.example.practica_final.databinding.ActivityPantallaInicioBinding
import java.util.prefs.Preferences



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
            intent = Intent(this, RegistroCliente::class.java)
            startActivity(intent)
        }

        binding.iniciarSesion.setOnClickListener {
            intent = Intent(this, Pantalla_Iniciar_Sesion::class.java)
            startActivity(intent)
        }

    }
}