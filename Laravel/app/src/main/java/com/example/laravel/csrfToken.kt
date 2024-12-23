package com.example.laravel

import com.google.gson.annotations.SerializedName

data class csrfToken (@SerializedName("csrf_token") val csrfToken: String)