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
    private var isStarred = false
    private lateinit var myDB: MyDatebaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed) // Setze das Layout für diese Aktivität

        closeButton = findViewById(R.id.close)
        myDB = MyDatebaseHelper(this)

        // Initialisiere die Views
        val detailName: TextView = findViewById(R.id.detailName)
        val detailImage: ImageView = findViewById(R.id.detailImage)
        val detailTime: TextView = findViewById(R.id.detailTime)
        val detailIngredients: TextView = findViewById(R.id.detailIngredients)
        val detailDesc: TextView = findViewById(R.id.detailDesc)
        val starButton: ImageButton = findViewById(R.id.starButton)

        // Daten aus dem Intent abrufen und die Views befüllen
        val intent = intent
        detailName.text = intent.getStringExtra("RECIPE_NAME")
        val imageUrl = intent.getStringExtra("RECIPE_IMAGE")
        Picasso.get().load(imageUrl).into(detailImage)
        detailTime.text = intent.getStringExtra("RECIPE_TIME")

        // INGREDIENTS formatieren und in die TextView einfügen
        val ingredients = intent.getStringExtra("RECIPE_INGREDIENTS")
        val formattedIngredients = formatRecipeIngredients(ingredients)
        detailIngredients.text = formattedIngredients

        // Rezept-Schritte formatieren und in die TextView einfügen
        val steps = intent.getStringExtra("RECIPE_STEPS")
        val formattedSteps = formatRecipeSteps(steps)
        detailDesc.text = formattedSteps

        closeButton.setOnClickListener {
            finish()
        }

        starButton.setOnClickListener {
            if (isStarred) {
                starButton.setColorFilter(resources.getColor(R.color.white))
            } else {
                starButton.setColorFilter(Color.YELLOW)

                // Daten in die Datenbank einfügen
                myDB.addFavorites(
                    detailName.text as String?, formattedIngredients,
                    detailTime.text as String?, formattedSteps, imageUrl)
            }
        }
    }

    // Funktion zum Formatieren der Rezept-Zutaten
    private fun formatRecipeIngredients(ingredients: String?): String {
        if (ingredients.isNullOrEmpty()) return ""

        val formatted = ingredients.replace(", ", ",\n")
        return formatted
    }

    // Funktion zum Formatieren der Rezept-Schritte
    private fun formatRecipeSteps(steps: String?): String {
        if (steps.isNullOrEmpty()) return ""

        val lines = steps.split("\\d+\\.".toRegex()) // Split nach Zahlen mit Punkt
            .filter { it.isNotBlank() }
            .joinToString("\n\n") { it.trim() } // Füge Zeilenumbrüche ein

        return lines
    }
}
