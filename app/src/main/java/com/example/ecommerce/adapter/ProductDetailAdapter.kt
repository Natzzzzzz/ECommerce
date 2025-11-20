package com.example.ecommerce.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommerce.R

class ProductDetailAdapter(
    private var imageUrls: List<String>,
    private val onItemClick: (String) -> Unit // Callback trả về URL của ảnh được chọn
) : RecyclerView.Adapter<ProductDetailAdapter.ImageViewHolder>() {

    private var imageList: List<String> = emptyList()

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewProduct: ImageView = itemView.findViewById(R.id.imageViewProduct)

        fun bind(imageUrl: String) {
            Glide.with(itemView.context)
                .load(imageUrl)
                .placeholder(R.drawable.banner_placeholder)
                .into(imageViewProduct)

            itemView.setOnClickListener {
                onItemClick(imageUrl) // Gọi callback khi click vào ảnh, truyền URL
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image_pattern_layout, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(imageList[position])
    }

    override fun getItemCount(): Int = imageList.size

    fun setData(newList: List<String>) {
        imageList = newList
        notifyDataSetChanged()
    }
}