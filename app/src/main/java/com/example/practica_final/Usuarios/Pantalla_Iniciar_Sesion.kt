package com.example.practica_final.Usuarios

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.practica_final.MainActivity
import com.example.practica_final.R
import com.example.practica_final.databinding.ActivityPantallaIniciarSesionBinding
import com.example.practica_final.databinding.ActivityRegistroClienteBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.appwrite.Client
import io.appwrite.services.Storage

class Pantalla_Iniciar_Sesion : AppCompatActivity() {

    private lateinit var binding: ActivityPantallaIniciarSesionBinding
    private lateinit var database: DatabaseReference
    private lateinit var storage: Storage
    private lateinit var id_projecto: String
    private lateinit var id_bucket: String
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Usar solo una vez para inflar el binding
        binding = ActivityPantallaIniciarSesionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializamos SharedPreferences
        sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE)
        database = FirebaseDatabase.getInstance().reference
        id_projecto = "67a4e7f40018ce9b3ca7"
        id_bucket = "67a4e8c0002f1ef58c66"
        val client = Client().setEndpoint("https://cloud.appwrite.io/v1").setProject(id_projecto)
        storage = Storage(client)

        checkUserLoggedIn()

        // Configuramos el botón de login
        binding.registrarse.setOnClickListener {
            val username = binding.introducirNombre.text.toString()
            val password = binding.introducirContrasena.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                database.child("usuarios").orderByChild("nombre").equalTo(username).addListenerForSingleValueEvent(object :
                    ValueEventListener {
                     override fun onDataChange(snapshot: DataSnapshot) {
                        // Si el usuario existe en la base de datos
                        if (snapshot.exists()) {
                            val userSnapshot = snapshot.children.first()  // Obtener el primer usuario que coincida
                            val passwordFromDatabase = userSnapshot.child("contrasena").value.toString()
                            Log.d("PASS", passwordFromDatabase)
                            //obtener url usuario
                            val url = userSnapshot.child("url_foto").value.toString()
                            Log.d("UUUUUUUUUUUUURL", url)
                            if (password == passwordFromDatabase) {
                                saveUser(userSnapshot.child("id").value.toString(), password, url)

                                Toast.makeText(this@Pantalla_Iniciar_Sesion, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@Pantalla_Iniciar_Sesion, Pantalla_Principal::class.java)
                                startActivity(intent)
                            } else {
                                Toast.makeText(this@Pantalla_Iniciar_Sesion, "Contraseña incorrecta", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this@Pantalla_Iniciar_Sesion, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Manejo de errores en la consulta
                        Log.e("FirebaseError", "Error al obtener los datos: ${error.message}")
                        Toast.makeText(this@Pantalla_Iniciar_Sesion, "Error al verificar los datos", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                // Verificar si los campos están vacíos
                Toast.makeText(this, "Por favor, ingresa los datos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Función para guardar el usuario en SharedPreferences
    private fun saveUser(username: String, password: String, url: String) {
        Log.d("URLLLLL", url)
        val editor = sharedPreferences.edit()
        editor.putString("username", username)
        editor.putString("password", password)
        editor.putString("imagen", url)
        editor.putBoolean("esAdmin", true)
        editor.putBoolean("isLoggedIn", true)
        editor.apply()
    }

    private fun checkUserLoggedIn() {
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        if (isLoggedIn) {
            val username = sharedPreferences.getString("username", "")
            Toast.makeText(this, "Bienvenido de nuevo, $username", Toast.LENGTH_SHORT).show()
        }
    }
}
