package com.dev.carfinder

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.github.kittinunf.fuel.httpPost
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import org.greenrobot.eventbus.EventBus

class ServiceLocacion : Service() {

    //Nombre del objeto a nivel de objeto del servicio
    companion object {
        const val CHANNEL_ID = "12345"
        const val NOTIFICATION_ID=12345
            lateinit var prefs: Prefs
    }

    //Llamo al constructor fusedLocationProviderClient para poder utilizar los metodos correspondientes y obtener la latitud y longitud
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null
    private var locationRequest: LocationRequest? = null

    private var notificationManager: NotificationManager? = null

    private var location: Location?=null
   private var userData: UserData?=null
    override fun onCreate() {
        super.onCreate()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        //Se coloca cada cuando se actualiza la ubicacion
        locationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 30000).setIntervalMillis(30000)
                .build()
        locationCallback = object : LocationCallback() {
            override fun onLocationAvailability(p0: LocationAvailability) {
                super.onLocationAvailability(p0)
            }

            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                onNewLocation(locationResult)
            }
        }
        notificationManager = this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(CHANNEL_ID, "locacion", NotificationManager.IMPORTANCE_HIGH)
            notificationManager?.createNotificationChannel(notificationChannel)
        }
    }


    //Creo el callback que me retornara la ubicacion necesaria
    @Suppress("MissingPermission")
    fun createLocationRequest(){
        try {
            fusedLocationProviderClient?.requestLocationUpdates(
                locationRequest!!,locationCallback!!,null
            )
        }catch (e:Exception){
            e.printStackTrace()
        }

    }


fun envioApi(Coordenadas:String,ID:String)
{

    val url = "http://www.desarrollowebumg.somee.com/api/Ubicacion"

    // Datos que enviarás en el cuerpo de la solicitud POST
    val postData = listOf(
        "Coordenadas_Geograficas" to Coordenadas,
        "ID_Camion" to  ID
        // Agrega otros parámetros según tu API
    )

    url.httpPost(postData).responseString { _, _, result ->
        when (result) {
            is com.github.kittinunf.result.Result.Failure -> {
                val ex = result.getException()

                println("envioApi: {$ex}")
            }
            is com.github.kittinunf.result.Result.Success-> {
//
            }

            else -> {

            }
        }
    }




}
    //me sirve para desactivar la actualizacion automatica
    private fun removeLocationUpdates(){
        locationCallback?.let {
            fusedLocationProviderClient?.removeLocationUpdates(it)
        }
        stopForeground(true)
        stopSelf()
    }

    //actualizo la nueva ubicacion para siempre tener el tracking del vehiculo
    private fun onNewLocation(locationResult: LocationResult) {
        location = locationResult.lastLocation
        EventBus.getDefault().post(dataLocalizador(
            latitud = location?.latitude,
            longitud = location?.longitude,


            ))

        val prefs1= Prefs(applicationContext)
        val ID_CONDUCTOR= prefs1.getID()

        envioApi(  "${location?.latitude}, ${location?.longitude}",ID_CONDUCTOR.toString())
        startForeground(NOTIFICATION_ID,getNotification())
    }

    //Envio la notificacion al dispositivo que estoy guardando la ubicacion desde aqui enviare datos a la API
    fun getNotification(): Notification {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Actualizacion")
            .setContentText(
                "Latitude--> ${location?.latitude}\nLongitude --> ${location?.longitude}"
            )
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            notification.setChannelId(CHANNEL_ID)
        }
        return notification.build()
    }


    //Inicializo el objeto
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        createLocationRequest()
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? = null

    //limpio el servicio
    override fun onDestroy() {
        super.onDestroy()
        removeLocationUpdates()
    }


}