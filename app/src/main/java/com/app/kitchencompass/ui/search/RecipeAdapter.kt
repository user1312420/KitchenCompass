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

class RecipeAdapter(private val recipes: List<Recipe>, private val itemClickListener: OnItemClickListener) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(recipe: Recipe)
    }

    class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //zeigt Rezept Name
        val recipeName: TextView = itemView.findViewById(R.id.recipeName)
        //zeigt Rezept bild
        val previewImage: ImageView = itemView.findViewById(R.id.previewImage)
        //zeigt Zeit
        val estimatedTime: TextView = itemView.findViewById(R.id.estimated_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe_card, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        //rezept an aktueller pos. aufrufen
        val recipe = recipes[position]
        //lädt bild
        Picasso.get().load(recipe.previewImage).into(holder.previewImage)
        //setzt Zeit
        holder.estimatedTime.text = recipe.estimated_time
        //setzt Name
        holder.recipeName.text = recipe.name

        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(recipe)
        }
    }

    override fun getItemCount(): Int {
        return recipes.size
    }
}
