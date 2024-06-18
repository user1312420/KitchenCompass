package com.app.kitchencompass.ui.search

import android.content.Context
import android.content.Intent
import android.health.connect.datatypes.units.Length
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.kitchencompass.DetailedActivity
import com.app.kitchencompass.R
import com.app.kitchencompass.Recipe
import com.google.android.material.internal.ViewUtils.hideKeyboard
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
    private val baseURL: String = "http://34.67.0.113:9999/api"
    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var filterButton: ImageButton
    private var currentFilter = "name"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.activity_search, container, false)

        //initialize views
        searchView = view.findViewById(R.id.search_view)
        recyclerView = view.findViewById(R.id.recycler_view)
        filterButton = view.findViewById(R.id.filter_button)

        recyclerView.layoutManager = LinearLayoutManager(context)

        //PopupMenu für filter options
        filterButton.setOnClickListener { showFilterPopup(it) }

        //SearchView listener
        searchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (!query.isNullOrBlank()) {
                        //start coroutine
                        lifecycleScope.launch {
                            try {
                                val arguments: List<String> = query.split(' ')
                                val recipes: List<Recipe> = requestRecipes(currentFilter, arguments)
                                Toast.makeText(context, "${recipes.size} Rezepte gefunden!", Toast.LENGTH_LONG).show()
                                recipeAdapter = RecipeAdapter(recipes, this@SearchFragment)
                                recyclerView.adapter = recipeAdapter
                                val inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                                inputMethodManager?.hideSoftInputFromWindow(view?.windowToken, 0)
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
        val intent = Intent(context, DetailedActivity::class.java)
        intent.putExtra("RECIPE_NAME", recipe.name)
        intent.putExtra("RECIPE_IMAGE", recipe.previewImage)
        intent.putExtra("RECIPE_TIME", recipe.estimated_time)
        intent.putExtra("RECIPE_INGREDIENTS", recipe.ingredients)
        intent.putExtra("RECIPE_STEPS", recipe.instructions)
        startActivity(intent)
    }

    @Throws(IOException::class)
    suspend fun requestRecipes(filter: String, requestArguments: List<String>): List<Recipe> {
        var url = "$baseURL/recipes?arguments="
        for (r in requestArguments) {
            url += "$r,"
        }
        url = url.trimEnd(',')

        val request = Request.Builder().url(url).build()

        return withContext(Dispatchers.IO) {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                val responseBody = response.body?.string() ?: throw IOException("Unexpected empty response body")
                val recipeType = object : TypeToken<List<Recipe>>() {}.type
                Gson().fromJson(responseBody, recipeType)
            }
        }
    }
}
