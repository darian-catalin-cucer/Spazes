package com.mcdev.spazes.util

sealed class Resource<T>(val data: T?, val error: String?){
    class Success<T>(data: T): Resource<T>(data, null)
    class Error<T>(error: String): Resource<T>(null, error)
}

