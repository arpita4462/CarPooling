package com.gmediasolutions.carpooling.alert

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.provider.Settings

/**
 * Created by Arpita Patel on 1/5/2018.
 */
class GPSAlert (context: Context){
    private var mcontex: Context?=null
    init {
        this.mcontex=context
    }
    internal  fun showAlert() {

        val dialog = AlertDialog.Builder(mcontex!!)
        dialog.setTitle("Enable Location").setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to use this app")
        dialog.setPositiveButton("Loocation Setting",object: DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {

                var intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                mcontex?.startActivity(intent)
            }

        })
        dialog.setNegativeButton("Cancel") { _dialog: DialogInterface?, which: Int ->  }
        dialog.show()
//        dialog.setCanceledOnTouchOutside(false)
//        dialog.setCancelable(false)
    }
}