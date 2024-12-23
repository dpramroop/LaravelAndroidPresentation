package com.example.laravel

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface RetrofitApi {


    @POST("api/store")
    fun storeuser(@Body data: LaravelUser?):Call<LaravelUser?>?

    @POST("/api/Login")
    fun login(@Body user:LoginUser):Call<ResponseBody?>?

    @POST("/api/allDistricts")
    fun district(@Body search:Search):Call<List<District>?>?

    @GET("/api/allRoles")
    fun role():Call<List<Role>?>?

    @POST("/user")
    fun  // on below line we are creating a method to post our data.
            postData(@Body data: LaravelUser?): Call<LaravelUser?>?


    @GET("/token")
    fun  // on below line we are creating a method to post our data.
            getData(): Call<String?>?


    @GET("/csrf-token")
    suspend fun getCsrfToken(): Response<csrfToken>
}


data class AuthToken(
    val message: String,
    val token_type:String,
    val token:String,
    val f_name:String
)