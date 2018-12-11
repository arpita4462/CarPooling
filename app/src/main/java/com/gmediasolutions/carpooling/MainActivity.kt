package com.gmediasolutions.carpooling

import android.widget.Toast
import android.support.v4.view.GravityCompat
import android.content.Intent
import android.support.design.widget.NavigationView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.Glide
import android.support.design.widget.Snackbar
import android.widget.TextView
import android.support.design.widget.FloatingActionButton
import android.support.v4.widget.DrawerLayout
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import com.gmediasolutions.carpooling.base.LoginActivity
import com.gmediasolutions.carpooling.fragment.MainFragment
import com.gmediasolutions.carpooling.fragment.MyBookingFragment
import com.gmediasolutions.carpooling.fragment.ProfileFragment
import com.gmediasolutions.carpooling.fragment.RideListFragment


class MainActivity : BaseActivity() {

    private var navigationView: NavigationView? = null
    private var drawer: DrawerLayout? = null
    private var navHeader: View? = null
    private var imgNavHeaderBg: ImageView? = null
    private var imgProfile: ImageView? = null
    private var txtName: TextView? = null
    private var txtWebsite: TextView? = null
    private var toolbar: Toolbar? = null

    // toolbar titles respected to selected nav menu item
    private var activityTitles: Array<String>? = null

    // flag to load home fragment when user presses back key
    private val shouldLoadHomeFragOnBackPress = true

    private var mHandler: Handler? = null

    companion object {

//        // urls to load navigation header background image
//        // and profile image
//        private val urlNavHeaderBg = "https://api.androidhive.info/images/nav-menu-header-bg.jpg"
//        private val urlProfileImg =
//            "https://lh3.googleusercontent.com/eCtE_G34M9ygdkmOpYvCag1vBARCmZwnVS6rS5t4JLzJ6QgQSBquM0nuTsCpLhYbKljoyS-txg"

        // index to identify current nav menu item
        var navItemIndex = 0

        // tags used to attach the fragments
        private val TAG_HOME = "home"
        private val TAG_PROFILE = "profile"
        private val TAG_OFFER_RIDE = "offerRide"
        private val TAG_FIND_RIDE = "findRide"
        private val TAG_MY_BOOKING = "myBooking"
        private val TAG_MY_CAR = "myCars"
        private val TAG_SETTINGS = "settings"
        private val TAG_LOGOUT = "logout"
        var CURRENT_TAG = TAG_HOME
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        toolbar = findViewById<View>(R.id.my_toolbar) as Toolbar
        setSupportActionBar(toolbar)

        mHandler = Handler()

        drawer = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)


        // load toolbar titles from string resources
        activityTitles = resources.getStringArray(R.array.nav_item_activity_titles)


        // initializing navigation menu
        setUpNavigationView()

        if (savedInstanceState == null) {
            navItemIndex = 0
            CURRENT_TAG = TAG_HOME
            loadHomeFragment()
        }
    }

    private val homeFragment: Fragment
        get() {
            when (navItemIndex) {
                0 -> {
                    return MainFragment()
                }
                1 -> {
                    return ProfileFragment()
                }
                2 -> {
                    return RideListFragment()
                }
                3 -> {
                    return RideListFragment()
                }
                4 -> {
                    return MyBookingFragment()
                }
//                7 -> {
//                    session!!.logoutUser()
//                    startActivity(Intent(this, LoginActivity::class.java))
//                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
//                    finish()
//                    return null
//                }

                else -> return MainFragment()
            }
        }

    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private fun loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu()

        // set toolbar title
        setToolbarTitle()

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (supportFragmentManager.findFragmentByTag(CURRENT_TAG) != null) {
            drawer!!.closeDrawers()
            return
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        val mPendingRunnable = Runnable {
            // update the main content by replacing fragments
            val fragment = homeFragment
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
            fragmentTransaction.replace(R.id.first_container, fragment, CURRENT_TAG)
            fragmentTransaction.commitAllowingStateLoss()
        }

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler!!.post(mPendingRunnable)
        }

        //Closing drawer on item click
        drawer!!.closeDrawers()

        // refresh toolbar menu
        invalidateOptionsMenu()
    }

    private fun setToolbarTitle() {
        supportActionBar!!.setTitle(activityTitles!![navItemIndex])
    }

    private fun selectNavMenu() {
        navigationView!!.menu.getItem(navItemIndex).isChecked = true
    }

    private fun setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView!!.setNavigationItemSelectedListener(object : NavigationView.OnNavigationItemSelectedListener {

            // This method will trigger on item Click of navigation menu
           override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {

                //Check to see which item was being clicked and perform appropriate action
                when (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    R.id.nav_home -> {
                        navItemIndex = 0
                        CURRENT_TAG = TAG_HOME
                    }
                    R.id.nav_profile -> {
                        navItemIndex = 1
                        CURRENT_TAG = TAG_PROFILE
                    }
                    R.id.nav_find_ride -> {
                        navItemIndex = 2
                        CURRENT_TAG = TAG_FIND_RIDE
                    }
                    R.id.nav_offer_ride -> {
                        navItemIndex = 3
                        CURRENT_TAG = TAG_OFFER_RIDE
                    }
                    R.id.nav_my_booking -> {
                        navItemIndex = 4
                        CURRENT_TAG = TAG_MY_BOOKING
                    }
                    R.id.nav_logout ->{
                        session!!.logoutUser()
                        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                        finish()
                    }
                    else -> navItemIndex = 0
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false)
                } else {
                    menuItem.setChecked(true)
                }
                menuItem.setChecked(true)

                loadHomeFragment()

                return true
            }
        })


        val actionBarDrawerToggle =
            object : ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

               override fun onDrawerClosed(drawerView: View) {
                    // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                    super.onDrawerClosed(drawerView)
                }

               override fun onDrawerOpened(drawerView: View) {
                    // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                    super.onDrawerOpened(drawerView)
                }
            }

        //Setting the actionbarToggle to drawer layout
        drawer!!.setDrawerListener(actionBarDrawerToggle)

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState()
    }

    override fun onBackPressed() {
        if (drawer!!.isDrawerOpen(GravityCompat.START)) {
            drawer!!.closeDrawers()
            return
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0
                CURRENT_TAG = TAG_HOME
                loadHomeFragment()
                return
            }
        }

        super.onBackPressed()
    }
}
