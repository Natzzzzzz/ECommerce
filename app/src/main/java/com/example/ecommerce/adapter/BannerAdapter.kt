package com.coding.imagesliderwithdotindicatorviewpager2.adapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import drawable.home_page.ProductListActivity
import com.example.ecommerce.R
import com.example.ecommerce.model.ImageItem

class BannerAdapter(private val onItemClick: (ImageItem) -> Unit) :
    RecyclerView.Adapter<BannerAdapter.ImageViewHolder>() {

    private var imageList: List<ImageItem> = emptyList()

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageViewBanner)
        private val title: TextView = itemView.findViewById(R.id.textViewTitle)
        private val discount: TextView = itemView.findViewById(R.id.textViewSubtitle)

        fun bind(imageItem: ImageItem) {
            Glide.with(itemView.context) // Load ảnh từ URL
                .load(imageItem.image)
                .placeholder(R.drawable.banner_placeholder) // Ảnh chờ khi tải
                .into(imageView)
//            Log.d("CategoriesAdapter", "image: ${imageItem.image}")
            title.text = imageItem.title
            discount.text = imageItem.discountInfo
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, ProductListActivity::class.java)
                intent.putExtra("ITEM_DATA", imageItem)
                itemView.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image_layout, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(imageList[position])
    }

    override fun getItemCount(): Int = imageList.size

    fun setData(newList: List<ImageItem>) {
        imageList = newList
        notifyDataSetChanged()
    }
}
