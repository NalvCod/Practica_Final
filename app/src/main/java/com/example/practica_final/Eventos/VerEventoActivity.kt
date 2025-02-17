package com.example.practica_final.Eventos

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.practica_final.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class VerEventoActivity : AppCompatActivity() {

    private lateinit var volver: ImageView
    private lateinit var recycler: RecyclerView
    private lateinit var lista: MutableList<Evento>
    private lateinit var db_ref: DatabaseReference
    private lateinit var adaptador: EventoAdapter
    private lateinit var buscar: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ver_eventos)

        volver = findViewById(R.id.perfil)
        recycler = findViewById(R.id.lista_eventos)
        lista = mutableListOf()
        db_ref = FirebaseDatabase.getInstance().reference
        buscar = findViewById(R.id.buscar_evento)

        var lista_filtrada: MutableList<Evento>

        adaptador = EventoAdapter(lista)
        recycler.adapter = adaptador
        recycler.setHasFixedSize(true)
        recycler.layoutManager = LinearLayoutManager(applicationContext)

        buscar.doOnTextChanged { text, _, _, _ ->
            lista_filtrada = lista.filter { evento ->
                evento.nombre.contains(text.toString(), ignoreCase = true)
            }.toMutableList()
            adaptador = EventoAdapter(lista_filtrada)
            recycler.adapter = adaptador
        }

        db_ref.child("eventos").addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                lista.clear()
                snapshot.children.forEach { hijo: DataSnapshot? ->
                    val pojoEvento = hijo?.getValue(Evento::class.java)
                    if (pojoEvento != null) {
                        lista.add(pojoEvento)
                    }
                }
                recycler.adapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }
        })
    }
}