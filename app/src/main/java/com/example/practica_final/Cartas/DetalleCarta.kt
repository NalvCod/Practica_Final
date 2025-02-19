package com.example.practica_final.Cartas

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
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

        db_ref = FirebaseDatabase.getInstance().reference

        cartaId = intent.getStringExtra("id_carta") ?: ""
        Log.d("DetalleCarta", "ID de la carta: $cartaId")

        obtenerDetallesCarta(cartaId)
    }
    private fun obtenerDetallesCarta(cartaId: String) {
        db_ref.child("cartas").child(cartaId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Obtener el objeto Carta desde la base de datos
                val carta = snapshot.getValue(Carta::class.java)

                carta?.let {
                    // Actualizamos la UI con los datos de la carta
                    binding.tituloCarta.text = it.nombre
                    binding.descripcionCarta.text = it.descripcion
                    binding.precio.text = "${it.precio}€"

                    Glide.with(this@DetalleCarta)
                        .load(it.url_imagen)
                        .into(binding.imagenCarta)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DetalleCarta", "Error al obtener los datos de la carta", error.toException())
            }
        })
    }
}
