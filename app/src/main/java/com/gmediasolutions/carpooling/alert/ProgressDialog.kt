package com.gmediasolutions.carpooling.alert


import android.view.WindowManager
import android.view.Gravity
import android.R.attr.gravity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.widget.TextView
import android.graphics.drawable.AnimationDrawable
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.view.Window.FEATURE_NO_TITLE
import android.os.Bundle
import android.view.Window
import com.gmediasolutions.carpooling.R
import kotlinx.android.synthetic.main.dialog_progress.*


class ProgressDialog (context: Context): Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_progress)
        this.setCancelable(false)
        avi.show()
    }
}