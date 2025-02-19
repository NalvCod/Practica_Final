package com.example.practica_final.Cartas

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.practica_final.R
import com.example.practica_final.databinding.ActivityModificarCartasBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import io.appwrite.Client
import io.appwrite.services.Storage

class ModificarCartas : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var storage: Storage
    private lateinit var id_projecto: String
    private lateinit var id_bucket: String
    private lateinit var id_carta: String
    private lateinit var carta_actual: Carta
    private lateinit var binding: ActivityModificarCartasBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityModificarCartasBinding.inflate(layoutInflater)
        //inicializa database
        database = FirebaseDatabase.getInstance().reference
        //appwrite
        id_projecto = "67a4e7f40018ce9b3ca7"
        id_bucket = "67a4e8c0002f1ef58c66"

        setContentView(binding.root)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        id_carta = intent.getStringExtra("id_carta").toString()
        Log.d("id carta", id_carta)

        database.child("cartas").child(id_carta).get().addOnSuccessListener {
            carta_actual = it.getValue(Carta::class.java)!!
            Log.d("carta", carta_actual.descripcion)
            binding.introducirNombre.setText(carta_actual.nombre)
            binding.introducirDescripcion.setText(carta_actual.descripcion)
            binding.introducirPrecio.setText(carta_actual.precio.toString())
            binding.fotoPerfil.setImageURI(Uri.parse(carta_actual.url_imagen))
        }

        binding.modificar.setOnClickListener{
            //editar datos nuevos carta
            database.child("cartas").child(id_carta).child("nombre").setValue(binding.introducirNombre.text.toString())
        }



        database = FirebaseDatabase.getInstance().reference
        id_projecto = "67a4e7f40018ce9b3ca7"
        id_bucket = "67a4e8c0002f1ef58c66"
        val client = Client().setEndpoint("https://cloud.appwrite.io/v1").setProject(id_projecto)
        storage = Storage(client)



        val colores = listOf("Blanco (W)", "Azul (U)", "Negro (B)", "Rojo (R)", "Verde (G)")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, colores)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.colorSpinner.adapter = adapter
        binding.colorSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                val colorSeleccionado = colores[position]
                // Realiza la acción que desees con el color seleccionado
                Toast.makeText(this@ModificarCartas, "Color seleccionado: $colorSeleccionado", Toast.LENGTH_SHORT).show()

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }


    }
}