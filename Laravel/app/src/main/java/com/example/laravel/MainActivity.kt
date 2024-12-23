package com.example.laravel

import android.icu.text.StringSearch
import android.os.Bundle
import android.util.Log
import android.util.MutableByte
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.laravel.ui.theme.LaravelTheme
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LaravelTheme {
                val name = remember { mutableStateOf("") }
                val email = remember { mutableStateOf("") }
                val password = remember { mutableStateOf("") }
                val result = remember { mutableStateOf("hhh") }
                val alldist = remember { mutableListOf<District>() }
                val districtid= remember{ mutableStateOf(0) }
                val search = remember {
                    mutableStateOf("")
                }

                val isDropDownExpanded = remember {
                    mutableStateOf(false)
                }

                val itemPosition = remember {
                    mutableStateOf(0)
                }
                val navController = rememberNavController()
                NavHost(navController, startDestination = Login) {
                    composable<Login> {
                        LoginScreen(
                            email = email,
                            password = password,
                            onNavigateToRegister = { navController.navigate(route = Register) },
                            result = result,
                            navController
                        )


                    }
                    composable<Register> {
                        RegisterScreen(
                            alldist, name = name, email = email, password = password,search,districtid,
                            onNavigateToLogin = {
                                navController.navigate(
                                    route = Login
                                )
                            }
                        )
                    }

                    composable<Home> { backStackEntry ->
                        val nhome: Home = backStackEntry.toRoute()
                        HomeScreen(home = nhome)
                    }

                }
            }
        }
    }

    @Serializable
    data class Profile(val name: String)

    @Serializable
    object FriendsList

    @Serializable
    object Register

    @Serializable
    object Login

    @Serializable
    data class Home(val token: String)


    @Composable
    fun HomeScreen(home: Home) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = home.token.toString())
        }
    }

    @Composable
    fun LoginScreen(
        email: MutableState<String>,
        password: MutableState<String>,
        onNavigateToRegister: () -> Unit,
        result: MutableState<String>,
        navController: NavController
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "LOGIN")
            TextField(
                value = email.value,
                onValueChange = { email.value = it },
                placeholder = { Text("EMAIL") },
                label = { Text(text = "Email address") })
            TextField(
                value = password.value,
                onValueChange = { password.value = it },
                placeholder = { Text("PASSWORD") },
                label = { Text(text = "Password") })
            Button(onClick = {
                getTok(result = result, email, password, navController)
                if (!result.value.isEmpty()) {

                    // navController.navigate(route = Home(result.value) )
                }


            }) {
                Text(text = "LOGIN")
            }
            Button(onClick = { onNavigateToRegister() }) {
                Text(text = "REGISTER")
            }
        }
    }

    @Composable
    fun RegisterScreen(
        alldist: MutableList<District>,
        name: MutableState<String>,
        email: MutableState<String>,
        password: MutableState<String>,
        search:MutableState<String>,
        districtid:MutableState<Int>,
        onNavigateToLogin: () -> Unit
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "REGISTER")

            DropDownDemo(alldist, search = search,districtid)
            TextField(
                value = name.value,
                onValueChange = { name.value = it },
                placeholder = { Text("NAME") })
            TextField(
                value = email.value,
                onValueChange = { email.value = it },
                placeholder = { Text("EMAIL") })
            TextField(
                value = password.value,
                onValueChange = { password.value = it },
                placeholder = { Text("PASSWORD") })
            Button(onClick = {
                var user = LaravelUser(name.value, email.value, password.value)
                postData(user)
                Toast.makeText(this@MainActivity, name.value.toString(), Toast.LENGTH_LONG).show()
                name.value = ""
                email.value = ""
                password.value = ""
                onNavigateToLogin()
            }) {
                Text(text = "REGISTER")
            }
            Button(onClick = {
                name.value = ""
                email.value = ""
                password.value = ""
                onNavigateToLogin()
            }) {
                Text(text = "BACK")

            }
        }
    }

    @Composable
     fun DropDownDemo(alldist: MutableList<District>,search:MutableState<String>,districtid: MutableState<Int>) {

        var isDropDownExpanded = remember {
            mutableStateOf(false)
        }

        val itemPosition = remember {
            mutableStateOf(0)
        }

      //  Text(text = alldist.get(0).district)
        val usernames = listOf("Alexander", "Isabella", "Benjamin", "Sophia", "Christopher")

        val agriData = AgriData()
        agriData.getDistricts(alldist = alldist,search.value)

            Column(
                modifier = Modifier,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Box {
                 Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            isDropDownExpanded.value = true

                        }
                    ) {



                        TextField(value =search.value , onValueChange = {search.value=it})

                       // Text(text =alldist[itemPosition.value].district )
                        Image(
                            painter = painterResource(id = R.drawable.form_dropdown),
                            contentDescription = "DropDown Icon",
                         //   modifier = Modifier.clickable { isDropDownExpanded.value=!isDropDownExpanded.value }

                            )
                    }
                    DropdownMenu(
                        expanded = isDropDownExpanded.value,
                        onDismissRequest = {
                            isDropDownExpanded.value = false
                        }) {
                   //   Thread.sleep(1000)
                   //    TimeUnit.SECONDS.sleep(1)

                       alldist.forEachIndexed {index,district->
                            DropdownMenuItem(text = {
                                Text(text = district.district)
                            },
                                onClick = {
                                     search.value=district.district
                                    isDropDownExpanded.value = false
                                    itemPosition.value = index
                                })
                        }
                    }
                }

            }




    }
  //
  @Composable
  fun DropDownRole(alldist: MutableList<District>,search:MutableState<String>,roleid:MutableState<Int>) {

      var isDropDownExpanded = remember {
          mutableStateOf(false)
      }

      val itemPosition = remember {
          mutableStateOf(0)
      }

      //  Text(text = alldist.get(0).district)
      val usernames = listOf("Alexander", "Isabella", "Benjamin", "Sophia", "Christopher")

      val agriData = AgriData()
      agriData.getDistricts(alldist = alldist,search.value)

      Column(
          modifier = Modifier,
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
      ) {

          Box {
              Row(
                  horizontalArrangement = Arrangement.Center,
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.clickable {
                      isDropDownExpanded.value = true

                  }
              ) {



                  TextField(value =search.value , onValueChange = {search.value=it})

                  // Text(text =alldist[itemPosition.value].district )
                  Image(
                      painter = painterResource(id = R.drawable.form_dropdown),
                      contentDescription = "DropDown Icon",
                      //   modifier = Modifier.clickable { isDropDownExpanded.value=!isDropDownExpanded.value }

                  )
              }
              DropdownMenu(
                  expanded = isDropDownExpanded.value,
                  onDismissRequest = {
                      isDropDownExpanded.value = false
                  }) {
                  //   Thread.sleep(1000)
                  //    TimeUnit.SECONDS.sleep(1)

                  alldist.forEachIndexed {index,district->
                      DropdownMenuItem(text = {
                          Text(text = district.district)
                      },
                          onClick = {
                              search.value=district.district
                              roleid.value=district.id
                              isDropDownExpanded.value = false
                              itemPosition.value = index
                          })
                  }
              }
          }

      }




  }


    @Composable
    fun ProfileScreen(
        profile: Profile,
        onNavigateToFriendsList: () -> Unit,
    ) {
        Column(){
            Text("Profile for ${profile.name}")
            Button(onClick = { onNavigateToFriendsList() }) {
                Text("Go to Friends List")
            }
        }

    }

    @Composable
    fun FriendsListScreen(onNavigateToProfile: () -> Unit) {

        Column(){
            Text("Friends List")
            Button(onClick = { onNavigateToProfile() }) {
                Text("Go to Profile")
            }
        }


    }


    suspend fun getcsrf(result: MutableState<String>?) {
        var url = "http://10.0.2.2:8000/"
        // on below line we are creating a retrofit
        // builder and passing our base url
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()
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
       // val dataModel = LaravelUser("Darrin")
        // calling a method to create an update and passing our model class.
        //  val csrfToken = MyRepository.fetchCsrfToken(retrofitAPI)
    }


    fun postData(user: LaravelUser) {
        var result =""
        var url = "http://10.0.2.2:8000/"
        // on below line we are creating a retrofit
        // builder and passing our base url
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()
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
        val call: Call<LaravelUser?>? = retrofitAPI.storeuser(user)

        // on below line we are executing our method.
        call!!.enqueue(object : Callback<LaravelUser?> {
            override fun onResponse(call: Call<LaravelUser?>?, response: Response<LaravelUser?>) {
                // this method is called when we get response from our api.
                if (response.isSuccessful) {
                    if (result != null) {
                        result = "SENT"
                        Toast.makeText(this@MainActivity,result,Toast.LENGTH_LONG).show()
                    }
                } else {
                    if (result != null) {
                        result = response.body().toString()
                        Toast.makeText(this@MainActivity,result,Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<LaravelUser?>?, t: Throwable) {
                // we get error response from API.
                if (result != null) {
                    result = "Error found is : " + t.message
                    Toast.makeText(this@MainActivity,result,Toast.LENGTH_LONG).show()
                }
                Log.d("Retrofit", "onFailure: " + t.message)
            }

        })
    }

   fun getTok(result:MutableState<String>, email:MutableState<String>, password: MutableState<String>, navController: NavController)
    {
        var url = "http://10.0.2.2:8000/"

        val okHttpClient= OkHttpClient.Builder()
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

        val user= LoginUser(email.value,password.value) // calling a method to create an update and passing our model class.
        val call: Call<ResponseBody?>? = retrofitAPI.login(user)
        call!!.enqueue(object : Callback<ResponseBody?>{
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                if(response.isSuccessful)
                {
                    val gson= Gson()

                    val csrf= gson.fromJson(response.body()?.string(),AuthToken::class.java)

                   
                    result.value=csrf.f_name


                    Log.e("RESULTS",result.value)
                    navController.navigate(route = Home(result.value) )



                }
                else{
                    Toast.makeText(this@MainActivity,"INCORRECT EMAIL OR PASSWORD",Toast.LENGTH_SHORT).show()
                    Log.e("RESULTS","INCORRECT EMAIL OR PASSWORD")
                    result.value="ello "+response.errorBody().toString()
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                result.value=t.message.toString()
            }

        })
    }


    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        LaravelTheme {
            Greeting("Android")
        }
    }

}