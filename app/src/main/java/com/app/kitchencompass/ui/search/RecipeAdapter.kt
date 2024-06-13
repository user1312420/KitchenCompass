// RecipeAdapter.kt
package com.app.kitchencompass.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.kitchencompass.R
import com.app.kitchencompass.Recipe
import com.squareup.picasso.Picasso

class RecipeAdapter(private val recipes: List<Recipe>) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val previewImage: ImageView = itemView.findViewById(R.id.previewImage)
        val estimatedTime: TextView = itemView.findViewById(R.id.estimated_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe_card, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]
        Picasso.get().load(recipe.previewImage).into(holder.previewImage)
        holder.estimatedTime.text = recipe.estimated_time
    }

    override fun getItemCount(): Int {
        return recipes.size
    }
}
