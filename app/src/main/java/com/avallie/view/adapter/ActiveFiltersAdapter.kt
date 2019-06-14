package com.avallie.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.avallie.R
import kotlinx.android.synthetic.main.active_filter_item.view.*

typealias OnCategoryDeleted = (category: String) -> Unit

class ActiveFiltersAdapter(private val context: Context, private val categories: List<String>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onCategoryDeleted: OnCategoryDeleted? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.active_filter_item, parent, false)
        return ActiveFilterViewHolder(view)
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as ActiveFilterViewHolder

        holder.categoryName.text = categories[position]
    }

    inner class ActiveFilterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val categoryName: TextView
        val deleteCategory: ImageView

        init {
            categoryName = itemView.v_active_filter_category_name
            deleteCategory = itemView.v_delete_category

            deleteCategory.setOnClickListener {
                onCategoryDeleted?.invoke(categories[adapterPosition])
            }
        }


    }
}