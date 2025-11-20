package com.example.ecommerce.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerce.R
import com.example.ecommerce.model.Variant

class AttributeTypeAdapter(
    private var attributes: Map<String, List<String>>,
    private val variants: List<Variant>,
    private val onAttributeSelected: (String, String) -> Unit
) : RecyclerView.Adapter<AttributeTypeAdapter.AttributeViewHolder>() {

    private val selectedAttributes = mutableMapOf<String, String>()
    private var currentVariant: Variant? = null

    init {
        if (variants.isNotEmpty()) {
            currentVariant = variants[0]
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttributeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_attribute_type, parent, false)
        return AttributeViewHolder(view)
    }

    override fun onBindViewHolder(holder: AttributeViewHolder, position: Int) {
        val attributeType = attributes.keys.elementAt(position)
        val values = attributes[attributeType] ?: emptyList()
        holder.bind(attributeType, values)
    }

    override fun getItemCount(): Int = attributes.size

    private fun updateVariantInfo(variant: Variant) {
        currentVariant = variant
        notifyDataSetChanged() // Cập nhật lại toàn bộ ViewHolder
    }

    inner class AttributeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val attributeText: TextView = itemView.findViewById(R.id.tv_attribute_type)
        private val recyclerView: RecyclerView = itemView.findViewById(R.id.rcv_values)

        fun bind(attributeType: String, values: List<String>) {
            Log.d("AttributeDebug", "Binding Attribute Type: $attributeType, Values: $values")
            recyclerView.layoutManager = GridLayoutManager(recyclerView.context, 3)
            attributeText.text = attributeType

            val validValues = getValidValues(attributeType, values)
            Log.d("validValues", "validValues: $validValues")
            recyclerView.adapter = AttributeValueAdapter(
                values,
                validValues,
                selectedAttributes[attributeType]
            ) { selectedValue ->
                Log.d("AttributeDebug", "Selected Value: $selectedValue")
                if (selectedAttributes[attributeType] == selectedValue) {
                    selectedAttributes.remove(attributeType)
                } else {
                    selectedAttributes[attributeType] = selectedValue
                }
                onAttributeSelected(attributeType, selectedValue)
                notifyDataSetChanged()
            }
        }

        private fun getValidValues(attributeType: String, values: List<String>): List<String> {
            return values.filter { value ->
                val tempSelection = selectedAttributes.toMutableMap()
                tempSelection[attributeType] = value
                findVariantByAttributes(tempSelection) != null
            }
        }

        private fun findVariantByAttributes(selectedAttributes: Map<String, String>): Variant? {
            return variants.find { variant ->
                val variantAttributes = variant.attributes.associate { it.type to it.value }
                selectedAttributes.all { (type, value) -> variantAttributes[type] == value }
            }
        }


    }
}