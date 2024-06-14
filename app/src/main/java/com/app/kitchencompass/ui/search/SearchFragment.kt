package com.app.kitchencompass.ui.search

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
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
    private val baseURL: String = "http://23.236.54.96:9999/api"
    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var filterButton: ImageButton
    private var currentFilter = "name"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.activity_search, container, false)

        // Initialize views
        searchView = view.findViewById(R.id.search_view)
        recyclerView = view.findViewById(R.id.recycler_view)
        filterButton = view.findViewById(R.id.filter_button)

        recyclerView.layoutManager = LinearLayoutManager(context)

        // Set up PopupMenu for filter options
        filterButton.setOnClickListener { showFilterPopup(it) }

        // SearchView listener
        searchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (!query.isNullOrBlank()) {
                        lifecycleScope.launch {
                            try {
                                val recipes: List<Recipe> = requestRecipes(currentFilter, query)
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

    private fun showFilterPopup(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.filter_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            currentFilter = when (menuItem.itemId) {
                R.id.filter_name -> "name"
                R.id.filter_ingredients -> "ingredients"
                R.id.filter_instructions -> "instructions"
                else -> "name" // Default case
            }
            searchView.queryHint = "Search by $currentFilter"
            true
        }

        popupMenu.show()
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

    @Throws(IOException::class)
    suspend fun requestRecipes(apiEndpoint: String, requestArgument: String): List<Recipe> {
        val url = "$baseURL/recipe?$apiEndpoint=$requestArgument"
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
