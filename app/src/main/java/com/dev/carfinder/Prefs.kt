package com.dev.carfinder

import android.content.Context

class Prefs(val context: Context){
    val SHARED_ID ="Prueba"
    val SHARED_USERNAME="Prueba1"

    val SHARED_SESSION ="Session"
    val SHARED_SESSION_CON="Session1"

    val storage = context.getSharedPreferences(SHARED_ID,0)
    val storage1= context.getSharedPreferences(SHARED_SESSION,0)
    fun saveID(ID:String){
        storage.edit().putString(SHARED_USERNAME,ID).apply()
    }
    fun getID():String{
        return storage.getString(SHARED_USERNAME,"")!!
    }

    fun SaveCon(Session:Boolean){
        storage1.edit().putBoolean(SHARED_SESSION_CON,Session).apply()
    }

    fun getCon():Boolean{
        return  storage1.getBoolean(SHARED_SESSION_CON,false)!!
    }



}