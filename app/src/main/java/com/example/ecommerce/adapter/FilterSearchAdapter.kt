package com.example.ecommerce.adapter

import android.content.Intent
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommerce.R
import com.example.ecommerce.model.Product
import com.example.ecommerce.model.Suggestion

class FilterSearchAdapter(private val onItemClick: (Suggestion) -> Unit) :
    RecyclerView.Adapter<FilterSearchAdapter.ImageViewHolder>() {

    private var productList: List<Suggestion> = emptyList()
    private var keyword: String = "" // Tạo biến để lưu từ khóa

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productName: TextView = itemView.findViewById(R.id.txtProductName)

        fun bind(item: Suggestion) {
            productName.text = highlightKeyword(item.keyword, keyword)
            itemView.setOnClickListener { onItemClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product_filter_search_layout, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(productList[position])
    }

    override fun getItemCount(): Int = productList.size

    fun setData(newList: List<Suggestion>, searchKeyword: String) {
        productList = newList
        keyword = searchKeyword
        notifyDataSetChanged()
    }

    private fun highlightKeyword(text: String, keyword: String): SpannableString {
        val spannable = SpannableString(text)
        val index = text.lowercase().indexOf(keyword.lowercase()) // Tìm vị trí từ khóa không phân biệt hoa thường
        if (index != -1) {
            spannable.setSpan(
                StyleSpan(Typeface.BOLD), // In đậm
                index, index + keyword.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        return spannable
    }
}
