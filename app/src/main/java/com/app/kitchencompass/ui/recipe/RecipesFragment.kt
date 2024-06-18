package com.app.kitchencompass.ui.recipe

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.kitchencompass.DetailedActivity
import com.app.kitchencompass.MyDatebaseHelper
import com.app.kitchencompass.Recipe
import com.app.kitchencompass.databinding.FragmentRecipeBinding
import com.app.kitchencompass.ui.search.RecipeAdapter

class RecipesFragment : Fragment(), RecipeAdapter.OnItemClickListener {

    private lateinit var myDB: MyDatebaseHelper
    private var _binding: FragmentRecipeBinding? = null
    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var recyclerView: RecyclerView


    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //Datenbank Helper initialisieren
        myDB = MyDatebaseHelper(requireContext())
        _binding = FragmentRecipeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //RecyclerView initialisieren
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)

        //ruft alle Rezepte aus der DB ab
        val recipes: List<Recipe> = myDB.getAllRecipes()
        recipeAdapter = RecipeAdapter(recipes, this@RecipesFragment)
        recyclerView.adapter = recipeAdapter

        return root
    }

    override fun onItemClick(recipe: Recipe) {
        val intent = Intent(context, DetailedActivity::class.java).apply {
            putExtra("RECIPE_ID", recipe.id)
            putExtra("RECIPE_NAME", recipe.name)
            putExtra("RECIPE_IMAGE", recipe.previewImage) //URL vom Bild
            putExtra("RECIPE_TIME", recipe.estimated_time)
            putExtra("RECIPE_INGREDIENTS", recipe.ingredients)
            putExtra("RECIPE_STEPS", recipe.instructions)
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
