package com.example.practica_final.Cartas

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.practica_final.R
import com.example.practica_final.databinding.ActivityVerCartasBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class VerCartasActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var recycler: RecyclerView
    private lateinit var lista: MutableList<Carta>
    private lateinit var db_ref: DatabaseReference
    private lateinit var adaptador: CartaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        val binding = ActivityVerCartasBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE)
        recycler = findViewById(R.id.lista_series)
        lista = mutableListOf()
        db_ref = FirebaseDatabase.getInstance().reference

        adaptador = CartaAdapter(this, lista)
        recycler.adapter = adaptador
        recycler.layoutManager = GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false)  // Usamos GridLayout para mostrar las cartas en una cuadrícula

        var perfilUrl = sharedPreferences.getString("imagen", "")
        Log.d("URL_IMAGEN", perfilUrl!!)
        if (!perfilUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(perfilUrl)
                .placeholder(R.drawable.magic_tras)
                .into(binding.perfil)
        } else {
            binding.perfil.setImageResource(R.drawable.baseline_supervised_user_circle_24)
        }

        db_ref.child("cartas").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                lista.clear()
                snapshot.children.forEach { hijo: DataSnapshot? ->
                    val carta = hijo?.getValue(Carta::class.java)
                    if (carta != null) {
                        lista.add(carta)
                        Log.d("CARTAS", carta.nombre)
                        Log.d("CARTAS ID", carta.id)
                        Log.d("CARTAS DES", carta.descripcion)
                    }
                }
                adaptador.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }
        })
    }
}
