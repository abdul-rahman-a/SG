package com.example.sg.data

import org.json.JSONObject
import retrofit2.Response


/**
 * Abstract Base Data source class with error handling
 */

//this class will load api response and covert into APIResult object
abstract class BaseRemoteDataSource {
    protected suspend fun <T> getResult(call: suspend () -> Response<T>): APIResult<T> {
        try {
            val response = call()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) return APIResult.success(body)
            } else {
                val jObjError = JSONObject(response.errorBody()!!.string())
                return error(" ${response.code()} ${jObjError.getJSONObject("error").getString("message")}")
            }

            return error(" ${response.code()} ${response.message()}")
        } catch (e: Exception) {
            return error(e.message ?: e.toString())
        }
    }

    private fun <T> error(message: String): APIResult<T> {
        return APIResult.error("Error: $message")
    }
}