package com.example.ecommerce.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommerce.R
import drawable.home_page.ProductsByCategory
import com.example.ecommerce.model.Category

class CategoriesAdapter(private val onItemClick: (Category) -> Unit) :
    RecyclerView.Adapter<CategoriesAdapter.ImageViewHolder>() {

    private var categoryList: List<Category> = emptyList()

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imgViewCategory)
        private val title: TextView = itemView.findViewById(R.id.txtCategoryName)

        fun bind(category: Category) {
            Glide.with(itemView.context) // Load ảnh từ URL
                .load(category.icon)
                .placeholder(R.drawable.banner_placeholder) // Ảnh chờ khi tải
                .into(imageView)
//            Log.d("CategoriesAdapter", "Icon URL: ${category.icon}")
//            Log.d("CategoriesAdapter", "icon id: ${category._id}")

            title.text = category.name
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, ProductsByCategory::class.java)
                intent.putExtra("CATEGORIES_DATA", category)
                itemView.context.startActivity(intent)
            }


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_categories_layout, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(categoryList[position])
    }

    override fun getItemCount(): Int = categoryList.size

    fun setData(newList: List<Category>) {
        categoryList = newList
        notifyDataSetChanged()
    }
}
