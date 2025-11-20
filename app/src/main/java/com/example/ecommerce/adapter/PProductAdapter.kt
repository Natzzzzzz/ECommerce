import android.content.Context
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
import com.example.ecommerce.features.auth.LoginActivity
import com.example.ecommerce.features.cart.CartViewModel
import com.example.ecommerce.features.product_detail.ProductDetail
import com.example.ecommerce.utils.TokenManager
import com.example.ecommerce.model.Product
import com.example.ecommerce.viewModel.WishlistViewModel

class PProductAdapter(
    private val context: Context,
    private val cartViewModel: CartViewModel,
    private val wishlistViewModel: WishlistViewModel,
    private val onItemClick: (Product) -> Unit
) : RecyclerView.Adapter<PProductAdapter.ImageViewHolder>() {

    private val productList: ArrayList<Product> = arrayListOf()
    private var wishlistStatus: MutableMap<String, Boolean> = mutableMapOf()

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewProduct: ImageView = itemView.findViewById(R.id.imageViewProduct)
        private val name: TextView = itemView.findViewById(R.id.txtProductName)
        private val brand: TextView = itemView.findViewById(R.id.txtBrandName)
        private val rating: TextView = itemView.findViewById(R.id.txtRating)
        private val quantitySold: TextView = itemView.findViewById(R.id.txtQuantitySold)
        private val originalPrice: TextView = itemView.findViewById(R.id.txtoriginalPrice)
        private val sellingPrice: TextView = itemView.findViewById(R.id.txtsellingPrice)
        private val favImage: ImageView = itemView.findViewById(R.id.imageViewFav)
        private val btnCart: ImageView = itemView.findViewById(R.id.btnCart)

        fun bind(productItem: Product) {
            Glide.with(itemView.context)
                .load(productItem.thumbnail)
                .placeholder(R.drawable.banner_placeholder)
                .into(imageViewProduct)

            name.text = productItem.name
            brand.text = productItem.brandName
            rating.text = productItem.rating.toString()
            quantitySold.text = productItem.quantitySold.toString()
            originalPrice.text = productItem.originalPrice.toString()
            sellingPrice.text = productItem.sellingPrice.toString()
            originalPrice.paintFlags = originalPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG


            // Kiểm tra trạng thái yêu thích từ danh sách wishlistStatus
            val isFavorite = wishlistStatus[productItem.id] ?: false
            updateFavoriteIcon(favImage, isFavorite)

            // Xử lý sự kiện click vào nút yêu thích
            favImage.setOnClickListener {
                if (TokenManager.getToken(context) == null) {
                    navigateToLogin()
                } else {
                    val token = TokenManager.getToken(context) ?: ""
                    val newStatus = !(wishlistStatus[productItem.id] ?: false)
                    wishlistViewModel.addToWishlist(productItem.id, newStatus, token)

                    // Cập nhật giao diện ngay lập tức
                    wishlistStatus[productItem.id] = newStatus
                    updateFavoriteIcon(favImage, newStatus)
                }
            }
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, ProductDetail::class.java)
                intent.putExtra("PRODUCT_ID", productItem.id)
                itemView.context.startActivity(intent)
            }

            btnCart.setOnClickListener {
                val token = TokenManager.getToken(context)
                if (token == null) {
                    navigateToLogin()
                } else {
                    Log.d("PProductAdapter", "Product variant id: ${productItem.variantId}")
                    cartViewModel.addToCart(token,productItem.variantId, 1)
                }
            }

        }

        private fun updateFavoriteIcon(imageView: ImageView, isFavorite: Boolean) {
            if (isFavorite) {
                imageView.setImageResource(R.drawable.baseline_favorite_red_24)
            } else {
                imageView.setImageResource(R.drawable.baseline_favorite_border_dark_24)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product_layout, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(productList[position])

        holder.itemView.post {
            val parentRecyclerView = holder.itemView.parent as? RecyclerView ?: return@post
            if (parentRecyclerView.id == R.id.rcvAllProduct || parentRecyclerView.id == R.id.rcvProductByCategory || parentRecyclerView.id == R.id.rcvSearchProduct || parentRecyclerView.id == R.id.rcvSimilarItem) {
                val layoutParams = holder.imageViewProduct.layoutParams ?: return@post
                val context = holder.itemView.context

                layoutParams.width = (190 * context.resources.displayMetrics.density).toInt()
                layoutParams.height = (190 * context.resources.displayMetrics.density).toInt()

                holder.imageViewProduct.layoutParams = layoutParams
                holder.imageViewProduct.requestLayout()
                holder.imageViewProduct.scaleType = ImageView.ScaleType.FIT_XY
            }
        }

    }

    override fun getItemCount(): Int = productList.size

    fun setData(newProducts: List<Product>) {
        productList.clear()
        productList.addAll(newProducts)
        notifyDataSetChanged()
    }

    fun addData(newProducts: List<Product>) {
        val startPosition = productList.size
        productList.addAll(newProducts)
        notifyItemRangeInserted(startPosition, newProducts.size)
    }

    fun updateWishlistStatus(wishlistItems: Set<String>) {
        wishlistStatus.clear()
        productList.forEach { product ->
            wishlistStatus[product.id] = wishlistItems.contains(product.id)
        }
        notifyDataSetChanged() // Cập nhật lại giao diện
    }

    private fun navigateToLogin() {
        val intent = Intent(context, LoginActivity::class.java)
        context.startActivity(intent)
    }

}


