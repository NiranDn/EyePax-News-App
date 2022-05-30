package com.eyepax_news_app.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.eyepax_news_app.R
import com.eyepax_news_app.model.Category
import kotlinx.android.synthetic.main.adapter_category.view.*

class CategoryAdapter : RecyclerView.Adapter<CategoryAdapter.FilterViewHolder>() {

    private var onItemClickListener: ((Category) -> Unit)? = null
    var selectedCategory: String = "business"

    inner class FilterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    fun setOnItemClickListener(listener: (Category) -> Unit) {
        onItemClickListener = listener
    }

    private val differCallback = object : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldCategory: Category, newCategory: Category): Boolean {
            return oldCategory.categoryId == newCategory.categoryId
        }

        override fun areContentsTheSame(oldCategory: Category, newCategory: Category): Boolean {
            return oldCategory == newCategory
        }
    }

    var categoryies = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        return FilterViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.adapter_category,
                parent,
                false
            )
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        val category = categoryies.currentList[position]

        holder.itemView.apply {
            categoryTv.text = category.categoryName
            if(category.isSelected) {
                categoryTv.isChecked = category.isSelected
                category.isSelected = !category.isSelected
                categoryTv.setTextColor(ContextCompat.getColor(context, R.color.white))
                selectedCategory = category.categoryName
            } else {
                categoryTv.setTextColor(ContextCompat.getColor(context, R.color.font_black))
                categoryTv.isChecked = category.isSelected
            }

            setOnClickListener {
                onItemClickListener?.let {
                    categoryies.currentList.forEach {
                        it.isSelected = false
                    }
                    category.isSelected = true
                    selectedCategory = categoryTv.text.toString().lowercase()
                    it(category)

                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return categoryies.currentList.size
    }
}