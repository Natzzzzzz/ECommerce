package com.example.ecommerce.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerce.R

class AttributeValueAdapter(
    private val allValues: List<String>,
    private val validValues: List<String>,
    private val selectedValue: String?,
    private val onValueSelected: (String) -> Unit
) : RecyclerView.Adapter<AttributeValueAdapter.ValueViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ValueViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_attribute_value, parent, false)
        return ValueViewHolder(view)
    }

    override fun onBindViewHolder(holder: ValueViewHolder, position: Int) {
        val value = allValues[position]
        holder.bind(value, validValues.contains(value), value == selectedValue)
    }

    override fun getItemCount(): Int = allValues.size

    inner class ValueViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val valueText: TextView = itemView.findViewById(R.id.tv_attribute_value)

        fun bind(value: String, isValid: Boolean, isSelected: Boolean) {
            valueText.text = value

            if (isValid) {
                valueText.alpha = 1.0f
                valueText.isEnabled = true
                valueText.setBackgroundResource(
                    if (isSelected) R.drawable.background_attribute_value_selected else R.drawable.background_attribute_value
                )
            } else {
                valueText.alpha = 0.5f
                valueText.isEnabled = false
                valueText.setBackgroundResource(R.drawable.background_attribute_value)
            }

            valueText.setOnClickListener {
                if (isValid) {
                    onValueSelected(value)
                }
            }
        }
    }
}