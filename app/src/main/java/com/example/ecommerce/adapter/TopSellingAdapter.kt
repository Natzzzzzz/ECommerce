package com.coding.imagesliderwithdotindicatorviewpager2.adapters

import android.content.Intent
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommerce.R
import com.example.ecommerce.features.product_detail.ProductDetail
import com.example.ecommerce.model.Product

class TopSellingAdapter(
    private  var product: List<Product>,
    private val onItemClick: (Product) -> Unit) :
    RecyclerView.Adapter<TopSellingAdapter.ImageViewHolder>() {

    private var productList: List<Product> = emptyList()

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.topProductImage)
        private val name: TextView = itemView.findViewById(R.id.txtTopProductname)
        private val rating: TextView = itemView.findViewById(R.id.txtRating)
        private val quantitySold: TextView = itemView.findViewById(R.id.txtQuantitySold)
        private val sellingPrice: TextView = itemView.findViewById(R.id.txtsellingPrice)



        fun bind(productItem: Product) {
            Glide.with(itemView.context) // Load ảnh từ URL
                .load(productItem.thumbnail)
                .placeholder(R.drawable.banner_placeholder) // Ảnh chờ khi tải
                .into(imageView)
            Log.d("productAdapter", "Icon URL: ${productItem.thumbnail}")
            Log.d("productAdapter", "icon name: ${productItem.name}")
            name.text = productItem.name
            rating.text = productItem.rating.toString()
            quantitySold.text = productItem.quantitySold.toString()
            sellingPrice.text = productItem.sellingPrice.toString()
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, ProductDetail::class.java)
                intent.putExtra("PRODUCT_ID", productItem.id)
                itemView.context.startActivity(intent)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_top_sell_layout, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(productList[position])
    }

    override fun getItemCount(): Int = productList.size

    fun setData(newList: List<Product>) {
        productList = newList
        notifyDataSetChanged()
    }
}
