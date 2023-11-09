package com.dev.carfinder

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dev.carfinder.databinding.ActivityLoginBinding
import com.github.kittinunf.fuel.httpPost
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class Login : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private var _binding: ActivityLoginBinding? = null

    companion object{
        lateinit var prefs: Prefs
    }

    private val binding: ActivityLoginBinding
        get()= _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs= Prefs(applicationContext)
        setContentView(R.layout.activity_login)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefsSession= Prefs(applicationContext)
        val ID_SESSION= prefsSession.getCon()

        if (ID_SESSION != false ){
            startMainActivity()

        }else{
            binding.btnLogin.setOnClickListener {
                var userData: UserData?=null

                lifecycleScope.launch(Dispatchers.IO) {

                    val resultado= performLogin(binding.txtUsername.text.toString(), binding.txtPassword.text.toString())

                    launch (Dispatchers.Main){
                        if(resultado){


                            saveLoginState()
                            // Inicio de sesión exitoso, guarda el token en la clase UsuarioDatos

                            // Obtener la instancia de UserData después del inicio de sesión

                            prefs.saveID(binding.txtUsername.text.toString())
                            prefsSession.SaveCon(true)
                            startMainActivity()
                        }
                        else{
                            prefsSession.SaveCon(false )
                            mostrarErrorLogin()
                        }
                    }
                }


            }

        }



        sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE)
    }


    private fun obtenerToken(Valor:String): Int {
        // Lógica para obtener el token después de un inicio de sesión exitoso
        return Valor.toInt()
    }



        fun mostrarErrorLogin() {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Información")
            builder.setMessage("El usuario o contraseña no son válidos")
            builder.setPositiveButton("Aceptar") { dialog, which ->
                // Acciones cuando el usuario hace clic en Aceptar
            }

            runOnUiThread {
                builder.show()
            }
        }


suspend fun performLogin(username: String, password: String): Boolean = withContext(Dispatchers.IO) {
    val url = "http://www.desarrollowebumg.somee.com/api/InicioSesion"

    // Datos que enviarás en el cuerpo de la solicitud POST
    val postData = listOf(
        "DPI_Conductor" to username,
        "Contraseña_Conductor" to password
        // Agrega otros parámetros según tu API
    )

    try {
        val (_, _, result) = url.httpPost(postData).responseString()

        return@withContext when (result) {
            is com.github.kittinunf.result.Result.Failure -> {
                // Puedes manejar el error si es necesario
                false
            }

            is com.github.kittinunf.result.Result.Success -> {
                // Puedes procesar la respuesta aquí si es necesario
                true
            }
        }
    } catch (e: Exception) {
        // Manejar excepciones si ocurren durante la solicitud
        return@withContext false
    }
}
    private fun saveLoginState() {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", true)
        editor.apply()
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()  // Para cerrar LoginActivity después de iniciar sesión
    }
}