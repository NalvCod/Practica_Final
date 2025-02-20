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
import com.example.practica_final.Pedidos.Pedido
import com.example.practica_final.Usuarios.Usuario
import com.example.practica_final.Util
import com.example.practica_final.databinding.ItemCartaBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class CartaAdapter(private val context: Context, private val listaCarta: MutableList<Carta>) :
    RecyclerView.Adapter<CartaAdapter.CartaViewHolder>() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var database: DatabaseReference

    init {
        sharedPreferences = context.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        database = FirebaseDatabase.getInstance().reference
    }

    inner class CartaViewHolder(val binding: ItemCartaBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartaViewHolder {
        val binding = ItemCartaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartaViewHolder(binding)
    }

    override fun getItemCount(): Int = listaCarta.size // Retornamos el tamaño de la lista

    override fun onBindViewHolder(holder: CartaViewHolder, position: Int) {
        val carta_actual = listaCarta[position]
        holder.binding.tituloCarta.text = carta_actual.nombre
        holder.binding.precioCarta.text = carta_actual.precio.toString()

        // Manejo de la URL para la imagen de la carta
        val URL = carta_actual.url_imagen.takeIf { it.isNotEmpty() } ?: "url_de_fallback"
        Log.d("URL", URL.toString())

        Glide.with(context)
            .load(URL)
            .apply(Util.opcionesGlide(context))
            .transition(Util.transicion)
            .into(holder.binding.imagenCarta)

        // Manejo de la visibilidad de los botones para Admin
        if (sharedPreferences.getBoolean("esAdmin", false)) {
            holder.binding.edit.visibility = View.VISIBLE
            holder.binding.delete.visibility = View.VISIBLE
        } else {
            holder.binding.edit.visibility = View.GONE
            holder.binding.delete.visibility = View.GONE
        }

        // Acción de editar la carta
        holder.binding.edit.setOnClickListener {
            val intent = Intent(context, ModificarCartas::class.java)
            intent.putExtra("id_carta", carta_actual.id)
            Log.d("id carta", carta_actual.id)
            context.startActivity(intent)
        }

        // Acción de comprar la carta
        holder.binding.comprar.setOnClickListener {
            val id_usu = sharedPreferences.getString("id", "")
            val nombre_usu = sharedPreferences.getString("username", "")
            val email = sharedPreferences.getString("email", "")
            val contrasena = sharedPreferences.getString("password", "")
            val url = sharedPreferences.getString("imagen", "")
            val esAdmin = sharedPreferences.getBoolean("esAdmin", false)
            var dinero_usu = sharedPreferences.getFloat("dinero", 0f)
            val usuario_loggeado = Usuario(id_usu, nombre_usu, contrasena, email, esAdmin, url!!, dinero_usu)

            if (dinero_usu >= carta_actual.precio) {
                if (carta_actual.unidades > 0) {
                    // Actualizar unidades de la carta
                    carta_actual.unidades -= 1
                    Util.actualizar_carta(database, carta_actual.id, carta_actual)

                    // Actualizar dinero del usuario en SharedPreferences
                    dinero_usu -= carta_actual.precio
                    sharedPreferences.edit().putFloat("dinero", dinero_usu).apply()

                    // Actualizar la información del usuario
                    Util.anadir_usuario(database, id_usu.toString(), usuario_loggeado)

                    // Crear el pedido con la ID generada automáticamente por Firebase
                    val pedido = Pedido(id_usu!!, usuario_loggeado.id!!, carta_actual.id)

                    // Añadir el pedido a la base de datos con la ID generada
                    Util.anadir_pedido(database, pedido)

                    Toast.makeText(context, "Carta comprada", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "No hay unidades disponibles", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "No tienes suficiente dinero", Toast.LENGTH_SHORT).show()
            }
        }


        // Acción para ver más detalles de la carta
        holder.binding.info.setOnClickListener {
            val intent = Intent(context, DetalleCarta::class.java)
            intent.putExtra("id_carta", carta_actual.id)
            Log.d("id carta", carta_actual.id)
            context.startActivity(intent)
        }
    }
}

