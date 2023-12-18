package com.example.sg.data

data class APIResult<out T>(val status: Status, val data: T?, val message: String?) {
    enum class Status {
        SUCCESS,
        ERROR,
        LOADING
    }

    companion object {
        fun <T> success(data: T): APIResult<T> {
            return APIResult(Status.SUCCESS, data, null)
        }

        fun <T> error(message: String, data: T? = null): APIResult<T> {
            return APIResult(Status.ERROR, data, message)
        }

        fun <T> loading(data: T? = null): APIResult<T> {
            return APIResult(Status.LOADING, null, null)
        }
    }
}