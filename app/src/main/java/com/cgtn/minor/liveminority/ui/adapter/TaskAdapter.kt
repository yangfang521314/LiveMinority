package com.cgtn.minor.liveminority.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cgtn.minor.liveminority.app.LiveApplication
import com.cgtn.minor.liveminority.contants.Constants
import com.cgtn.minor.liveminority.mvp.model.CommonListEntity
import com.cgtn.minor.liveminority.utils.toast
import com.cgtn.minor.liveminority.widget.OnItemClickListener
import com.cgtn.minor.liveminority.widget.SwipeMenu
import com.cgtn.minor.liveminority.widget.stickyitemdecoration.FullSpanUtil
import kotlinx.android.synthetic.main.item_task.view.*


class TaskAdapter(
    taskList: List<CommonListEntity>
) : RecyclerView.Adapter<ItemViewHolder>() {


    private val TYPE_STICKY_HEAD: Int = 5
    private var onItemClickListener: OnItemClickListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        var viewHolder: ItemViewHolder? = null

        when (viewType) {
            Constants.HEADER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(com.cgtn.minor.liveminority.R.layout.item_task, parent, false)
                viewHolder = ItemViewHolder(view, viewType)
            }
            Constants.CREATE,
            Constants.TASK -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(com.cgtn.minor.liveminority.R.layout.item_task, parent, false)
                viewHolder = ItemViewHolder(view, viewType)
            }

        }
        return viewHolder!!

    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        FullSpanUtil.onAttachedToRecyclerView(recyclerView, this, TYPE_STICKY_HEAD)
    }

    override fun onViewAttachedToWindow(holder: ItemViewHolder) {
        super.onViewAttachedToWindow(holder)
        FullSpanUtil.onViewAttachedToWindow(holder, this, TYPE_STICKY_HEAD)
    }


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        when (holder.itemViewType) {
//            Constants.HEADER -> {
//                holder.itemView.tv_header.text = data!![position].title
//            }
            Constants.TASK ->{
                holder.itemView.task_title.text = data!![position].list!!.headline

                holder.itemView.card_view.isSwipeEnable = false

                holder.itemView.task_live.setOnClickListener {
                    toast(LiveApplication.mInstance!!.baseContext,"fuck")
                }
            }
            Constants.CREATE -> {
                holder.itemView.task_title.text = data!![position].list!!.headline
                holder.itemView.card_view.setOnMenuClickListener(object : SwipeMenu.MenuListener {
                    override fun showLive() {
                        holder.itemView.task_live.visibility = View.VISIBLE

                    }

                    override fun closeLive() {
                        holder.itemView.task_live.visibility = View.INVISIBLE
                    }


                })

                holder.itemView.task_live.setOnClickListener {

                }
            }
        }

    }

    private var data: List<CommonListEntity>? = taskList

    override fun getItemCount(): Int {
        return if (data!!.isEmpty()) 0 else data!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return data!![position].type
    }

    fun setOnClickListener(clickListener: OnItemClickListener) {
        onItemClickListener = clickListener

    }

    fun getData(): List<CommonListEntity>? {
        return data
    }


}


