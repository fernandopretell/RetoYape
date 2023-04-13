package com.fulbiopretell.retoyape.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.fulbiopretell.retoyape.R
import com.fulbiopretell.retoyape.base.viewBinding
import com.fulbiopretell.retoyape.core.extensions.loadUrl
import com.fulbiopretell.retoyape.databinding.FragmentRecipeDetailBinding
import com.fulbiopretell.retoyape.model.Recipe
import com.google.gson.Gson

const val LOCATION_RECIPE_LATITUDE = "locationRecipeLatitude"
const val LOCATION_RECIPE_LONGITUDE = "locationRecipeLongitude"

class RecipeDetailFragment : Fragment(R.layout.fragment_recipe_detail) {

    private val binding by viewBinding(FragmentRecipeDetailBinding::bind)

    private lateinit var dataRecipe: Recipe

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments.let {
            dataRecipe = Gson().fromJson(it?.getString(DATA_RECIPE), Recipe::class.java)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initListener()

    }

    private fun initViews() {
        binding.ivImage.loadUrl(dataRecipe.image, resources.getDrawable(R.drawable.placeholder_recipe))
        binding.tvName.text = dataRecipe.name
        binding.tvDescription.text = dataRecipe.description
    }

    private fun initListener() {

        binding.btnLocation.setOnClickListener {
            val bundle = bundleOf(LOCATION_RECIPE_LATITUDE to dataRecipe.latitud, LOCATION_RECIPE_LONGITUDE to dataRecipe.longitud)
            NavHostFragment.findNavController(this@RecipeDetailFragment).navigate(R.id.action_recipeDetailFragment_to_mapScreenFragment, bundle)
        }
    }
}