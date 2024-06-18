package com.app.kitchencompass

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso

class DetailedActivity : AppCompatActivity() {

    private lateinit var closeButton: Button
    private lateinit var myDB: MyDatebaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed)

        closeButton = findViewById(R.id.close)
        myDB = MyDatebaseHelper(this)

        //initialisiere Views
        val detailName: TextView = findViewById(R.id.detailName)
        val detailImage: ImageView = findViewById(R.id.detailImage)
        val detailTime: TextView = findViewById(R.id.detailTime)
        val detailIngredients: TextView = findViewById(R.id.detailIngredients)
        val detailDesc: TextView = findViewById(R.id.detailDesc)
        val starButton: ImageButton = findViewById(R.id.starButton)

        val detailID: Int = intent.getIntExtra("RECIPE_ID", 0)

        detailName.text = intent.getStringExtra("RECIPE_NAME")
        val imageUrl = intent.getStringExtra("RECIPE_IMAGE")
        Picasso.get().load(imageUrl).into(detailImage)
        detailTime.text = intent.getStringExtra("RECIPE_TIME")

        val ingredients = intent.getStringExtra("RECIPE_INGREDIENTS")
        val formattedIngredients = formatRecipeIngredients(ingredients)
        detailIngredients.text = formattedIngredients

        val steps = intent.getStringExtra("RECIPE_STEPS")
        val formattedSteps = formatRecipeSteps(steps)
        detailDesc.text = formattedSteps

        closeButton.setOnClickListener {
            finish()
        }

        val recipes: List<Recipe> = myDB.getAllRecipes()
        val recipesNames: List<String> = recipes.map { it.name }

        if(recipesNames.contains(detailName.text)){
            starButton.setColorFilter(Color.YELLOW)
        } else {
            starButton.setColorFilter(resources.getColor(R.color.white))
        }


        starButton.setOnClickListener {
            if (recipesNames.contains(detailName.text)) {
                starButton.setColorFilter(resources.getColor(R.color.white))
                myDB.deleteRecipe(detailID)
            } else {
                starButton.setColorFilter(Color.YELLOW)
                myDB.addFavorites(
                    detailName.text as String?, formattedIngredients,
                    detailTime.text as String?, formattedSteps, imageUrl)
            }
        }
    }

    //zum Formatieren der Rezept-Zutaten
    private fun formatRecipeIngredients(ingredients: String?): String {
        if (ingredients.isNullOrEmpty()) return ""

        val formatted = ingredients.replace(", ", ",\n")
        return formatted
    }

    //zum Formatieren der Rezept-Schritte
    private fun formatRecipeSteps(steps: String?): String {
        if (steps.isNullOrEmpty()) return ""

        val lines = steps.split("\\d+\\.".toRegex()) // Split nach Zahlen mit Punkt
            .filter { it.isNotBlank() }
            .joinToString("\n\n") { it.trim() } // Füge Zeilenumbrüche ein

        return lines
    }
}
