package com.example.ecommerce.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommerce.R
import com.example.ecommerce.model.Variant

class VariantAdapter(
    private var variant: Variant // Chỉ hiển thị một variant tại một thời điểm
) : RecyclerView.Adapter<VariantAdapter.VariantViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VariantViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_variant, parent, false)
        return VariantViewHolder(view)
    }

    override fun onBindViewHolder(holder: VariantViewHolder, position: Int) {
        holder.bind(variant)
    }

    override fun getItemCount(): Int = 1 // Chỉ hiển thị một variant

    fun updateVariant(newVariant: Variant) {
        variant = newVariant
        notifyDataSetChanged()
    }

    inner class VariantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgViewVariant: ImageView = itemView.findViewById(R.id.imgVariant)
        private val tvNameVariant: TextView = itemView.findViewById(R.id.tvNameVariant)
        private val tvStockVariant: TextView = itemView.findViewById(R.id.tvStockVariant)

        fun bind(variant: Variant) {
            tvNameVariant.text = variant.name
            tvStockVariant.text = "Stock: ${variant.stock}"
            Log.d("VariantAdapter","Stock: ${variant.stock}")
            Glide.with(itemView.context)
                .load(variant.images)
                .placeholder(R.drawable.banner_placeholder)
                .into(imgViewVariant)
        }
    }
}