package com.app.kitchencompass.ui.search

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.kitchencompass.DetailedActivity
import com.app.kitchencompass.R
import com.app.kitchencompass.Recipe
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class SearchFragment : Fragment(), RecipeAdapter.OnItemClickListener {

    private val client = OkHttpClient()
    private val baseURL: String = "http://23.236.54.96:9999/api/"
    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var recipeAdapter: RecipeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.activity_search, container, false)

        // Initialize views
        searchView = view.findViewById(R.id.search_view)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // SearchView listener
        searchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (!query.isNullOrBlank()) {
                        lifecycleScope.launch {
                            try {
                                val recipes: List<Recipe> = requestRecipes("ingredients", query)
                                recipeAdapter = RecipeAdapter(recipes, this@SearchFragment)
                                recyclerView.adapter = recipeAdapter
                            } catch (e: IOException) {
                                e.printStackTrace()
                                Log.e("Error: ", "failed to fetch recipes. {${e.message}")
                            }
                        }
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }
            }
        )

        return view
    }

    override fun onItemClick(recipe: Recipe) {
        val intent = Intent(context, DetailedActivity::class.java)
        intent.putExtra("RECIPE_NAME", recipe.name)
        intent.putExtra("RECIPE_IMAGE", recipe.previewImage)
        intent.putExtra("RECIPE_TIME", recipe.estimated_time)
        intent.putExtra("RECIPE_INGREDIENTS", recipe.ingredients)
        intent.putExtra("RECIPE_STEPS", recipe.instructions)
        startActivity(intent)
    }

    @Throws(IOException::class)
    suspend fun requestRecipes(apiEndpoint: String, requestArgument: String): List<Recipe> {
        val url = "$baseURL$apiEndpoint?$apiEndpoint=$requestArgument"
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
