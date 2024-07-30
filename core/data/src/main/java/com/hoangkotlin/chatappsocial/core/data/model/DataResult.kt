package com.hoangkotlin.chatappsocial.core.data.model

sealed class DataResult<out T> {
    sealed class Success<T> : DataResult<T>() {
        abstract val data: T

        data class Network<T>(override val data: T) : Success<T>()
        data class Cache<T>(override val data: T, val shouldNotify: Boolean) : Success<T>()
    }


    data class Error(
        val errorMessage: String,
        val errorCode: Code = Code.None
    ) : DataResult<Nothing>() {
        enum class Code {
            NotFound, NotAuthorized, Blocked, None, BadRequest, BadCredential
        }
    }
}

fun <T> DataResult<T>.onSuccess(
    onSuccess: (T) -> Unit
): DataResult<T> {
    if (this is DataResult.Success) {
        onSuccess(this.data)
    }
    return this
}


fun <T> DataResult<T>.onError(
    onError: (
        errorMessage: String,
        errorCode: DataResult.Error.Code
    ) -> Unit
): DataResult<T> {
    if (this is DataResult.Error) {
        onError(this.errorMessage, this.errorCode)
    }
    return this
}

suspend fun <T> DataResult<T>.onSuccessSuspend(
    onSuccess: suspend (T) -> Unit
): DataResult<T> {
    if (this is DataResult.Success) {
        onSuccess(this.data)
    }
    return this
}


suspend fun <T> DataResult<T>.onErrorsSuspend(
    onError: suspend (
        errorMessage: String,
        errorCode: DataResult.Error.Code
    ) -> Unit
): DataResult<T> {
    if (this is DataResult.Error) {
        onError(this.errorMessage, this.errorCode)
    }
    return this
}