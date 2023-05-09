package com.tfg.domain.util

sealed class ResultOf<out T> {

    object Loading : ResultOf<Nothing>()

    data class Failure(val e: Throwable?): ResultOf<Nothing>()

    data class Success<out R>(val data: R): ResultOf<R>()

}

fun <T,R> ResultOf<T>.transform(block :T.() -> R): ResultOf<R> {
    return when(this) {
        is ResultOf.Success<T> -> ResultOf.Success(data.block())
        is ResultOf.Failure -> this
        is ResultOf.Loading -> this
    }
}
