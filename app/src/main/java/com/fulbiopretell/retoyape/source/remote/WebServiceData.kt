package com.fulbiopretell.retoyape.source.remote

import com.fulbiopretell.retoyape.model.Recipe
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET

interface WebServiceData {

    @GET("getRecipes/")
    fun getRecipes(): Deferred<Response<List<Recipe>>?>
}