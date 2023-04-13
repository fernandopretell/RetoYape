package com.fulbiopretell.retoyape.source.remote

import com.fulbiopretell.retoyape.base.BaseRepository
import com.fulbiopretell.retoyape.model.Recipe
import retrofit2.Response

class RepositoryApp (private val api: WebServiceData) : BaseRepository() {

    suspend fun getRecipes(): State {

        val recipesList = safeApiCall(
            call = { api.getRecipes().await() as Response<*>},
            errorMessage = "Error Occurred during getting safe Api result"
        )
        return State.Success(recipesList as List<Recipe>)
    }

    sealed class State {
        data class Success(val response: List<Recipe>) : State()
        data class Failure(val errorMessage: String) : State()
    }
}