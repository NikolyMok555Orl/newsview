package com.test.newsview.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface Api {
    @GET("top-headlines/")
    suspend fun getArticleRepos(
        @Query("q") query: String?=null,
        @Query("apiKey") key: String= token,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Query("country") country: String?=null,
        @Query("category") category: String?=null
    ): NewsResponse


    companion object {

        private const val URL = "https://newsapi.org/v2/"
        private const val token="Ваш токен"
        private var api: Api? = null

        private fun createApi() {
            val interceptor : HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
                this.level = HttpLoggingInterceptor.Level.BODY
            }

            val client : OkHttpClient = OkHttpClient.Builder().apply {
                this.addInterceptor(interceptor)
                this.readTimeout(60, TimeUnit.SECONDS)
                this.connectTimeout(60, TimeUnit.SECONDS)
            }.build()

            val retrofit = Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            api = retrofit.create(Api::class.java)
        }

        fun get(): Api {
            if (api == null)
                createApi()
            return api!!
        }
    }


}