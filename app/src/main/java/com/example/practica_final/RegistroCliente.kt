package com.example.practica_final

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.practica_final.databinding.ActivityRegistroClienteBinding

class RegistroCliente : AppCompatActivity() {
    private lateinit var binding: ActivityRegistroClienteBinding
    private lateinit var usuario: Usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegistroClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.registrarse.setOnClickListener {
            val nombre = binding.introducirNombre.text.toString()
            val contrasena = binding.introducirContrasena.text.toString()

            if (nombre.isNotEmpty() && contrasena.isNotEmpty()) {
                intent = Intent(this, Pantalla_Principal::class.java)
                usuario = Usuario(nombre = nombre, contrasena = contrasena)
                startActivity(intent)
            }else{
                binding.introducirNombre.error = "Por favor ingrese un nombre"
                binding.introducirContrasena.error = "Por favor ingrese una contraseña"
            }
        }

        binding.imagenPerfil.setOnClickListener{
            //Acceder a galeria y elegir foto
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 1)

            binding.imagenPerfil.setImageResource(R.drawable.usuario_generico)

        }

    }
}