package com.example.ecommerce.features.product_detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.coding.imagesliderwithdotindicatorviewpager2.adapters.ImagePDAdapter
import com.example.ecommerce.R

class ImageDetailActivity : AppCompatActivity() {
    private lateinit var viewPager2: ViewPager2
    private lateinit var backButton: ImageButton
    private var currentPage = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_detail)
        viewPager2 = findViewById(R.id.viewpager2)
        backButton = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        val imageUrls = intent.getStringArrayListExtra("PRODUCT_ITEM_DATA") ?: arrayListOf()
        Log.d("ImageDetailActivity", "Danh sách ảnh: $imageUrls")

        val adapter = ImagePDAdapter(onItemClick = { product ->
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show()
        },imageUrls)
        viewPager2.adapter = adapter
        adapter.notifyDataSetChanged()

        setupDotsIndicator(imageUrls.size)
    }

    private fun setupDotsIndicator(size: Int) {
        val slideDotLL = findViewById<LinearLayout>(R.id.slideDotLL)
        slideDotLL?.removeAllViews()

        val dotsImage = Array(size) { ImageView(this) }
        dotsImage.forEach {
            it.setImageResource(R.drawable.non_active_dot)
            slideDotLL?.addView(it, LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(8, 0, 8, 0) })
        }

        if (dotsImage.isNotEmpty()) {
            dotsImage[0].setImageResource(R.drawable.active_dot)
        }

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                currentPage = position
                dotsImage.forEachIndexed { index, imageView ->
                    imageView.setImageResource(
                        if (position == index) R.drawable.active_dot else R.drawable.non_active_dot
                    )
                }
            }
        })
    }
}