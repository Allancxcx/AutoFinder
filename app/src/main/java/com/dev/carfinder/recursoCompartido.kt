package com.dev.carfinder
import android.content.Context
import android.content.SharedPreferences

public object recursoCompartido {
    private const val PREFERENCE_NAME = "MiAppPreferences"
    private const val KEY_ID_CAMION = "ID_Camion"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
    }

    fun saveIDCamion(context: Context, ID: Int) {
        val editor = getSharedPreferences(context).edit()
        editor.putInt(KEY_ID_CAMION, ID)
        editor.apply()
    }

    fun getIDCamion(context: Context): Int {
        return getSharedPreferences(context).getInt(KEY_ID_CAMION, 0)
        // 0 es el valor predeterminado en caso de que no se encuentre la clave
    }
}