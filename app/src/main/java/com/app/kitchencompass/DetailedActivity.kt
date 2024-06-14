package com.app.kitchencompass

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

class DetailedActivity : AppCompatActivity() {

    private lateinit var closeButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed) // Setze das Layout für diese Aktivität

        closeButton = findViewById(R.id.close)
        // Initialisiere die Views
        val detailName: TextView = findViewById(R.id.detailName)
        val detailImage: ImageView = findViewById(R.id.detailImage)
        val detailTime: TextView = findViewById(R.id.detailTime)
        val detailIngredients: TextView = findViewById(R.id.detailIngredients)
        val detailDesc: TextView = findViewById(R.id.detailDesc)

        // Daten aus dem Intent abrufen und die Views befüllen
        val intent = intent
        detailName.text = intent.getStringExtra("RECIPE_NAME")
        val imageUrl = intent.getStringExtra("RECIPE_IMAGE")
        Picasso.get().load(imageUrl).into(detailImage)
        detailTime.text = intent.getStringExtra("RECIPE_TIME")
        detailIngredients.text = intent.getStringExtra("RECIPE_INGREDIENTS")
        detailDesc.text = intent.getStringExtra("RECIPE_STEPS")
        
        closeButton.setOnClickListener {
                finish()
        }
    }

  
}
