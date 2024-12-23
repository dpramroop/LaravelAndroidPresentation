package com.example.laravel

data class LaravelUser(val f_name:String,val email:String,val password:String)
data class LoginUser(val email:String,val password:String)
data class Search(val search: String)