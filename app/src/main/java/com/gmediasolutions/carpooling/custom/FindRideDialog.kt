package com.gmediasolutions.carpooling.custom

import android.app.Dialog
import android.content.Context
import android.support.design.widget.TextInputLayout
import android.widget.EditText
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.widget.Button
import com.gmediasolutions.carpooling.R
import com.gmediasolutions.carpooling.fragment.RideListFragment


class FindRideDialog(val fragmentManager:FragmentManager? ,internal var mycontext: Context) : Dialog(mycontext) {
    internal var btn_search: Button?=null
    internal var et_source: EditText?=null
    internal var et_destination: EditText?=null
    internal var et_time: EditText?=null
    internal var inputLayoutSource: TextInputLayout?=null
    internal var inputLayoutDestination: TextInputLayout?=null
    internal var inputLayoutTime: TextInputLayout?=null

//    private var progressBar: AVLoadingIndicatorView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        setContentView();
        setContentView(R.layout.dialog_search_ride)

        et_source = findViewById(R.id.et_source) as EditText
        et_destination = findViewById(R.id.et_destination) as EditText
        et_time = findViewById(R.id.et_time) as EditText



        btn_search = findViewById(R.id.btn_search) as Button
//        progressBar = findViewById(R.id.progressBar) as AVLoadingIndicatorView
        inputLayoutSource = findViewById(R.id.source_text_input) as TextInputLayout
        inputLayoutDestination= findViewById(R.id.destination_text_input) as TextInputLayout
        inputLayoutTime = findViewById(R.id.time_text_input) as TextInputLayout

        btn_search!!.setOnClickListener {
            //            val clickintent = Intent(mycontext, RideListActivity::class.java)
    //            mycontext.startActivity(clickintent)

//            val fragmentManager = getActivity(mycontext).getSupportFragmentManager()
//            val fragment = MainFragment()
//            fragmentManager.beginTransaction().replace(R.id.first_container, fragment).commit()

            val fragmentTransaction = fragmentManager!!.beginTransaction()
            val fragment = RideListFragment()
            fragmentTransaction.replace(R.id.first_container, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
            dismiss()


        }

    }
}