package com.gmediasolutions.carpooling.fragment

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.gmediasolutions.carpooling.MainActivity
import com.gmediasolutions.carpooling.R
import com.gmediasolutions.carpooling.alert.NetworkStateReceiver
import com.gmediasolutions.carpooling.alert.ProgressDialog
import com.gmediasolutions.carpooling.custom.FindRideDialog
import com.gmediasolutions.carpooling.session.UserSession
class MainFragment : Fragment(), NetworkStateReceiver.NetworkStateReceiverListener{


    var mActivity: MainActivity? = null
    private var item1Layout: CardView? = null
    private var item2Layout: CardView? = null


    private var customFindRide: FindRideDialog? = null

    var spotDialog: ProgressDialog? = null
    var networkStateReceiver: NetworkStateReceiver? = null
    var session: UserSession? = null
    var user_token: String? = null
    var user_id: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = activity as MainActivity?
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_main, container, false)

        networkStateReceiver = NetworkStateReceiver()
        networkStateReceiver!!.addListener(this)
        context!!.registerReceiver(networkStateReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

        spotDialog = ProgressDialog(context!!)

        session = UserSession(context!!)
        session!!.checkLogin()

        // get user data from session
        val loginuser: HashMap<String, String> = session!!.userDetails
        user_id = loginuser.get(UserSession.USER_ID)
        user_token = loginuser.get(UserSession.USER_TOKEN)
        iconOption(view)

        return view
    }

    private fun iconOption(view: View) {

//        set findViewById----------------------------------
        item1Layout = view.findViewById(R.id.item1)
        item2Layout = view.findViewById(R.id.item2)

        val animSlideleft = AnimationUtils.loadAnimation(context, R.anim.slide_left)
        val animSlideRight = AnimationUtils.loadAnimation(context, R.anim.slide_right)

        item1Layout!!.startAnimation(animSlideRight)
        item2Layout!!.startAnimation(animSlideleft)

        item1Layout!!.setOnClickListener {
            customFindRide =  FindRideDialog(fragmentManager,context!!)
            customFindRide!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
//            customFindRide!!.window.attributes.windowAnimations = R.style.DialogAnimation
//            customFindRide!!.window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            customFindRide!!.setCanceledOnTouchOutside (false)
            customFindRide!!.show()
         }
         item2Layout!!.setOnClickListener {
             customFindRide = FindRideDialog(fragmentManager,context!!)
             customFindRide!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
             customFindRide!!.setCanceledOnTouchOutside (false)
             customFindRide!!.show()
         }



    }


    /*Checking Internet Connection and Showing Message*/
    private fun showSnack(isConnected: String) {
        val message: String
        if (isConnected.equals("true")) {
        } else {
            message = context!!.getString(R.string.sorry_nointernet)
            if (context != null) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun networkAvailable() {
        showSnack("true")
    }

    override fun networkUnavailable() {
        showSnack("false")
    }

    override fun onResume() {
        super.onResume()
        iconOption(view!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        networkStateReceiver!!.removeListener(this)
        context!!.unregisterReceiver(networkStateReceiver)
    }

}
