package com.example.practica_final.Cartas

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.practica_final.R
import com.example.practica_final.Util
import com.example.practica_final.databinding.ActivityItemCartaBinding
import com.google.firebase.database.FirebaseDatabase


class CartaAdapter(private val context: Context, private val listaCarta: MutableList<Carta>): RecyclerView.Adapter<CartaAdapter.CartaViewHolder>() {
    private lateinit var lista_filtrada: MutableList<Carta>
    private lateinit var contexto: Context

    inner class  CartaViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        lateinit var binding: ActivityItemCartaBinding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartaViewHolder {
        val vista_item = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_carta, parent, false)
        contexto = parent.context
        return CartaViewHolder(vista_item)
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: CartaViewHolder, position: Int) {
        val carta_actual = listaCarta[position]
        holder.binding.tituloCarta.text = carta_actual.nombre
        holder.binding.imagenCarta.setImageResource(carta_actual.url_imagen.toInt())

        //Aplico la imagen y animacion. Manejo si hay algún error
        Glide.with(contexto)
            .load(carta_actual.url_imagen)
            .apply(Util.opcionesGlide(contexto))
            .transition(Util.transicion)
            .into(holder.binding.imagenCarta)

        holder.binding.edit.setOnClickListener{
            val intent = Intent(contexto, ModificarCartas::class.java)
            contexto.startActivity(intent)
        }
        holder.binding.delete.setOnClickListener{
            val db_ref = FirebaseDatabase.getInstance().reference
            db_ref.child("cartas").child(carta_actual.url_imagen).removeValue()
            listaCarta.removeAt(position)
            notifyItemRemoved(position)
        }
        holder.binding.comprar.setOnClickListener{
            //quitar unidad de carta, añadir a usuario
        }
    }

}

