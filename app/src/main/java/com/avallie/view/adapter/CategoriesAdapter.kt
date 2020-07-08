package com.avallie.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.avallie.R
import com.avallie.helpers.AppHelper
import com.avallie.model.Category
import kotlinx.android.synthetic.main.category_header.view.*
import kotlinx.android.synthetic.main.category_item.view.*

typealias OnCategoryClicked = (selected: Boolean, category: Category) -> Unit

class CategoriesAdapter(private val context: Context, private val categories: ArrayList<Category>, private val selectedCategories: HashSet<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val HEADER = 0
    private val ITEM = 1
    private val LAST_ITEM = 2

    var selectedPhase: String? = null

    var onCategoryClicked: OnCategoryClicked? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == HEADER) {
            val view = LayoutInflater.from(context).inflate(R.layout.category_header, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(context).inflate(R.layout.category_item, parent, false)
            CategoryViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return categories.size + 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when {
            getItemViewType(position) == ITEM -> {
                val categoryViewHolder = holder as CategoryViewHolder

                categoryViewHolder.categoryName.text = categories[position - 1].name.toLowerCase().capitalize()
                categoryViewHolder.categoryName.isChecked = selectedCategories.contains(categories[position - 1].name)
            }
            getItemViewType(position) == HEADER -> (holder as HeaderViewHolder).selectedPhase.text = selectedPhase
            else -> {
                val categoryViewHolder = holder as CategoryViewHolder

                categoryViewHolder.view.setPadding(0, 0, 0, AppHelper.dpToPx(70, context))

                categoryViewHolder.categoryName.text = categories[position - 1].name.toLowerCase().capitalize()
                categoryViewHolder.categoryName.isChecked = selectedCategories.contains(categories[position - 1].name)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            HEADER
        } else if (position == categories.size) {
            LAST_ITEM
        } else {
            ITEM
        }
    }

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var categoryName = itemView.v_category_checkbox
        var view = itemView

        init {
            categoryName.setOnCheckedChangeListener{ buttonView, isChecked ->
                onCategoryClicked?.invoke(isChecked, categories[adapterPosition - 1])
            }
        }
    }

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var selectedPhase = itemView.v_selected_phase
    }
}