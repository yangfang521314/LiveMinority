package com.cgtn.minor.liveminority.mvp.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * created by yf on 2019/1/16.
 *  id 任务序列号
 *  title 推流地址
 *
 *   "id": 44,
 *   "headline": "China's fiscal revenue up 6.2 pct in Q1",
 *   "pushUrl": "rtmp://52.220.161.193:1935/tv/CGTN_1_10107",
 *   "startTime": "2019-4-17 15:33"
 */
@Entity(tableName = "task")
data class TaskEntity(@PrimaryKey var id: Int, var headline: String, val pushUrl: String, val startTime: String)