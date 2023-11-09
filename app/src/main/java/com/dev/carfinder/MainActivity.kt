package com.dev.carfinder

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.dev.carfinder.databinding.ActivityMainBinding
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe



class MainActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private var _binding: ActivityMainBinding? = null
    companion object{
        lateinit var prefs: Prefs
    }

    private val binding: ActivityMainBinding
        get()= _binding!!

    private var service: Intent?=null

    private val backgroundLocation =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {

            }
        }

    private val locationPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            when {
                it.getOrDefault(android.Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        if (ActivityCompat.checkSelfPermission(
                                this,
                                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            backgroundLocation.launch(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        }
                    }

                }
                it.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION, false) -> {

                }
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        service = Intent(this,ServiceLocacion::class.java)
        sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE)

        binding.btnLogout.setOnClickListener {
            logout()
        }

        binding.apply {
            btnInicio.setOnClickListener {
                checkPermissions()
            }

            btnDetener.setOnClickListener {
                stopService(service)
            }
        }
    }

    private fun logout() {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", false)
        editor.apply()
        val prefsSession= Prefs(applicationContext)
        prefsSession.SaveCon(false)


        val intent = Intent(this, Login::class.java)
        startActivity(intent)
        finish()
    }
        override fun onStart() {
            super.onStart()
            if(!EventBus.getDefault().isRegistered(this)){
                EventBus.getDefault().register(this)
            }
        }
        fun checkPermissions() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    locationPermissions.launch(
                        arrayOf(
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }else{
                    startService(service)
                }
            }
        }
        @Subscribe
        fun receiveLocationEvent(dataLocalizador: dataLocalizador){
            binding.vlLatitud.text = " ${dataLocalizador.latitud}"
            binding.vlLongitud.text =" ${dataLocalizador.longitud}"



        }



}