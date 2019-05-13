package com.cgtn.minor.liveminority.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.cgtn.minor.liveminority.R
import com.cgtn.minor.liveminority.mvp.model.TaskEntity
import com.cgtn.minor.liveminority.widget.OnItemClickListener
import kotlinx.android.synthetic.main.item_task.view.*

class TaskAdapter(
    taskList: List<TaskEntity>
) : RecyclerView.Adapter<ItemViewHolder>() {


    private var onItemClickListener: OnItemClickListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ItemViewHolder {
        return ItemViewHolder(View.inflate(parent.context, R.layout.item_task, null))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.itemView.task_title.text = mTaskEntity?.get(position)!!.headline
        holder.itemView.task_id.text = (position+1).toString()
        holder.itemView.task_live.setOnClickListener {
            onItemClickListener?.onClickListener(holder.itemView, mTaskEntity!![position])
        }
    }
    private var mTaskEntity: List<TaskEntity>? = taskList

    override fun getItemCount(): Int {
        return if (mTaskEntity!!.isEmpty()) 0 else mTaskEntity!!.size
    }

    fun setOnClickListener(clickListener: OnItemClickListener) {
        onItemClickListener = clickListener

    }


}


