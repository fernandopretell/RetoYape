package com.fulbiopretell.retoyape.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.fulbiopretell.retoyape.core.extensions.loadUrl
import com.fulbiopretell.retoyape.databinding.ItemRepiceBinding
import com.fulbiopretell.retoyape.model.Recipe
import java.util.ArrayList

class RecipesAdapter(
    val listener: RecipesAdapterListener
) : RecyclerView.Adapter<RecipesAdapter.ViewHolder>() {

    val values  = arrayListOf<Recipe>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(ItemRepiceBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.ivImage.loadUrl(item.image)
        holder.tvName.text = item.name
        holder.tvDescription.text = item.description

        holder.clContainer.setOnClickListener {
            listener.goDetail(item)
        }
    }

    fun updateData(newItems: List<Recipe>){
        values.clear()
        values.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: ItemRepiceBinding) : RecyclerView.ViewHolder(binding.root) {
        val ivImage: ImageView = binding.ivImage
        val tvName: TextView = binding.tvName
        val tvDescription: TextView = binding.tvDescription
        val clContainer: ConstraintLayout = binding.clContainer
    }

    interface RecipesAdapterListener{
        fun goDetail(item: Recipe)
    }
}