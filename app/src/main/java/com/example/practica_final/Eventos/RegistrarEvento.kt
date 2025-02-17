package com.example.practica_final.Eventos

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.practica_final.R
import com.example.practica_final.Usuarios.Pantalla_Principal
import com.example.practica_final.databinding.ActivityRegistrarEventoBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar

class RegistrarEvento : AppCompatActivity() {
    private lateinit var binding: ActivityRegistrarEventoBinding
    private lateinit var database: DatabaseReference
    private lateinit var id_projecto: String
    private lateinit var id_bucket: String

    @SuppressLint("UnsafeIntentLaunch")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        database = FirebaseDatabase.getInstance().reference
        id_projecto = "67a4e7f40018ce9b3ca7"  // tu id de proyecto
        id_bucket = "67a4e8c0002f1ef58c66"    // tu id de bucket
        binding = ActivityRegistrarEventoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.anadirCarta.setOnClickListener {
            val nombre = binding.introducirNombre.text.toString()
            val descripcion = binding.descripcionEditText.text.toString()
            val fecha = binding.fechaEditText.text.toString()

            if (comprobarEvento(nombre, descripcion, fecha)) {
                crearEvento(nombre, descripcion, fecha)
            }
        }

        binding.fechaEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    binding.fechaEditText.setText(selectedDate)
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }
    }

    private fun crearEvento(nombre: String, descripcion: String, fecha: String) {
        val eventoId = database.child("eventos").push().key
        val evento = Evento(
            id = eventoId!!,
            nombre = nombre,
            descripcion = descripcion,
            fecha = fecha,
            participantes = ""
        )

        // Guardar evento en Firebase
        database.child("eventos").child(eventoId).setValue(evento).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this, "Evento creado con éxito", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, Pantalla_Principal::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Error al crear evento", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun comprobarEvento(nombre: String, descripcion: String, fecha: String): Boolean {
        return when {
            nombre.isEmpty() -> {
                Toast.makeText(this, "Por favor, ingrese un nombre", Toast.LENGTH_SHORT).show()
                false
            }
            descripcion.isEmpty() -> {
                Toast.makeText(this, "Por favor, ingrese una descripción", Toast.LENGTH_SHORT).show()
                false
            }
            fecha.isEmpty() -> {
                Toast.makeText(this, "Por favor, ingrese una fecha", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }
}