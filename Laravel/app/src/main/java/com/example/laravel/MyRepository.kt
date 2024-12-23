package com.example.laravel

class MyRepository(private val apiService: RetrofitApi) {
    suspend fun fetchCsrfToken(): String? {
        val response = apiService.getCsrfToken()
        return if (response.isSuccessful) {
            response.body()?.csrfToken
        } else {
            null
        }
    }
}