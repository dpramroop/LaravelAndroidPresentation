package com.example.laravel

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import com.example.laravel.MainActivity.Home
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.await
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class AgriData {
    val url = "http://10.0.2.2:8000/"


    fun getDistricts(alldist:MutableList<District>,search:String){

        //  var districtList=DistrictList(listOf(District(1,"ss")))
     //   var adddistricts = listOf<District>()
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()
        // on below line we are creating a retrofit
        // builder and passing our base url


        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            // as we are sending data in json format so
            // we have to add Gson converter factory
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            // at last we are building our retrofit builder.
            .build()
        // below the line is to create an instance for our retrofit api class.
        val retrofitAPI = retrofit.create(RetrofitApi::class.java)
        // passing data from our text fields to our model class.

        // calling a method to create an update and passing our model class.
        val searched=Search(search)
        val call=retrofitAPI.district(searched)


        call!!.enqueue(object : Callback<List<District>?> {
            @SuppressLint("SuspiciousIndentation")
            override fun onResponse(
                call: Call<List<District>?>,
                response: Response<List<District>?>
            ) {

                alldist.clear()
                if (response.isSuccessful) {
                   response.body()!!.forEach {

                       alldist.add(it)
                   }

                    //Log.e("ALLDIST", alldist.toString())
                }


            }

            override fun onFailure(call: Call<List<District>?>, t: Throwable) {
                Log.e("ERR", t.message.toString())
            }

        })

    }
    }

fun getRoles(allrole: MutableList<Role>)
{
    val url = "http://10.0.2.2:8000/"
    //  var districtList=DistrictList(listOf(District(1,"ss")))
    //   var adddistricts = listOf<District>()
    val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()
    // on below line we are creating a retrofit
    // builder and passing our base url


    val retrofit = Retrofit.Builder()
        .baseUrl(url)
        // as we are sending data in json format so
        // we have to add Gson converter factory
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        // at last we are building our retrofit builder.
        .build()
    // below the line is to create an instance for our retrofit api class.
    val retrofitAPI = retrofit.create(RetrofitApi::class.java)
    // passing data from our text fields to our model class.

    // calling a method to create an update and passing our model class.
    val call=retrofitAPI.role()


    call!!.enqueue(object :Callback<List<Role>?>{
        override fun onResponse(call: Call<List<Role>?>, response: Response<List<Role>?>) {
            if (response.isSuccessful) {
                response.body()!!.forEach {
                    allrole.add(it)
                }

                Log.e("ALLDIST", allrole.toString())
            }
        }

        override fun onFailure(call: Call<List<Role>?>, t: Throwable) {
            Log.e("ERR", t.message.toString())
        }

    })



}

data class Role(
    val id:Int,
    val name:String,
    val display_name:String,
    val role:String,
    val slug:String,
    val description:String,
    val created_at:String,
    val updated_at:String

)





data class DistrictList(
    var dislist:List<District>
)

data class District(
    val id:Int,
    val district:String,
    val ward_id:Int,
   val farmer_district_id:Int,
    val deleted_at:String

)