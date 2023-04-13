package com.fulbiopretell.retoyape.base

/**
 * Created by Fernando Pretell on 11/07/2020.
 * fernandopretell93@gmail.com
 *
 * Lima, Peru.
 **/
sealed class Result<out T: Any> {
    data class Success<out T : Any>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
   // object InProgress : Result<Nothing>()
}