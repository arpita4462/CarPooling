package com.gmediasolutions.carpooling

import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.gmediasolutions.carpooling.alert.CheckPermission
import com.gmediasolutions.carpooling.alert.NetworkStateReceiver
import com.gmediasolutions.carpooling.alert.ProgressDialog
import com.gmediasolutions.carpooling.base.LoginActivity
import com.gmediasolutions.carpooling.session.UserSession
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

open class BaseActivity : AppCompatActivity(), NetworkStateReceiver.NetworkStateReceiverListener {

     var networkStateReceiver: NetworkStateReceiver? = null
     var session: UserSession? = null
     var user_token: String? = null
     var user_id: String? = null
     var spotDialog: ProgressDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //check internet connections
        networkStateReceiver = NetworkStateReceiver()
        networkStateReceiver!!.addListener(this)
        this.registerReceiver(networkStateReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

        //check android runtime permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (CheckPermission().ischeckandrequestPermission(this@BaseActivity)) {
            }
        }
        //display loading dialog
        spotDialog = ProgressDialog(this)

        //call sessionManagment
        session = UserSession(getApplicationContext())
        session!!.checkLogin()

        // get user data from session
        val loginuser: HashMap<String, String> = session!!.userDetails
        user_id = loginuser.get(UserSession.USER_ID)
        user_token = loginuser.get(UserSession.USER_TOKEN)

    }

    private fun logoutUser() {
        session!!.logoutUser()
        startActivity(Intent(this@BaseActivity, LoginActivity::class.java))
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        finish()

    }


    // method to check runtime permission above version Android 6
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            CheckPermission().PERMISSION_CODE -> {
                if (grantResults.last() > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                }
            }
        }
    }

    /*Checking Internet Connection and Showing Message*/
    private fun showSnack(isConnected: String) {
        val message: String
        val color: Int
        if (isConnected.equals("true")) {

        } else {
            message = getString(R.string.sorry_nointernet)
            color = Color.RED
            val snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
            val sbView = snackbar.view
            val textView = sbView.findViewById<View>(android.support.design.R.id.snackbar_text) as TextView
            textView.setTextColor(color)
            snackbar.show()
        }


    }

    override fun networkAvailable() {
        showSnack("true")
    }

    override fun networkUnavailable() {
        Toast.makeText(this, "No Internet Connection", Toast.LENGTH_LONG).show()
        showSnack("false")
    }

    override fun onDestroy() {
        super.onDestroy()
        networkStateReceiver!!.removeListener(this)
        this.unregisterReceiver(networkStateReceiver)
    }

    //save instance onScreenOrientaion
    override fun onSaveInstanceState(outState: Bundle?) {
        outState!!.putString("KeyUserId", user_id)
        outState.putString("KeyUserToken", user_token)
        super.onSaveInstanceState(outState)
    }

    //restore instance onScreenOrientation
    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        user_id = savedInstanceState!!.getString("KeyUserId")
        user_token = savedInstanceState.getString("KeyUserToken")

    }
}
