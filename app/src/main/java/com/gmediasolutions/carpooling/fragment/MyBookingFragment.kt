package com.gmediasolutions.carpooling.fragment

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.gmediasolutions.carpooling.MainActivity
import com.gmediasolutions.carpooling.R
import com.gmediasolutions.carpooling.adapter.MyBookingRVAdapter
import com.gmediasolutions.carpooling.alert.NetworkStateReceiver
import com.gmediasolutions.carpooling.alert.ProgressDialog
import com.gmediasolutions.carpooling.custom.FindRideDialog
import com.gmediasolutions.carpooling.session.UserSession
import com.willowtreeapps.spruce.Spruce
import com.willowtreeapps.spruce.animation.DefaultAnimations
import com.willowtreeapps.spruce.sort.DefaultSort

class MyBookingFragment : Fragment(), NetworkStateReceiver.NetworkStateReceiverListener,
    MyBookingRVAdapter.ItemClickListener {


    var mActivity: MainActivity? = null

    var recyclerView: RecyclerView?=null
    var adapter: MyBookingRVAdapter? = null
    private var spruceAnimator: Animator? = null

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

        val view = inflater.inflate(R.layout.fragment_my_booking, container, false)

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

        recyclerView = view.findViewById(R.id.mybookig_recyclerview)

        setupRecycleView()

        return view
    }


    private fun setupRecycleView() {
        // data to populate the RecyclerView with
        val data = arrayOf(
            "On Going",
            "Completed",
            "Cancelled",
            "Completed",
            "Cancelled",
            "Completed",
            "Completed",
            "Cancelled",
            "Cancelled",
            "Cancelled",
            "Completed",
            "Cancelled",
            "Completed",
            "Completed",
            "Cancelled",
            "Cancelled")

        val linearLayoutManager = object : LinearLayoutManager(context) {
            override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State) {
                super.onLayoutChildren(recycler, state)
                initSpruce()
            }
        }
        recyclerView!!.layoutManager=linearLayoutManager
        adapter = MyBookingRVAdapter(context!!, data)
        adapter!!.setClickListener(this)
        recyclerView!!.setAdapter(adapter)


    }
    @SuppressLint("ObjectAnimatorBinding")
    private fun initSpruce() {
        spruceAnimator = Spruce.SpruceBuilder(recyclerView)
            .sortWith(DefaultSort(100))
            .animateWith(
                DefaultAnimations.shrinkAnimator(recyclerView, 800),
                ObjectAnimator.ofFloat(recyclerView, "translationX", -recyclerView!!.getWidth().toFloat(), 0f).setDuration(800)
            )
            .start()
    }

    override fun onItemClick(view: View, position: Int) {
        val fragmentManager = activity!!.getSupportFragmentManager()
        val fragment = BookingDetailFragment()
        fragmentManager.beginTransaction().replace(R.id.first_container, fragment).commit()
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
