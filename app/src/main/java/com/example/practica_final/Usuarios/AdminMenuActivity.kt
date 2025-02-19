package com.example.practica_final.Usuarios

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.practica_final.Cartas.PedidosAdminActivity
import com.example.practica_final.Cartas.Registrar_Cartas
import com.example.practica_final.Cartas.VerCartasActivity
import com.example.practica_final.Eventos.Modificar_Evento
import com.example.practica_final.Eventos.RegistrarEvento
import com.example.practica_final.R
import com.example.practica_final.databinding.ActivityAdminMenuBinding

class AdminMenuActivity : AppCompatActivity() {
    //binding
    private lateinit var binding: ActivityAdminMenuBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAdminMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.eventoEditar.setOnClickListener{
            intent = Intent(this, Modificar_Evento::class.java)
            startActivity(intent)
        }

        binding.eventoCrear.setOnClickListener{
            intent = Intent(this, RegistrarEvento::class.java)
            startActivity(intent)
        }

        binding.cartaEditar.setOnClickListener{
            intent = Intent(this, VerCartasActivity::class.java)
            startActivity(intent)
        }

        binding.cartaCrear.setOnClickListener{
            intent = Intent(this, Registrar_Cartas::class.java)
            startActivity(intent)
        }

        binding.gestionarPedidos.setOnClickListener{
            intent = Intent(this, PedidosAdminActivity::class.java)
            startActivity(intent)
        }
    }
}