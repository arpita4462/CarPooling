package com.gmediasolutions.carpooling.base

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Build
import android.support.design.widget.Snackbar
import android.support.v4.content.res.ResourcesCompat
import android.view.Window
import android.widget.TextView
import com.gmediasolutions.carpooling.MainActivity
import com.gmediasolutions.carpooling.R
import com.gmediasolutions.carpooling.alert.CheckPermission
import com.gmediasolutions.carpooling.alert.NetworkStateReceiver
import com.gmediasolutions.carpooling.alert.ProgressDialog
import com.gmediasolutions.carpooling.custom.ForgetPswdDialog
import com.gmediasolutions.carpooling.session.UserSession
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity(), NetworkStateReceiver.NetworkStateReceiverListener {

    private var session: UserSession? = null
    var user_token: String? = null
    var user_id: String? = null

    private var networkStateReceiver: NetworkStateReceiver? = null

    private var pDialog: ProgressDialog? = null

    var user: FirebaseUser? = null
    private var firebase_token: String? = null
    private var email: String? = null
    private var pass: String? = null

    private var intentEmail: String? = null
    private var intentPassword: String? = null

    private var customRestpwd: ForgetPswdDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val typeface = ResourcesCompat.getFont(this, R.font.blacklist)
        appname_tv.setTypeface(typeface)

        //check runtime permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (CheckPermission().ischeckandrequestPermission(this@LoginActivity)) {
            }
        }

        //check Internet Connection
        networkStateReceiver = NetworkStateReceiver()
        networkStateReceiver!!.addListener(this)
        this.registerReceiver(networkStateReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

        //shared prefenced
        session = UserSession(applicationContext)
        val loginuser: HashMap<String, String> = session!!.userDetails
        user_id = loginuser.get(UserSession.USER_ID)
        user_token = loginuser.get(UserSession.USER_TOKEN)

        Log.i("user11", user.toString())

        pDialog = ProgressDialog(this)

        //Intent from register class
        intentEmail = intent.getStringExtra("user_email")
        intentPassword = intent.getStringExtra("user_password")

        if (intentEmail != null && intentPassword != null) {
            email_et.setText(intentEmail)
            password_et.setText(intentPassword!!)
        }
        //check user is loggedin from session
        if (session!!.isLoggedIn) {
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            finish()
        }

        register_now.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            finish()
        }

        forgot_pass.setOnClickListener {
            customRestpwd =  ForgetPswdDialog(this)
            customRestpwd!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
            customRestpwd!!.window.attributes.windowAnimations = R.style.DialogAnimation
            customRestpwd!!.setCanceledOnTouchOutside (false)
            customRestpwd!!.show()
        }


        login_button.setOnClickListener(View.OnClickListener {
            email = email_et.getText().toString()
            pass = password_et.getText().toString()



            if (validateUsername(email!!) && validatePassword(pass!!)) {
//                val userlogindetail = LoginRequest(email!!, pass!!)
//                attemptIntentLogin(userlogindetail)
                attempdemologin(email!!,pass!!)
            }else{

            }
        })


    }

    private fun attempdemologin(email: String, pass: String) {
        if (email=="admin@domain.com" && pass=="admin"){
            session!!.createLoginSession(email,pass)
            //login for testing ,no validation
            val loginSuccess = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(loginSuccess)
            finish()
        }
    }

/*
    private fun attemptIntentLogin(userslogin: LoginRequest) {
        pDialog!!.show()
        val requestBody = HashMap<String, LoginRequest>()
        requestBody.put("data", userslogin)

        val retrofituser = Retrofit.Builder().addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(getString(R.string.base_url))
                .build()
        val apiServiceuser = retrofituser.create(ApiInterface::class.java)
        val postUser = apiServiceuser.login(requestBody)

*/
/*
        postUser.enqueue(object : Callback<ApiReturn> {

            override fun onFailure(call: Call<ApiReturn>, t: Throwable) {
                pDialog!!.dismiss()
                Toast.makeText(applicationContext, "Authentication Error", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<ApiReturn>, response: Response<ApiReturn>) {
                if (response.code() == 401) {
                    pDialog!!.dismiss()
                    session!!.logoutUser()
                    Toast.makeText(this@LoginActivity, "Session Out", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this@LoginActivity, LoginActivity::class.java))
                    finish()
                } else {
                    val loginresponse = response.body()
                    if (loginresponse!!.result != null) {
                        val user_responseid = loginresponse.result.userData.userId
                        val user_responsetoken = loginresponse.result.token
                        if (response.isSuccessful) {
                            spotdialog!!.dismiss()
                            subscribeToPushService(user_responseid)
                            if (loginresponse.response.equals("error")) {
                                spotdialog!!.dismiss()
                                Toast.makeText(applicationContext, "Authentication Error", Toast.LENGTH_LONG).show()
                            } else {
                                spotdialog!!.dismiss()
                                checkIfEmailVerified(user_responseid, user_responsetoken)
                            }
                        } else {
                            spotdialog!!.dismiss()
                            Toast.makeText(applicationContext, "Authentication Error", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        spotdialog!!.dismiss()
                        Toast.makeText(applicationContext, "Authentication Error", Toast.LENGTH_LONG).show()

                    }
                }
            }
        })
*//*


    }
*/

    //subscribe for notification
    private fun subscribeToPushService(user_id: String) {
        FirebaseMessaging.getInstance().subscribeToTopic(user_id)
        firebase_token = FirebaseInstanceId.getInstance().getToken()
    }


    //saved instance for screen rotation
    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState!!.putString("KeyEmail", email_et.text.toString())
        outState.putString("KeyPass", password_et.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        email = savedInstanceState!!.getString("KeyEmail")
        pass = savedInstanceState.getString("KeyPass")
    }


    private fun validatePassword(pass: String): Boolean {
        password_et.requestFocus()
        if (pass.length < 4 || pass.length > 20) {
            password_et.setError("Password Must consist of 4 to 20 characters")
            return false
        }
        return true
    }

    private fun validateUsername(email: String): Boolean {
        if (email.length < 4 || email.length > 30) {
            email_et.setError("Email Must consist of 4 to 30 characters")
            return false
        } else if (!email.matches("^[A-za-z0-9.@]+".toRegex())) {
            email_et.setError("Only . and @ characters allowed")
            return false
        } else if (!email.contains("@") || !email.contains(".")) {
            email_et.setError("Email must contain @ and .")
            return false
        }
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        moveTaskToBack(true)
        finish()

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
        showSnack("false")
    }

    override fun onDestroy() {
        super.onDestroy()
        networkStateReceiver!!.removeListener(this)
        this.unregisterReceiver(networkStateReceiver)
    }

    //runtime permission ask and allow true or false
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            CheckPermission().PERMISSION_CODE -> {
                if (grantResults.last() > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                }
            }
        }
    }
}
