package com.example.practica_final.Cartas

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.practica_final.R
import com.example.practica_final.Usuarios.Pantalla_Principal
import com.example.practica_final.databinding.ActivityRegistrarCartasBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import io.appwrite.Client
import io.appwrite.ID
import io.appwrite.models.InputFile
import io.appwrite.services.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Registrar_Cartas : AppCompatActivity() {
    private lateinit var binding: ActivityRegistrarCartasBinding
    private lateinit var database: DatabaseReference
    private lateinit var storage: Storage
    private var url_imagen: Uri? = null
    private lateinit var id_projecto: String
    private lateinit var id_bucket: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        database = FirebaseDatabase.getInstance().reference
        id_projecto = "67a4e7f40018ce9b3ca7"
        id_bucket = "67a4e8c0002f1ef58c66"
        binding = ActivityRegistrarCartasBinding.inflate(layoutInflater)
        val client = Client().setEndpoint("https://cloud.appwrite.io/v1").setProject(id_projecto)
        storage = io.appwrite.services.Storage(client)
        setContentView(binding.root)
        val colores = listOf("Blanco (W)", "Azul (U)", "Negro (B)", "Rojo (R)", "Verde (G)")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, colores)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.colorSpinner.adapter = adapter
        binding.colorSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                val colorSeleccionado = colores[position]
                // Realiza la acción que desees con el color seleccionado
                Toast.makeText(this@Registrar_Cartas, "Color seleccionado: $colorSeleccionado", Toast.LENGTH_SHORT).show()

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        // Configurar las ventanas del sistema (gestión de márgenes)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Acción al pulsar en el botón de registrarse
        binding.anadirCarta.setOnClickListener {
            val nombre = binding.introducirNombre.text.toString()
            val color = binding.colorSpinner.selectedItem.toString()
            val descripcion = binding.introducirDescripcion.text.toString()
            val unidades = binding.introducirUnidades.text.toString().toIntOrNull() ?: 0

            if (comprobarDatos(nombre, color, descripcion, unidades)) {
                subirCarta(storage, nombre, color, descripcion, unidades)
                val intent = Intent(this, Pantalla_Principal::class.java)
                startActivity(intent)
            }
        }

        // Acción al hacer clic en la imagen de perfil
        binding.fotoPerfil.setOnClickListener {
            accesoGaleria.launch("image/*")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data
            binding.fotoPerfil.setImageURI(selectedImageUri)
        }
    }

    fun subirCarta(storage: Storage, nombre: String, color: String, descripcion: String, unidades: Int) {
        var url = ""
        val id_carta = database.child("cartas").push().key
        GlobalScope.launch(Dispatchers.IO) {
            var mimeType = ""
            var nombreArchivo = ""
            val inputStream = contentResolver.openInputStream(url_imagen!!)
            val aux = contentResolver.query(url_imagen!!, null, null, null, null)
            aux.use {
                if (it!!.moveToFirst()) {
                    // Obtener el nombre del archivo
                    val nombreIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nombreIndex != -1) {
                        nombreArchivo = it.getString(nombreIndex)
                    }
                }
            }
            mimeType = contentResolver.getType(url_imagen!!).toString()

            val fileInput = InputFile.fromBytes(
                bytes = inputStream?.readBytes() ?: byteArrayOf(),
                filename = nombreArchivo,
                mimeType = mimeType
            )

            val identificadorFile = ID.unique()
            val file = storage.createFile(
                bucketId = id_bucket,
                fileId = identificadorFile,
                file = fileInput,
            )

            url = "https://cloud.appwrite.io/v1/storage/buckets/$id_bucket/files/$identificadorFile/preview?project=$id_projecto&output=jpg"

            val carta = Carta(
                nombre = nombre,
                color = color,
                descripcion = descripcion,
                unidades = unidades,
                url_imagen = url
            )

            // Guardar la carta en la base de datos
            com.example.practica_final.Util.anadir_carta(database, id_carta!!, carta)

            // Mostrar un mensaje de éxito
            com.example.practica_final.Util.toastCorrutina(
                this@Registrar_Cartas, applicationContext,
                "Carta registrada con éxito"
            )
        }
        Log.v("url", url)
    }

    fun comprobarDatos(nombre: String, color: String, descripcion: String, unidades: Int): Boolean {
        var todoCorrecto = true
        if (nombre.isEmpty()) {
            binding.introducirNombre.error = "El nombre es obligatorio"
            todoCorrecto = false
        }
        if (descripcion.isEmpty()) {
            binding.introducirDescripcion.error = "La descripción es obligatoria"
            todoCorrecto = false
        }
        if (unidades <= 0) {
            binding.introducirUnidades.error = "Debe ser mayor que 0"
            todoCorrecto = false
        }
        if (color == "Selecciona un color") {
            Toast.makeText(this, "Selecciona un color válido", Toast.LENGTH_SHORT).show()
            todoCorrecto = false
        }
        return todoCorrecto
    }

    private fun configurarSpinnerColores() {
        val colores = listOf("Rojo", "Verde", "Azul", "Negro", "Blanco")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, colores)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.colorSpinner.adapter = adapter
    }

    // Acción de selección de imagen desde la galería
    private val accesoGaleria = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            url_imagen = uri
            binding.fotoPerfil.setImageURI(url_imagen)
        }
    }
}