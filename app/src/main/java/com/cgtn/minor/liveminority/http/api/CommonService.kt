package com.cgtn.minor.liveminority.http.api

import com.cgtn.minor.liveminority.mvp.model.HttpResponse
import com.cgtn.minor.liveminority.mvp.model.TaskEntity
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * created by yf on 2019/1/6.
 *
 * 公用的请求接口
 */
interface CommonService {

    @POST("/V1/task/login")
    fun login(@Body body: RequestBody): Observable<HttpResponse<String>>

    @POST("/V1/task/list")
    fun getTaskData(@Body body: RequestBody): Observable<HttpResponse<List<TaskEntity>>>

    @POST("/V1/performance/test")
    fun postData(@Body body: RequestBody):Observable<HttpResponse<String>>

}