package com.cgtn.minor.liveminority.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.cgtn.minor.liveminority.R
import com.cgtn.minor.liveminority.mvp.model.TaskEntity
import kotlinx.android.synthetic.main.item_create.view.*

class CreateAdapter : RecyclerView.Adapter<ItemViewHolder>() {

    private  var createTaskList: List<TaskEntity> = ArrayList()


    override fun getItemCount(): Int {
        return if (createTaskList.isEmpty()) {
            0
        } else {
            createTaskList.size
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ItemViewHolder {
        return ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_create, null))

    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.itemView.task_title.text = createTaskList[position].headline
    }

    /**
     * 设置数据
     */
    fun setData(taskEntity: List<TaskEntity>) {
        createTaskList = taskEntity
    }


}
