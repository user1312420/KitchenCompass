package com.app.kitchencompass.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.kitchencompass.DetailedActivity
import com.app.kitchencompass.Recipe
import com.app.kitchencompass.databinding.FragmentHomeBinding
import com.app.kitchencompass.ui.search.RecipeAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class HomeFragment : Fragment(), RecipeAdapter.OnItemClickListener {

    private var _binding: FragmentHomeBinding? = null
    private val client = OkHttpClient()
    private val baseURL: String = "http://34.67.0.113:9999/api/"
    private lateinit var homeAdapter: RecipeAdapter
    private lateinit var recyclerView: RecyclerView

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize RecyclerView
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Load recipes
        lifecycleScope.launch {
            try {
                val recipes: List<Recipe> = requestRecipes("random")
                homeAdapter = RecipeAdapter(recipes, this@HomeFragment)
                recyclerView.adapter = homeAdapter
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        return root
    }

    override fun onItemClick(recipe: Recipe) {
        val intent = Intent(context, DetailedActivity::class.java).apply {
            putExtra("RECIPE_NAME", recipe.name)
            putExtra("RECIPE_IMAGE", recipe.previewImage) // URL des Bildes
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

    @Throws(IOException::class)
    suspend fun requestRecipes(apiEndpoint: String): List<Recipe> {
        val url = "$baseURL$apiEndpoint"
        val request = Request.Builder().url(url).build()

        return withContext(Dispatchers.IO) {
            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string() ?: throw IOException("Unexpected empty response body")
                val recipeType = object : TypeToken<List<Recipe>>() {}.type
                Gson().fromJson(responseBody, recipeType)
            }
        }
    }
}
