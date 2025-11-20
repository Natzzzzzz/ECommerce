package drawable.home_page

import PProductAdapter
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerce.R
import com.example.ecommerce.features.cart.CartRepository
import com.example.ecommerce.features.cart.CartViewModel
import com.example.ecommerce.model.Category
import com.example.ecommerce.features.home_page.HomepageViewModel
import com.example.ecommerce.features.home_page.HomepageViewModelFactory
import com.example.ecommerce.features.wishlist.WishlistRepository
import com.example.ecommerce.utils.TokenManager
import com.example.ecommerce.viewModel.CartViewModelFactory
import com.example.ecommerce.viewModel.WishlistViewModel
import com.example.ecommerce.viewModel.WishlistViewModelFactory

class ProductsByCategory : AppCompatActivity() {

    private lateinit var adapter: PProductAdapter
    private lateinit var viewModelHome: HomepageViewModel
    private lateinit var viewModelCart: CartViewModel
    private lateinit var viewModelWishlist: WishlistViewModel
    private lateinit var rcvProductsByCategory: RecyclerView
    private var isLoading = false
    private var currentPage = 1
    private val limit = 6

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_detail)

        viewModelHome = ViewModelProvider(
            this,
            HomepageViewModelFactory(HomepageRepository())
        )[HomepageViewModel::class.java]

        viewModelCart= ViewModelProvider(
            this,
            CartViewModelFactory(CartRepository())
        )[CartViewModel::class.java]

        viewModelWishlist= ViewModelProvider(
            this,
            WishlistViewModelFactory(WishlistRepository())
        )[WishlistViewModel::class.java]


        val textViewCategoryName = findViewById<TextView>(R.id.txtCategoryName)
        val backButton = findViewById<ImageView>(R.id.backButton)
        rcvProductsByCategory = findViewById(R.id.rcvProductByCategory)

        backButton.setOnClickListener { finish() }

        adapter = PProductAdapter(this, viewModelCart, viewModelWishlist) { product ->
            showToast("Clicked: ${product.name}")
        }

        rcvProductsByCategory.layoutManager = GridLayoutManager(this, 2)
        rcvProductsByCategory.adapter = adapter

        val selectedItem: Category? = intent.getParcelableExtra("CATEGORIES_DATA") as? Category
        selectedItem?.let {
            textViewCategoryName.text = it.name
            fetchProducts(it._id, currentPage)
        }

        // Lấy token và tải wishlist ban đầu
        val token = TokenManager.getToken(this)
        if (token != null) {
            viewModelWishlist.getWishlist(token) // Tải danh sách yêu thích ngay khi khởi tạo
        }

        // Quan sát danh sách yêu thích
        viewModelWishlist.wishlistItems.observe(this) { wishlistItems ->
            val favProductIds = wishlistItems.filter { it.status }.map { it._id }.toSet()
            (rcvProductsByCategory.adapter as? PProductAdapter)?.updateWishlistStatus(favProductIds)
        }
        // Quan sát kết quả thêm vào wishlist
        viewModelWishlist.addToWishlistResult.observe(this) { result ->
            result.onSuccess { message ->
                Toast.makeText(this, message ?: "Added to wishlist", Toast.LENGTH_SHORT).show()
                val token = TokenManager.getToken(this)
                if (token != null) {
                    viewModelWishlist.getWishlist(token)
                }
            }.onFailure { error ->
                Toast.makeText(this, "Failed: ${error.message}", Toast.LENGTH_LONG).show()
                Log.e("WishlistViewModel", "Failed to add to wishlist: ${error.message}")
            }
        }

        // Quan sát kết quả thêm vào giỏ hàng
        viewModelCart.addToCartResult.observe(this) { result ->
            result.onSuccess { response ->
                Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                val token = TokenManager.getToken(this)
                if (token != null) {
                    viewModelCart.getCartQuantity(token)
                }
            }.onFailure { error ->
                Toast.makeText(this, "Failed: ${error.message}", Toast.LENGTH_LONG).show()
                Log.e("CartViewModel", "Failed to add to cart: ${error.message}")
            }
        }

        // Thêm sự kiện ScrollListener để load thêm sản phẩm
        rcvProductsByCategory.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                if (!isLoading && lastVisibleItemPosition + 1 >= totalItemCount) {
                    selectedItem?.let {
                        isLoading = true
                        currentPage++
                        fetchProducts(it._id, currentPage)
                    }
                }
            }
        })

        // Quan sát dữ liệu từ ViewModel
        viewModelHome.productListByCategory.observe(this, Observer { products ->
            adapter.addData(products) // Thêm dữ liệu mới thay vì set lại toàn bộ
            isLoading = false
        })
        viewModelCart.addToCartResult.observe(this) { result ->
            result.onSuccess { response ->
                Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()

            }.onFailure { error ->
                Toast.makeText(this, "Failed: ${error.message}", Toast.LENGTH_LONG).show()
                Log.e("CartViewModel", "Failed to add to cart: ${error.message}")
            }
        }
    }

    private fun fetchProducts(categoryId: String, page: Int) {
        viewModelHome.fetchProductsByCategory(categoryId, page, limit)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

