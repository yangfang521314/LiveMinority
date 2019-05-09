//package com.cgtn.minor.liveminority.utils
//
//import io.reactivex.Flowable
//import io.reactivex.functions.Function
//
//
//class HttpResultFunc<T> : Function<Throwable, Flowable<T>> {
//    @Throws(Exception::class)
//    override fun apply(throwable: Throwable): Flowable<T> {
//        return Flowable.error(ExceptionConverter.convertException(throwable))
//    }
//}
//
