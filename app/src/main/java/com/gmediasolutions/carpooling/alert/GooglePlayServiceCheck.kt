package com.gmediasolutions.carpooling.alert


import android.support.v7.app.AppCompatActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
/**
 * Created by Arpita Patel on 1/5/2018.
 */
class GooglePlayServiceCheck{
    val PERMISSION_GOOGLEPLAY_CODE=200

    internal fun isGooglePlayInstalled(appCompatActivity: AppCompatActivity):Boolean{
        val apiAvalibility=GoogleApiAvailability.getInstance()
        val status=apiAvalibility.isGooglePlayServicesAvailable(appCompatActivity)
        val dialog=apiAvalibility.getErrorDialog(appCompatActivity,status,PERMISSION_GOOGLEPLAY_CODE)

        if (status==ConnectionResult.SUCCESS){
            return true
        }else{
            dialog.show()
            return false
        }

    }
}