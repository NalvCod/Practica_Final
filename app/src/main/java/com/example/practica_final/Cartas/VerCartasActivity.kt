package com.example.practica_final.Cartas

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.practica_final.R
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
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ver_cartas)

        // Configuración de SharedPreferences
        sharedPreferences = getSharedPreferences("CartasPreferences", Context.MODE_PRIVATE)

        // Referencias a vistas
        recycler = findViewById(R.id.lista_series)

        // Lista de cartas y base de datos de Firebase
        lista = mutableListOf()
        db_ref = FirebaseDatabase.getInstance().reference

        // Inicializamos el adaptador de la lista
        adaptador = CartaAdapter(this, lista)
        recycler.adapter = adaptador
        recycler.layoutManager = GridLayoutManager(this, 2) // Usamos GridLayout para mostrar las cartas en una cuadrícula
        recycler.setHasFixedSize(true)

        // Obtención de datos de Firebase
        db_ref.child("cartas").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                lista.clear()
                snapshot.children.forEach { hijo: DataSnapshot? ->
                    val carta = hijo?.getValue(Carta::class.java)
                    if (carta != null) {
                        lista.add(carta)
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
