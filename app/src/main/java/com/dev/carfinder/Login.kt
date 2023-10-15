package com.dev.carfinder

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dev.carfinder.databinding.ActivityLoginBinding
import com.dev.carfinder.databinding.ActivityMainBinding


class Login : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private var _binding: ActivityLoginBinding? = null

    private val binding: ActivityLoginBinding
        get()= _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnLogin.setOnClickListener {


            if (performLogin(binding.txtUsername.text.toString(), binding.txtPassword.text.toString())) {
                saveLoginState()
                startMainActivity()
            }
        }



        sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE)
    }


    private fun performLogin(username: String, password: String): Boolean {
        // Aquí realizarías la verificación de credenciales
        return true
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