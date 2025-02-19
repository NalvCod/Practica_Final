package com.example.practica_final.Usuarios

import android.annotation.SuppressLint
import android.app.Activity
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
import com.example.practica_final.Util
import com.example.practica_final.databinding.ActivityRegistroClienteBinding
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

class Registro_Usuario : AppCompatActivity() {
    private lateinit var binding: ActivityRegistroClienteBinding
    private lateinit var database: DatabaseReference
    private lateinit var storage : Storage
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
        binding = ActivityRegistroClienteBinding.inflate(layoutInflater)
        val client = Client().setEndpoint("https://cloud.appwrite.io/v1").setProject(id_projecto)
        storage = Storage(client)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.registrarUsuario.setOnClickListener {
            val nombre = binding.introducirNombre.text.toString()
            val contrasena = binding.introducirContrasena.text.toString()
            val contrasena2 = binding.repetirContrasena.text.toString()
            val email = binding.introducirCorreo.text.toString()

            if (comprobarUsuario(nombre, contrasena, contrasena2, email)){
                subir_usuario(storage)
                intent = Intent(this, PantallaPrincipalActivity::class.java)
                startActivity(intent)
            }
        }

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

    fun subir_usuario(storage: Storage): String {
        var url = ""
        val id_usuario = database.child("clientes").push().key
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


            val usuario = Usuario(id_usuario,
                binding.introducirNombre.text.toString(),
                binding.introducirContrasena.text.toString(),
                binding.introducirCorreo.text.toString(),
                false,
                url,
                binding.introducirDinero.text.toString().toFloat()
            )

            Util.anadir_usuario(database, id_usuario!!, usuario)

            Util.toastCorrutina(
                this@Registro_Usuario, applicationContext,
                "Imagen descargada con éxito"
            )
        }
        //finish()
        Log.v("url", url)
        return url
    }

    fun comprobarUsuario(nombre: String, contrasena: String, contrasena2: String, email: String):Boolean {
        var todoCorrecto = false
        if (nombre.isNotEmpty() && contrasena.isNotEmpty()) {
            //Si todos los datos son correctos, creo el objeto
            if (comprobarNombre(nombre) && comprobarContrasena(contrasena, contrasena2) && comprobarCorreo(email)) {
                todoCorrecto = true
            }
        } else {
            Toast.makeText(this, "Por favor, rellene todos los campos", Toast.LENGTH_SHORT).show()
        }
        return todoCorrecto
    }

    fun comprobarNombre(nombre: String): Boolean {
        val listaUsuarios = obtenerListaUsuarios(database, this)
        var esCorrecto = false
        if (nombre.length > 4) {
            if (Util.existeUsuario(listaUsuarios, nombre)) {
                Toast.makeText(this, "El usuario ya existe", Toast.LENGTH_SHORT).show()
            }

            esCorrecto = true
        } else {
            binding.introducirNombre.error = "El nombre de usuario debe tener al menos 5 caracteres"
        }
        return esCorrecto
    }

    fun comprobarCorreo(email: String): Boolean {
        val listaUsuarios = obtenerListaUsuarios(database, this)  // Suponiendo que ya tienes esta función
        var esCorrecto = false

        if (email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // Verificar si el correo ya existe en la lista de usuarios
            if (Util.existeUsuario(listaUsuarios, email)) {
                Toast.makeText(this, "El correo electrónico ya está registrado", Toast.LENGTH_SHORT).show()
            } else {
                esCorrecto = true
            }
        } else {
            binding.introducirCorreo.error = "Por favor, introduce un correo electrónico válido"
        }

        return esCorrecto
    }

    // Función para obtener la lista de usuarios desde Firebase
    fun obtenerListaUsuarios(db_ref: DatabaseReference, contexto: Context): MutableList<Usuario> {
        val lista_usuarios = mutableListOf<Usuario>()

        db_ref.child("usuarios").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                lista_usuarios.clear() // Limpiar la lista antes de agregar los nuevos usuarios

                snapshot.children.forEach { usuarioSnapshot ->
                    val usuario = usuarioSnapshot.getValue(Usuario::class.java)
                    if (usuario != null) {
                        lista_usuarios.add(usuario)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    contexto,
                    "Error al obtener los usuarios: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        return lista_usuarios
    }

    fun comprobarContrasena(contrasena: String, contrasena2: String): Boolean {
        if (contrasena != contrasena2) {
            binding.repetirContrasena.error = "Las contraseñas no coinciden"
            return false
        } else {
            //comprobar que la contraseña tenga mas de 8 caracteres y una mayuscula
            if (contrasena.length > 8 && contrasena.any { it.isUpperCase() }) {
                return true
            } else {
                binding.introducirContrasena.error = "La contraseña debe tener al menos 8 caracteres y una mayúscula"
                binding.repetirContrasena.error = "La contraseña debe tener al menos 8 caracteres y una mayúscula"
                return false
            }
        }
    }

    // Actualiza la foto de perfil con la imagen seleccionada desde la galería
    private val accesoGaleria = registerForActivityResult(ActivityResultContracts.GetContent())
    { uri: Uri? ->
        if (uri != null) {
            url_imagen = uri
            binding.fotoPerfil.setImageURI(url_imagen)
        }
    }
}
