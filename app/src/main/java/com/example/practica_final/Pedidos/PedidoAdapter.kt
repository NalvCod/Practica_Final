package com.example.practica_final.Pedidos

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.practica_final.Cartas.Carta
import com.example.practica_final.Usuarios.Usuario
import com.example.practica_final.databinding.ItemPedidoBinding
import com.google.firebase.database.FirebaseDatabase

class PedidoAdapter(
    private val lista_pedidos: MutableList<Pedido>
) : RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder>() {

    private lateinit var database: FirebaseDatabase
    private lateinit var contexto: Context

    inner class PedidoViewHolder(val binding: ItemPedidoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoViewHolder {
        contexto = parent.context
        database = FirebaseDatabase.getInstance()

        val binding = ItemPedidoBinding.inflate(LayoutInflater.from(contexto), parent, false)
        return PedidoViewHolder(binding)
    }

    override fun getItemCount(): Int = lista_pedidos.size

    override fun onBindViewHolder(holder: PedidoViewHolder, position: Int) {
        val pedido = lista_pedidos[position]
        val idUsuarioActual = pedido.id_usuario
        val idCartaActual = pedido.id_carta

        database.reference.child("usuarios").child(idUsuarioActual).get()
            .addOnSuccessListener { snapshot ->
                val usuario = snapshot.getValue(Usuario::class.java)

                if (usuario != null) {
                    holder.binding.apply {
                        nombreUsu.text = usuario.nombre

                    }
                }

        database.reference.child("cartas").child(idCartaActual).get()
            .addOnSuccessListener { snapshot ->
                val carta = snapshot.getValue(Carta::class.java)

                if (carta != null) {
                    holder.binding.apply {
                        // Actualizamos el nombre de la carta
                        tituloCarta.text = carta.nombre

                        // Cargar imagen de la carta si existe
                        if (carta.url_imagen.isNotEmpty()) {
                            Glide.with(contexto).load(carta.url_imagen).into(cartaImg)
                        }
                    }
                }
            }
        holder.binding.estado.text = "POR TRAMITAR"

        holder.binding.aceptar.setOnClickListener {
            Toast.makeText(contexto, "Pedido aceptado", Toast.LENGTH_SHORT).show()
        }

        holder.binding.cancelar.setOnClickListener {
            Toast.makeText(contexto, "Pedido cancelado", Toast.LENGTH_SHORT).show()
        }
    }
}
}
