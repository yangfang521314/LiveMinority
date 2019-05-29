package com.cgtn.minor.livemin

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cgtn.minor.liveminority.R
import com.cgtn.minor.liveminority.contants.Constants
import com.cgtn.minor.liveminority.mvp.model.ConfigSetEntity
import com.cgtn.minor.liveminority.ui.adapter.ItemViewHolder
import kotlinx.android.synthetic.main.item_config.view.*
import kotlinx.android.synthetic.main.item_task_name.view.*

/**
 * created by yf on 2019-05-21.
 */
class ConfigAdapter(mConfigList: ArrayList<ConfigSetEntity>) : RecyclerView.Adapter<ItemViewHolder>() {

    private val mData = mConfigList


    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ItemViewHolder {
        var holder: View? = null
        when (position) {
            Constants.TASK_HEADER->{
                holder = LayoutInflater.from(parent.context).inflate(R.layout.item_task_name, parent,false)

            }
            Constants.RESOLUTION,
            Constants.BITRATE,
            Constants.FRAMERATE -> {
                holder = LayoutInflater.from(parent.context).inflate(R.layout.item_config, parent,false)

            }

        }
        return ItemViewHolder(holder!!)
    }

    override fun getItemCount(): Int {
        return if (mData.isEmpty()) 0 else mData.size
    }

    override fun getItemViewType(position: Int): Int {
        return mData[position].type
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        when (getItemViewType(position)) {
            Constants.TASK_HEADER -> {
                holder.itemView.task_name.text = "task name dddd"
//
            }
            Constants.RESOLUTION -> {
                holder.itemView.ll_config.visibility = View.VISIBLE
                holder.itemView.task_config_name.text = "Resolution"
                if (mData[1].configList!!.isEmpty()) {
                    return
                }
                if (mData[1].configList!![0] == 480) {
                    holder.itemView.choice1.text = "640x480P"
                }
                if (mData[1].configList!![1] == 720) {
                    holder.itemView.choice2.text = "1280x720P"

                }
                if (mData[1].configList!![2] == 1080) {
                    holder.itemView.choice3.text = "1920X1080P"
                }
            }
            Constants.FRAMERATE -> {
                holder.itemView.task_config_name.text = "FrameRate"
                holder.itemView.choice1.text = mData[2].configList!![0].toString()
                holder.itemView.choice2.text = mData[2].configList!![1].toString()
                holder.itemView.choice3.text = mData[2].configList!![2].toString()
            }

            Constants.BITRATE -> {
                holder.itemView.task_config_name.text = "Bitrate"
                holder.itemView.choice1.text = mData[3].configList!![0].toString()
                holder.itemView.choice2.text = mData[3].configList!![1].toString()
                holder.itemView.choice3.text = mData[3].configList!![2].toString()
            }

        }
    }

}