package com.gmediasolutions.carpooling.session

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import com.gmediasolutions.carpooling.base.LoginActivity


import java.util.HashMap


class UserSession(internal var context: Context) {

    internal var pref: SharedPreferences
    internal var editor: SharedPreferences.Editor
    internal var PRIVATE_MODE = 0

    companion object {

        private val PREF_NAME = "UserSessionPref"
        internal val FIRST_TIME = "firsttime"
        private val USER_IS_LOGIN = "IsLoggedIn"

        val USER_ID = "UserId"
        val USER_TOKEN = "UserToken"

        val IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch"

//        val ADD_TOCART="0"

    }

    init {
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = pref.edit()
    }

    val userDetails: HashMap<String, String>
        get() {
            val user = HashMap<String, String>()
            user[USER_ID] = pref.getString(USER_ID, "null")
            user[USER_TOKEN] = pref.getString(USER_TOKEN, "null")
            return user
        }

    // Get Login State
    val isLoggedIn: Boolean
        get() = pref.getBoolean(USER_IS_LOGIN, false)


   /* //Get Cart Value
    fun getCartValue(): Int {
        return pref.getInt(ADD_TOCART, 0)
    }

    fun setCartValue(count: Int) {
        editor.putInt(ADD_TOCART, count)
        editor.commit()
    }*/


    var firstTime: Boolean?
        get() = pref.getBoolean(FIRST_TIME, true)
        set(n) {
            editor.putBoolean(FIRST_TIME, n!!)
            editor.commit()
        }

    var isFirstTimeLaunch: Boolean
        get() = pref.getBoolean(IS_FIRST_TIME_LAUNCH, true)
        set(isFirstTime) {
            editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime)
            editor.commit()
        }


    fun createLoginSession(UserId: String, UserToken: String) {
        editor.putBoolean(USER_IS_LOGIN, true)
        editor.putString(USER_ID, UserId)
        editor.putString(USER_TOKEN, UserToken)
        editor.commit()
    }

    fun checkLogin() {
        if (!this.isLoggedIn) {
            val i = Intent(context, LoginActivity::class.java)
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            // Add new Flag to start new Activity
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            // Staring Login Activity
            context.startActivity(i)
        }

    }

    fun logoutUser() {
        editor.clear()
        editor.commit()
        val i = Intent(context, LoginActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(i)
    }

    /*//Add to card -> increase or decrese

    fun increaseCartValue() {
        val increase = getCartValue() + 1
        editor.putInt(ADD_TOCART, increase)
        editor.commit()
        Log.e("Cart Value PE", "Var value : " + increase + "Cart Value :" + getCartValue() + " ")
    }

    fun decreaseCartValue() {
        val decrease = getCartValue() - 1
        editor.putInt(ADD_TOCART, decrease)
        editor.commit()
        Log.e("Cart Value PE", "Var value : " + decrease + "Cart Value :" + getCartValue() + " ")
    }*/
}