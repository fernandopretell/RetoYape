package com.fulbiopretell.retoyape.viewmodel

import android.app.Application
import com.fulbiopretell.retoyape.model.Recipe
import com.fulbiopretell.retoyape.source.remote.RepositoryApp
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class RecipeListViewModelTest {

    @Mock
    private lateinit var application: Application

    @Mock
    private lateinit var repository: RepositoryApp

    private lateinit var viewModel: RecipeListViewModel

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        viewModel = RecipeListViewModel(application)
        viewModel.repository = repository
    }

    @Test
    fun `getRecipes success state`() {
        val recipe = Recipe("1", "recipe1", "description1", 0.0)
        val response = listOf(recipe)
        val successState = RepositoryApp.State.Success(response)
        runBlocking {
            Mockito.`when`(repository.getRecipes()).thenReturn(successState)
            viewModel.getRecipes().observeForever { viewState ->
                assertEquals(RecipeListViewModel.ViewState.Loading, viewState)
                assertEquals(RecipeListViewModel.ViewState.ListRecipesLoaded(response), viewState)
            }
        }
    }

    @Test
    fun `getRecipes error state`() {
        val exception = Exception("Error")
        runBlocking {
            Mockito.`when`(repository.getRecipes()).thenThrow(exception)
            viewModel.getRecipes().observeForever { viewState ->
                assertEquals(RecipeListViewModel.ViewState.Loading, viewState)
                assertEquals(RecipeListViewModel.ViewState.ListRecipesFailure("Error"), viewState)
            }
        }
    }
}