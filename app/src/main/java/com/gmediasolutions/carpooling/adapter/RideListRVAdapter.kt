package com.gmediasolutions.carpooling.adapter

import android.content.Context
import android.support.annotation.NonNull
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.gmediasolutions.carpooling.R


class RideListRVAdapter// data is passed into the constructor
internal constructor(context: Context, private val mData: Array<String>) :
    RecyclerView.Adapter<RideListRVAdapter.ViewHolder>() {
    private val mInflater: LayoutInflater
    private var mClickListener: ItemClickListener? = null

    init {
        this.mInflater = LayoutInflater.from(context)
    }

    // inflates the cell layout from xml when needed
    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.ride_find_rv_view, parent, false)
        return ViewHolder(view)
    }

    // binds the data to the TextView in each cell
    override fun onBindViewHolder(@NonNull holder: ViewHolder, position: Int) {
//        holder.btnGetCode.setOnClickListener(View.OnClickListener {

//        })
    }

    // total number of cells
    override fun getItemCount(): Int {
        return mData.size
    }


    // stores and recycles views as they are scrolled off screen
    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
//        internal var myTextView: TextView
//        internal var btnGetCode: Button

        init {

            itemView.setOnClickListener(this)
//            customVendorDialog =  CustomVendorDialog(itemView.context)
        }

        override fun onClick(view: View) {
            if (mClickListener != null) mClickListener!!.onItemClick(view, adapterPosition)

        }
    }

    // convenience method for getting data at click position
    internal fun getItem(id: Int): String {
        return mData[id]
    }

    // allows clicks events to be caught
    internal fun setClickListener(itemClickListener: ItemClickListener) {
        this.mClickListener = itemClickListener
    }

    // parent activity will implement this method to respond to click events
    interface ItemClickListener {
        fun onItemClick(view: View, position: Int)
    }
}