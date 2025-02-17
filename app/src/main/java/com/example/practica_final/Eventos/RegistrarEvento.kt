package com.example.practica_final.Eventos

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Person
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.practica_final.R
import com.example.practica_final.Usuarios.Pantalla_Principal
import com.example.practica_final.Util
import com.example.practica_final.databinding.ActivityRegistrarEventoBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.appwrite.Client
import io.appwrite.ID
import io.appwrite.models.InputFile
import io.appwrite.services.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Calendar

class RegistrarEvento : AppCompatActivity() {
    private lateinit var binding: ActivityRegistrarEventoBinding
    private lateinit var database: DatabaseReference
    private lateinit var storage: Storage
    private var url_imagen: Uri? = null
    private lateinit var id_projecto: String
    private lateinit var id_bucket: String

    @SuppressLint("UnsafeIntentLaunch")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        database = FirebaseDatabase.getInstance().reference
        id_projecto = "67a4e7f40018ce9b3ca7"
        id_bucket = "67a4e8c0002f1ef58c66"
        binding = ActivityRegistrarEventoBinding.inflate(layoutInflater)
        val client = Client().setEndpoint("https://cloud.appwrite.io/v1").setProject(id_projecto)
        storage = Storage(client)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.crearEvento.setOnClickListener {
            val nombre = binding.introducirNombre.text.toString()
            val descripcion = binding.descripcionEditText.text.toString()
            val fecha = binding.fechaEditText.text.toString()
            val aforo = binding.aforo.text.toString().toInt()
            val precio = binding.aforo.text.toString().toFloat()

            if (comprobarEvento(nombre, descripcion, fecha)) {
                subirImagenEvento(storage)
                intent = Intent(this, Pantalla_Principal::class.java)
                startActivity(intent)
            }
        }

        binding.fotoEvento.setOnClickListener {
            accesoGaleria.launch("image/*")
        }

        binding.fechaEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    binding.fechaEditText.setText(selectedDate)
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data
            binding.fotoEvento.setImageURI(selectedImageUri)
        }
    }

    fun subirImagenEvento(storage: Storage): String {
        var url = ""
        val id_evento = database.child("eventos").push().key
        GlobalScope.launch(Dispatchers.IO) {
            var mimeType = ""
            var nombreArchivo = ""
            val inputStream = contentResolver.openInputStream(url_imagen!!)
            val aux = contentResolver.query(url_imagen!!, null, null, null, null)
            aux.use {
                if (it!!.moveToFirst()) {
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

            val evento = Evento(
                id = id_evento!!,
                nombre = binding.introducirNombre.text.toString(),
                descripcion = binding.descripcionEditText.text.toString(),
                fecha = binding.fechaEditText.text.toString(),
                aforo = binding.aforo.text.toString().toInt(),
                precio = binding.precio.text.toString().toFloat(),
                participantes = mutableListOf(),
                url_imagen = url
            )

            Util.anadir_evento(database, id_evento!!, evento)

            Util.toastCorrutina(
                this@RegistrarEvento, applicationContext,
                "Evento creado con éxito"
            )
        }
        //finish()
        Log.v("url", url)
        return url
    }

    fun comprobarEvento(nombre: String, descripcion: String, fecha: String): Boolean {
        var todoCorrecto = false
        if (nombre.isNotEmpty() && descripcion.isNotEmpty() && fecha.isNotEmpty()) {
            if (comprobarNombreEvento(nombre)) {
                todoCorrecto = true
            }
        } else {
            Toast.makeText(this, "Por favor, rellene todos los campos", Toast.LENGTH_SHORT).show()
        }
        return todoCorrecto
    }

    fun comprobarNombreEvento(nombre: String): Boolean {
        val listaEventos = obtenerListaEventos(database, this)
        var esCorrecto = false
        if (nombre.length > 4) {
            if (Util.existe_evento(listaEventos, nombre)) {
                Toast.makeText(this, "El evento ya existe", Toast.LENGTH_SHORT).show()
            } else {
                esCorrecto = true
            }
        } else {
            binding.introducirNombre.error = "El nombre del evento debe tener al menos 5 caracteres"
        }
        return esCorrecto
    }

    fun obtenerListaEventos(db_ref: DatabaseReference, contexto: Context): MutableList<Evento> {
        val lista_eventos = mutableListOf<Evento>()

        db_ref.child("eventos").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                lista_eventos.clear() // Limpiamos la lista antes de llenarla nuevamente

                snapshot.children.forEach { eventoSnapshot ->
                    val evento = eventoSnapshot.getValue(Evento::class.java)
                    if (evento != null) {
                        lista_eventos.add(evento)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    contexto,
                    "Error al obtener los eventos: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        return lista_eventos
    }

    private val accesoGaleria = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            url_imagen = uri
            binding.fotoEvento.setImageURI(url_imagen)
        }
    }
}
