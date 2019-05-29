package com.cgtn.minor.liveminority.mvp.data

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.GsonBuilder

/**
 * created by yf on 2019/1/16.
 *  id 任务序列号
 *  title 推流地址
 *
 *  "id":110,
 *  "headline":"2UK PM May steps up calls for Labour to agree a Brexit deal",
 *  "pushUrl":"rtmp://52.220.161.193:1935/tv/CGTN_1_Gt68J3Lufe",
 *  "comment":null,"apiUrl":"http://52.220.161.193:8081/"
 */
@Entity(tableName = "task")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) var id: Int, var headline: String, val pushUrl: String, val comment: String,
    var apiUrl: String
) {
    fun fromJson(json: String?): TaskEntity {
        return GsonBuilder().create().fromJson(json, this.javaClass)
    }

    fun toJson(): String {
        return GsonBuilder().create().toJson(this)
    }
}