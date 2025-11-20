package drawable.home_page

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.ecommerce.R
import com.example.ecommerce.model.ImageItem

class ProductListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_list)

        val textViewTitle = findViewById<TextView>(R.id.textViewTitleDetail)
        val textViewDiscount = findViewById<TextView>(R.id.textViewSubtitleDetail)
        val textViewDescription = findViewById<TextView>(R.id.textViewDescriptionDetail)
        val imageViewBanner = findViewById<ImageView>(R.id.imageViewBannerDetail)
        val backButton = findViewById<ImageView>(R.id.backButton)

        // Xử lý nút quay lại
        backButton.setOnClickListener {
            finish()
        }

        // Nhận đối tượng ImageItem từ Intent
        val selectedItem: ImageItem? = intent.getParcelableExtra("ITEM_DATA") as? ImageItem

        selectedItem?.let {
            textViewTitle.text = it.title
            textViewDiscount.text = it.discountInfo
            textViewDescription.text = it.description

            // Load ảnh từ URL bằng Glide
            Glide.with(this)
                .load(it.image)
                .placeholder(R.drawable.banner_placeholder)
                .into(imageViewBanner)
        }
    }
}
