package com.example.practica_final.Cartas

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.practica_final.databinding.ActivityDetalleCartaBinding
import com.google.firebase.database.*

class DetalleCarta : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleCartaBinding
    private lateinit var db_ref: DatabaseReference
    private lateinit var cartaId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleCartaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializamos la referencia de Firebase
        db_ref = FirebaseDatabase.getInstance().reference

        // Obtenemos el id de la carta desde el intent
        cartaId = intent.getStringExtra("id_carta") ?: ""

        // Cargar los datos de la carta
        obtenerDetallesCarta(cartaId)
    }

    private fun obtenerDetallesCarta(cartaId: String) {
        db_ref.child("cartas").child(cartaId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val carta = snapshot.getValue(Carta::class.java)
                carta?.let {
                    // Actualizamos la UI con los datos de la carta
                    binding.tituloCarta.text = it.nombre
                    binding.descripcionCarta.text = it.descripcion
                    binding.precio.text = "${it.precio}€"
                    // Aquí puedes usar una librería como Glide o Picasso para cargar la imagen desde la URL (si es que tienes una)
                    // Glide.with(this@DetalleCarta).load(it.imagenUrl).into(binding.imagenCarta)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar el error
            }
        })
    }
}
