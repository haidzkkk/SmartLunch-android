package com.fpoly.smartlunch.data.network

import android.content.Context
import com.fpoly.smartlunch.data.model.ZaloPayInfo
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RemoteDataSource(

) {
    companion object{
        public const val BASE_URL = "http://192.168.137.13:3000"
        public const val OPEN_STREET_MAP_URL = "https://nominatim.openstreetmap.org/"
        public const val URL_PROVINCE = "https://vapi.vnappmob.com"
    }

    public fun <API> buildApi(apiClass: Class<API>, context: Context): API{
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(getHttpClientBuilder(context).build())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(apiClass)
    }

    public fun <API> buildApiProvince(apiClass: Class<API>, context: Context): API{
        return Retrofit.Builder()
            .baseUrl(URL_PROVINCE)
            .client(getHttpClientBuilder(context).build())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(apiClass)
    }

    public fun <API> buildApiOpenStreetMap(apiClass: Class<API>, context: Context): API{
        return Retrofit.Builder()
            .baseUrl(OPEN_STREET_MAP_URL)
            .client(getHttpClientBuilder(context).build())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(apiClass)
    }

    public fun <API> buildApiOrderZalopay(apiClass: Class<API>, context: Context): API{
        return Retrofit.Builder()
            .baseUrl(ZaloPayInfo.URL_CREATE_ORDER)
            .client(getHttpClientBuilder(context).build())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(apiClass)
    }

    // build okhttp
    private fun getHttpClientBuilder(context: Context) : OkHttpClient.Builder{
        var builder: OkHttpClient.Builder = OkHttpClient.Builder()
        builder.addInterceptor(getInterceptorHeader(context))
        builder.addInterceptor(getInterceptorLogging())
        return builder
    }

    // add header
    private fun getInterceptorHeader(context: Context): Interceptor
            = Interceptor {
                var originalRequest: Request = it.request()
                var newRequest : Request = originalRequest.newBuilder()
                    .header("Authorization", SessionManager(context.applicationContext)
                        .fetchAuthTokenAccess().let {token ->
                        if (token.isNullOrEmpty()) "Basic Y29yZV9jbGllbnQ6c2VjcmV0" else "Bearer $token" }
                    )
                    .build();
                it.proceed(newRequest)
            }
    }

// log data
    private fun getInterceptorLogging(): HttpLoggingInterceptor
            = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
