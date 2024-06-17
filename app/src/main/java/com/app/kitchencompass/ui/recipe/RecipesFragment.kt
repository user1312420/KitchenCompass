package com.app.kitchencompass.ui.recipe

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.app.kitchencompass.DetailedActivity
import com.app.kitchencompass.MyDatebaseHelper
import com.app.kitchencompass.R
import com.app.kitchencompass.Recipe
import com.app.kitchencompass.databinding.FragmentRecipeBinding
import com.app.kitchencompass.ui.search.RecipeAdapter


class RecipesFragment : Fragment(), RecipeAdapter.OnItemClickListener {

    private lateinit var myDB: MyDatebaseHelper
    private var _binding: FragmentRecipeBinding? = null
    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var recyclerView: RecyclerView

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.activity_search, container, false)
        recyclerView = view.findViewById(R.id.recycler_view)
        val recipes: List<Recipe> = myDB.getAllRecipes()
        recipeAdapter = RecipeAdapter(recipes, this@RecipesFragment)
        recyclerView.adapter = recipeAdapter

        _binding = FragmentRecipeBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(recipe: Recipe) {
        // Handle item click and navigate to DetailedActivity
        val intent = Intent(context, DetailedActivity::class.java)
        intent.putExtra("RECIPE_NAME", recipe.name)
        intent.putExtra("RECIPE_IMAGE", recipe.previewImage)
        intent.putExtra("RECIPE_TIME", recipe.estimated_time)
        intent.putExtra("RECIPE_INGREDIENTS", recipe.ingredients)
        intent.putExtra("RECIPE_STEPS", recipe.instructions)
        startActivity(intent)
    }
}