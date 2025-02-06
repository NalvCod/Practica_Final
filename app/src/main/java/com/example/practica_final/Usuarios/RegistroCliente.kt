package com.example.practica_final.Usuarios

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.practica_final.R
import com.example.practica_final.Util
import com.example.practica_final.databinding.ActivityRegistroClienteBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegistroCliente : AppCompatActivity() {
    private lateinit var binding: ActivityRegistroClienteBinding
    private lateinit var usuario: Usuario
    private lateinit var database: DatabaseReference

    //La imagen es un URI pq
    private var url_imagen: Uri? = null
    private lateinit var id_projecto: String
    private lateinit var id_bucket: String

    @SuppressLint("UnsafeIntentLaunch")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        database = FirebaseDatabase.getInstance().reference
        id_projecto = "67a4e7f40018ce9b3ca7"
        id_bucket = "67a4e8c0002f1ef58c66"
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
            val contrasena2 = binding.repetirContrasena.text.toString()
            val email = binding.introducirCorreo.text.toString()

            if (comprobarUsuario(nombre, contrasena, contrasena2, email)){
                crearUsuario(nombre, contrasena, contrasena2, email)
                intent = Intent(this, Pantalla_Principal::class.java)
                startActivity(intent)
            }
        }

        binding.fotoPerfil.setOnClickListener {
            //Acceder a galeria y elegir foto
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data
            binding.fotoPerfil.setImageURI(selectedImageUri)
        }
    }

    fun crearUsuario(nombre: String, contrasena: String, contrasena2: String, email: String): Usuario{
        lateinit var usuario : Usuario

        val id = database.child("usuarios").push().key
        usuario = Usuario(id, nombre, contrasena, email, false, url_imagen)

        Util.anadir_usuario(database, id!!, usuario)


        return usuario
    }

    fun comprobarUsuario(nombre: String, contrasena: String, contrasena2: String, email: String):Boolean {
        var todoCorrecto = false
        if (nombre.isNotEmpty() && contrasena.isNotEmpty()) {
            //Si todos los datos son correctos, creo el objeto
            if (comprobarNombre(nombre) && comprobarContrasena(contrasena, contrasena2) && comprobarCorreo(email)) {
                todoCorrecto = true
            }
        } else {
            if (comprobarNombre(nombre) == false){
                binding.introducirNombre.error = "El nombre de usuario no es correcto"
                binding.introducirNombreLayout.boxStrokeColor = resources.getColor(R.color.rojito)
                binding.introducirNombreLayout.hintTextColor = resources.getColorStateList(R.color.rojito)
            }
            if (comprobarContrasena(contrasena, contrasena2) == false){
                binding.introducirContrasenaLayout.boxStrokeColor = resources.getColor(R.color.rojito)
                binding.introducirContrasenaLayout.hintTextColor = resources.getColorStateList(R.color.rojito)
                binding.repetirContrasenaLayout.boxStrokeColor = resources.getColor(R.color.rojito)
                binding.repetirContrasenaLayout.hintTextColor = resources.getColorStateList(R.color.rojito)
            }

            if (comprobarCorreo(email) == false){
                binding.introducirCorreo.error = "El correo no es correcto"
                binding.introducirCorreoLayout.boxStrokeColor = resources.getColor(R.color.rojito)
                binding.introducirCorreoLayout.hintTextColor = resources.getColorStateList(R.color.rojito)
            }
            binding.introducirCorreo.error = "Ingresa un correo válido"
        }
        return todoCorrecto
    }

    fun comprobarNombre(nombre: String): Boolean {
        var esCorrecto = false
        if (nombre.length > 4) {
            database.child("usuarios").orderByChild("nombre").equalTo(nombre)
                .get().addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        binding.introducirNombre.error = "El nombre de usuario ya existe"
                    } else esCorrecto = true

                }.addOnFailureListener { exception ->
                    Log.v("CONSULTA", "Error al consultar la base de datos: ${exception.message}")
                    binding.introducirNombre.error = "Hubo un error al verificar el nombre"
                }
            return esCorrecto
        } else {
            binding.introducirNombre.error = "El nombre de usuario debe tener al menos 5 caracteres"
            return false
        }
    }

    fun comprobarCorreo(correo: String): Boolean {
        var esCorrecto = false
        database.child("usuarios").orderByChild("email").equalTo(correo).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    binding.introducirCorreo.error = "El correo ya está registrado"
                } else esCorrecto = true
                }.addOnFailureListener { exception ->
                Log.v("CONSULTA", "Error al consultar la base de datos: ${exception.message}")
                binding.introducirCorreo.error = "Hubo un error al verificar el correo"
            }
        return esCorrecto
    }

    fun comprobarContrasena(contrasena: String, contrasena2: String): Boolean {
        if (contrasena != contrasena2) {
            binding.repetirContrasena.error = "La contraseñas no coincide"
            binding.repetirContrasenaLayout.boxStrokeColor = resources.getColor(R.color.rojito)
            return false
        } else {
            //comprobar que la contraseña tenga mas de 8 caracteres y una mayuscula
            if (contrasena.length > 8 && contrasena.any { it.isUpperCase() }) {
                return true
            } else {
                binding.introducirContrasena.error = "La contraseña debe tener al menos 8 caracteres y una mayúscula"
                binding.repetirContrasena.error = "La contraseña debe tener al menos 8 caracteres y una mayúscula"
                return false
            }
        }
    }

    fun generar_URL_Imagen(url: Uri?) {

    }
}

