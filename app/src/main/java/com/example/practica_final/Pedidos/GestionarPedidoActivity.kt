package com.example.practica_final.Pedidos

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.practica_final.Eventos.Evento
import com.example.practica_final.Eventos.EventoAdapter
import com.example.practica_final.R
import com.example.practica_final.databinding.ActivityGestionarPedidoBinding
import com.google.firebase.database.FirebaseDatabase

class GestionarPedidoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGestionarPedidoBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var lista_pedidos: MutableList<Pedido>
    private lateinit var adapter: PedidoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicialización de binding
        binding = ActivityGestionarPedidoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicialización de Firebase
        database = FirebaseDatabase.getInstance()
        lista_pedidos = mutableListOf()  // Inicializa la lista antes de usarla

        // Inicialización del adapter después de haber cargado los pedidos
        adapter = PedidoAdapter(lista_pedidos)
        binding.listaEventos.adapter = adapter
        binding.listaEventos.layoutManager = LinearLayoutManager(this)

        // Habilitar Edge-to-Edge (opcional)
        enableEdgeToEdge()

        // Obtener pedidos desde la base de datos
        database.reference.child("pedidos").get()
            .addOnSuccessListener { snapshot ->
                // Limpiar lista antes de agregar los nuevos datos
                lista_pedidos.clear()

                for (childSnapshot in snapshot.children) {
                    val pedido = childSnapshot.getValue(Pedido::class.java)
                    if (pedido != null) {
                        lista_pedidos.add(pedido)
                    }
                }

                // Notificar al adaptador que los datos han cambiado
                adapter.notifyDataSetChanged()
            }

        // Configurar el sistema de barras
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
