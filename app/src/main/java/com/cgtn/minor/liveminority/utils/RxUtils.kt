package com.cgtn.minor.liveminority.utils

import com.cgtn.minor.liveminority.mvp.model.HttpResponse
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * created by yf on 2019/4/19.
 */
class RxUtils {

    companion object {
        //compose简化线程 针对Flowable
        fun <T> rxFlSchedulerHelper(): FlowableTransformer<T, T> {
            return FlowableTransformer { _t ->
                _t.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
            }
        }

        // 针对Observable
        fun <T> rxObScheduleHelper(): ObservableTransformer<T, T> {
            return ObservableTransformer { _t ->
                _t.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
            }
        }

        val TAG = RxUtils::class.java.simpleName

        /**
         * 统一处理服务器数据
         *
         * @param <T>
         * @return
        </T> */
        fun <T> handleResult(): FlowableTransformer<HttpResponse<T>, T> {
            return FlowableTransformer { _t ->
                _t
                    .compose(rxFlSchedulerHelper())
                    .flatMap { tHttpResponse ->
                        if (tHttpResponse.status == 200) {
                            LogUtil.e("status:${tHttpResponse.status}")
                            if (tHttpResponse.data == null) {
                                Flowable.error(Throwable("没有数据"))
                            } else createData(tHttpResponse.data)
                        } else {
                            LogUtil.e("服务器返回error")
                            Flowable.error(
                                Throwable(
                                    tHttpResponse.description,
                                    Throwable("错误码" + tHttpResponse.status)
                                )
                            )
                        }
                    }
            }
        }


        /**
         * 统一处理服务器数据 observable
         *
         * @param <T>
         * @return
        </T> */
        fun <T> handleObservableResult(): ObservableTransformer<HttpResponse<T>, T> {
            return ObservableTransformer { upstream ->
                upstream
                    .flatMap { tHttpResponse ->
                        createObservableData(tHttpResponse)
                    }
            }
        }

        private fun <T> createObservableData(response: HttpResponse<T>): ObservableSource<out T>? {
            return Observable.create { emitter ->
                LogUtil.e("${response.status}")
                if (response != null) {
                    if (response.status == 200) {
                        emitter.onNext(response.data)
                    } else {
                        if (response.description != null) {
                            emitter.onError(Throwable(response.description, Throwable("错误码" + response.status)))
                        } else {
                            emitter.onError(Throwable("server error"))
                        }
                    }
                    emitter.onComplete()
                }else{
                    emitter.onError(Throwable("network error"))
                }
            }
        }

//        private fun <T> createObservableData(result: T): ObservableSource<T> {
//            return Observable.create { emitter ->
//                try {
//                    emitter.onNext(result)
//                    emitter.onComplete()
//                } catch (e: Exception) {
//                    emitter.onError(e)
//                }
//            }
//        }

        /**
         * 生成Flowable,数据量大的时候背压处理
         *
         * @param result
         * @param <T>
         * @return
        </T> */
        private fun <T> createData(result: T): Flowable<T> {
            return Flowable.create({ emitter ->
                try {
                    emitter.onNext(result)
                    emitter.onComplete()
                } catch (e: Exception) {
                    emitter.onError(e)
                }
            }, BackpressureStrategy.BUFFER)
        }

    }


}