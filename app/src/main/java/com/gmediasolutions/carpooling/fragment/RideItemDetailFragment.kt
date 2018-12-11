package com.gmediasolutions.carpooling.fragment

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.gmediasolutions.carpooling.MainActivity
import com.gmediasolutions.carpooling.R
import com.gmediasolutions.carpooling.alert.NetworkStateReceiver
import com.gmediasolutions.carpooling.alert.ProgressDialog
import com.gmediasolutions.carpooling.custom.FindRideDialog
import com.gmediasolutions.carpooling.session.UserSession
import kotlinx.android.synthetic.main.fragment_ride_item_details.*

class RideItemDetailFragment : Fragment(), NetworkStateReceiver.NetworkStateReceiverListener{


    var mActivity: MainActivity? = null
    private var item1Layout: CardView? = null
    private var item2Layout: CardView? = null


    private var customFindRide: FindRideDialog? = null

    var spotDialog: ProgressDialog? = null
    var networkStateReceiver: NetworkStateReceiver? = null
    var session: UserSession? = null
    var user_token: String? = null
    var user_id: String? = null

    var btn_back: TextView? = null
    var btn_book: TextView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = activity as MainActivity?
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_ride_item_details, container, false)

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

//        initialize the layout items
        btn_back = view.findViewById(R.id.text_action_bottom1)
        btn_book = view.findViewById(R.id.text_action_bottom2)


        btn_back!!.setOnClickListener(View.OnClickListener {
            val fragmentManager = activity!!.getSupportFragmentManager()
            val fragment = RideListFragment()
            fragmentManager.beginTransaction().replace(R.id.first_container, fragment).commit()
        })
        btn_book!!.setOnClickListener(View.OnClickListener {
            val fragmentManager = activity!!.getSupportFragmentManager()
            val fragment = BookConfirmFragment()
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
