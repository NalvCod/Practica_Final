package com.example.practica_final.Cartas

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.practica_final.Util
import com.example.practica_final.databinding.ItemCartaBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class CartaAdapter(private val context: Context, private val listaCarta: MutableList<Carta>) :
    RecyclerView.Adapter<CartaAdapter.CartaViewHolder>() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var contexto: Context
    private lateinit var database: DatabaseReference

    inner class CartaViewHolder(val binding: ItemCartaBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartaViewHolder {
        val binding = ItemCartaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartaViewHolder(binding)
    }

    override fun getItemCount(): Int = listaCarta.size // Retornamos el tamaño de la lista

    override fun onBindViewHolder(holder: CartaViewHolder, position: Int) {
        contexto = this.context
        sharedPreferences = context.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        database = FirebaseDatabase.getInstance().reference
        val carta_actual = listaCarta[position]
        holder.binding.tituloCarta.text = carta_actual.nombre
        holder.binding.precioCarta.text = carta_actual.precio.toString()

        val URL: String? = when (carta_actual.url_imagen) {
            "" -> null // Para que active imagen de fallback
            else -> carta_actual.url_imagen
        }
        Log.d("URL", URL.toString())

        Glide.with(contexto)
            .load(URL)
            .apply(Util.opcionesGlide(contexto))
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

            holder.binding.edit.setOnClickListener{
                val intent = Intent(context, ModificarCartas::class.java)
                intent.putExtra("id_carta", carta_actual.id)
                Log.d("id carta", carta_actual.id)
                context.startActivity(intent)
            }

        // Comprar carta
        holder.binding.comprar.setOnClickListener {
            var dinero_usu = sharedPreferences.getFloat("dinero", 0f)
            if (dinero_usu >= carta_actual.precio) {
                if (carta_actual.unidades > 0) {
                dinero_usu -= carta_actual.precio
                sharedPreferences.edit().putFloat("dinero", dinero_usu).apply()
                Toast.makeText(context, "Carta comprada", Toast.LENGTH_SHORT).show()
            }
            }
        }

        holder.binding.info.setOnClickListener{
            val intent = Intent(context, DetalleCarta::class.java)
            intent.putExtra("id_carta", carta_actual.id)

            Log.d("id carta", carta_actual.id)
            context.startActivity(intent)
        }
    }
}
}

