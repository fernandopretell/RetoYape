package com.fulbiopretell.retoyape.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import com.fulbiopretell.retoyape.model.Recipe
import com.fulbiopretell.retoyape.source.remote.HelperWs
import com.fulbiopretell.retoyape.source.remote.RepositoryApp
import kotlinx.coroutines.Dispatchers
import timber.log.Timber

class RecipeListViewModel(application: Application) : AndroidViewModel(application)  {

    var repository: RepositoryApp = RepositoryApp(HelperWs.getServiceData())

    fun getRecipes() = liveData(Dispatchers.IO) {
        emit(ViewState.Loading)
        try {
            val getRecipesResponse = repository.getRecipes() as RepositoryApp.State.Success
            emit(ViewState.ListRecipesLoaded(getRecipesResponse.response))
        } catch (e: Exception) {
            emit(ViewState.ListRecipesFailure("Error"))
            Timber.e(e)
        }
    }

    sealed class ViewState() {
        object Loading : ViewState()
        data class ListRecipesLoaded(val listRecipes: List<Recipe>) : ViewState()
        data class ListRecipesFailure(val error: String) : ViewState()
    }
}