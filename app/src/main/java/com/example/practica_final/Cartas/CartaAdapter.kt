package com.example.practica_final.Cartas

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.practica_final.Util
import com.example.practica_final.databinding.ActivityItemCartaBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class CartaAdapter(private val context: Context, private val listaCarta: MutableList<Carta>) :
    RecyclerView.Adapter<CartaAdapter.CartaViewHolder>() {

    private lateinit var lista_filtrada: MutableList<Carta>
    private lateinit var db_ref: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences

    inner class CartaViewHolder(val binding: ActivityItemCartaBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartaViewHolder {
        val binding = ActivityItemCartaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartaViewHolder(binding)
    }

    override fun getItemCount(): Int = listaCarta.size // Retornamos el tamaño de la lista

    override fun onBindViewHolder(holder: CartaViewHolder, position: Int) {
        sharedPreferences = context.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val carta_actual = listaCarta[position]
        holder.binding.tituloCarta.text = carta_actual.nombre

        Glide.with(context)
            .load(carta_actual.url_imagen)
            .apply(Util.opcionesGlide(context))
            .transition(Util.transicion)
            .into(holder.binding.imagenCarta)

        if (sharedPreferences.getBoolean("esAdmin", false)) {
            holder.binding.edit.visibility = View.GONE
            holder.binding.delete.visibility = View.GONE
        holder.binding.edit.setOnClickListener {
            val intent = Intent(context, ModificarCartas::class.java)
            intent.putExtra("id_carta", carta_actual.id) // Pasamos el id de la carta si es necesario
            context.startActivity(intent)
        }

        // Eliminar carta
        holder.binding.delete.setOnClickListener {
            db_ref = FirebaseDatabase.getInstance().reference
            db_ref.child("cartas").child(carta_actual.id).removeValue() // Usamos el ID único de la carta
            listaCarta.removeAt(position)
            notifyItemRemoved(position)
        }

        // Comprar carta
        holder.binding.comprar.setOnClickListener {
            // Aquí debes implementar la lógica para quitar unidad de carta y añadirla al usuario
            // Esto depende de cómo estás gestionando las unidades y usuarios en tu base de datos
        }

        holder.binding.info.setOnClickListener{
            val intent = Intent(context, DetalleCarta::class.java)
            intent.putExtra("id_carta", carta_actual.id)
            context.startActivity(intent)
        }
    }
}
}

