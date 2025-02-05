package com.example.practica_final.Usuarios

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.practica_final.R
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
                binding.introducirCorreo.error = "Por favor ingrese un correo"
            }
        }

        binding.fotoPerfil.setOnClickListener{
            //Acceder a galeria y elegir foto
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            // Obtener la URI de la imagen seleccionada
            val selectedImageUri: Uri? = data.data

            // Establecer la URI seleccionada como imagen en la ImageView
            binding.fotoPerfil.setImageURI(selectedImageUri)
        }
    }

}