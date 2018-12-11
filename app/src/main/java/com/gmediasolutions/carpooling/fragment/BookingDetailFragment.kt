package com.gmediasolutions.carpooling.fragment

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import android.widget.Button
import android.widget.Toast
import com.gmediasolutions.carpooling.MainActivity
import com.gmediasolutions.carpooling.R
import com.gmediasolutions.carpooling.alert.NetworkStateReceiver
import com.gmediasolutions.carpooling.alert.ProgressDialog
import com.gmediasolutions.carpooling.session.UserSession

class BookingDetailFragment : Fragment(), NetworkStateReceiver.NetworkStateReceiverListener{


    var mActivity: MainActivity? = null


    var spotDialog: ProgressDialog? = null
    var networkStateReceiver: NetworkStateReceiver? = null
    var session: UserSession? = null
    var user_token: String? = null
    var user_id: String? = null

    var btn_back: Button? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = activity as MainActivity?
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_booking_details, container, false)

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

        btn_back=view.findViewById(R.id.btn_back)

        btn_back!!.setOnClickListener(View.OnClickListener {
            val fragmentManager = activity!!.getSupportFragmentManager()
            val fragment = MyBookingFragment()
            fragmentManager.beginTransaction().replace(R.id.first_container, fragment).commit()
        })


        return view
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
    }

    override fun onDestroy() {
        super.onDestroy()
        networkStateReceiver!!.removeListener(this)
        context!!.unregisterReceiver(networkStateReceiver)
    }

}
