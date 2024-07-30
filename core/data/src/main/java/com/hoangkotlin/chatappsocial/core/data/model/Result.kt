package com.hoangkotlin.chatappsocial.core.data.model

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable? = null, val message: String? = null) :
        Result<Nothing>()
}

suspend fun <T> Result<T>.onSuccess(
    onSuccess: suspend (T) -> Unit
): Result<T> {
    if (this is Result.Success) {
        onSuccess(this.data)
    }
    return this
}

suspend fun <T> Result<T>.onError(
    onError: suspend (exception: Throwable?, message: String?) -> Unit
): Result<T> {
    if (this is Result.Error) {
        onError(this.exception, this.message)
    }
    return this
}