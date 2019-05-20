package com.cgtn.minor.liveminority.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cgtn.minor.liveminority.contants.Constants
import com.cgtn.minor.liveminority.contants.Constants.Companion.HEADER
import com.cgtn.minor.liveminority.mvp.model.CommonListEntity
import com.cgtn.minor.liveminority.widget.SwipeMenu
import com.cgtn.minor.liveminority.widget.listener.OnItemClickListener
import com.cgtn.minor.liveminority.widget.stickyitemdecoration.FullSpanUtil
import kotlinx.android.synthetic.main.item_header.view.*
import kotlinx.android.synthetic.main.item_task.view.*


class TaskAdapter(
) : RecyclerView.Adapter<ItemViewHolder>() {


    private var onItemClickListener: OnItemClickListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        var viewHolder: ItemViewHolder? = null

        when (viewType) {
            HEADER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(com.cgtn.minor.liveminority.R.layout.item_header, parent, false)
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
        FullSpanUtil.onAttachedToRecyclerView(recyclerView, this, HEADER)
    }

    override fun onViewAttachedToWindow(holder: ItemViewHolder) {
        super.onViewAttachedToWindow(holder)
        FullSpanUtil.onViewAttachedToWindow(holder, this, HEADER)
    }


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        when (holder.itemViewType) {
            HEADER -> {
                holder.itemView.tv_header.text = data!![position].title
            }
            Constants.TASK -> {
                holder.itemView.task_title.text = data!![position].list!!.headline

                holder.itemView.card_view.isSwipeEnable = false

                holder.itemView.task_live.setOnClickListener {
                    onItemClickListener!!.onClickListener(it, data!![position].list!!)
                }
                holder.itemView.task_id.text = data!![position].list!!.id.toString()
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
                    onItemClickListener!!.onClickListener(it, data!![position].list!!)
                }

                holder.itemView.menu_edit.setOnClickListener {
                    onItemClickListener!!.onShowMenuPop()
                }
                holder.itemView.task_id.text = data!![position].list!!.id.toString()

            }

        }

    }


    private var data: List<CommonListEntity>? = ArrayList()

    override fun getItemCount(): Int {
        return if (data == null && data!!.isEmpty()) 0 else data!!.size
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

    fun setData(taskList: ArrayList<CommonListEntity>) {
        data = taskList

    }


}


