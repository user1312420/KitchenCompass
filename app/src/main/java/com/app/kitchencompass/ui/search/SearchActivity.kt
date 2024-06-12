package com.app.kitchencompass.ui.search

import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
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

class SearchActivity : ComponentActivity() {
    private val client = OkHttpClient()
    private val baseURL: String = "http://34.134.3.190:9999/api/"
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchView = findViewById(R.id.search_view)

        //searchview listener
        searchView.setOnQueryTextListener(
            object: SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if(!query.isNullOrBlank()){
                        lifecycleScope.launch {
                            try {
                                val recipes: List<Recipe> = requestRecipes("ingredients", query)
                                recipes.forEach {
                                    Log.d("requestLog", "name: ${it.name}, ingredients: ${it.ingredients}")
                                }
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
    }

    @Throws(IOException::class)
    suspend fun requestRecipes(apiEndpoint: String, requestArgument: String): List<Recipe>{

        //http://35.226.107.39/api/ingredients?ingredients=Salz
        val url = "$baseURL$apiEndpoint?$apiEndpoint=$requestArgument"

        val request = Request.Builder()
            .url(url)
            .build()

        return withContext(Dispatchers.IO) {
            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string() ?: throw IOException("Unexpected empty response body")
                val recipeType = object : TypeToken<List<Recipe>>() {}.type
                Gson().fromJson(responseBody, recipeType)
            }
        }
    }
}