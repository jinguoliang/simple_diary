package com.empty.jinux.simplediary.weather

import com.empty.jinux.baselibaray.log.loge
import com.empty.jinux.simplediary.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by jingu on 2018/2/25.
 *
 */

@Singleton
class WeatherManagerRetrofitImpl @Inject constructor() : WeatherManager {

    private val mOkHttpClientBuilder = OkHttpClient.Builder().apply {
        if (BuildConfig.DEBUG) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            addInterceptor(interceptor)
        }
    }

    private val mRetrofit = Retrofit.Builder()
            .baseUrl("http://api.openweathermap.org/data/2.5/")
            .client(mOkHttpClientBuilder.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

//    icon uri "http://openweathermap.org/img/w/$icon.png"

    private val mWeatherService = mRetrofit.create(WeatherApi::class.java)

    override fun getCurrentWeather(lat: Double, lon: Double, callback: (Weather) -> Unit) {
        val call = mWeatherService.getCurrentWeatherByCoordinates(lat, lon)
        call.enqueue(object : Callback<CurrentWeatherResult> {
            override fun onResponse(call: Call<CurrentWeatherResult>?, response: Response<CurrentWeatherResult>?) {
                val info = response?.body()?.weather?.get(0)
                if (info != null) {
                    callback(info)
                }
            }

            override fun onFailure(call: Call<CurrentWeatherResult>?, t: Throwable?) {
                loge("request weather data failed ${t.toString()}")
            }
        })
    }

}