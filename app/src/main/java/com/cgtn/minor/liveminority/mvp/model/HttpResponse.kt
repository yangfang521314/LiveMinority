package com.cgtn.minor.liveminority.mvp.data

/**
 * created by yf on 2019/4/19.
 */
data class HttpResponse<T> constructor(val data: T, val description: String, val status: Int) {

}