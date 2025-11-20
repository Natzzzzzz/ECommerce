package drawable.home_page

import PProductAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.compose.runtime.key
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerce.R
import com.example.ecommerce.adapter.FilterSearchAdapter
import com.example.ecommerce.features.cart.CartRepository
import com.example.ecommerce.features.cart.CartViewModel
import com.example.ecommerce.features.home_page.HomepageViewModel
import com.example.ecommerce.features.home_page.HomepageViewModelFactory
import com.example.ecommerce.features.wishlist.WishlistRepository
import com.example.ecommerce.model.Category
import com.example.ecommerce.model.Product
import com.example.ecommerce.model.Suggestion
import com.example.ecommerce.utils.TokenManager
import com.example.ecommerce.viewModel.CartViewModelFactory
import com.example.ecommerce.viewModel.WishlistViewModel
import com.example.ecommerce.viewModel.WishlistViewModelFactory
import kotlinx.coroutines.launch

class SearchProductActivity : AppCompatActivity() {
    private lateinit var rcvSearchProduct: RecyclerView
    private lateinit var viewModel: HomepageViewModel
    private lateinit var viewModelCart: CartViewModel
    private lateinit var viewModelWishlist: WishlistViewModel
    private lateinit var adapter: PProductAdapter
    private lateinit var editTextSearch: EditText
    private var searchKeyword: String? = null
    private var isLoading = false
    private var currentPage = 1
    private val limit = 6
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_product)

        viewModel = ViewModelProvider(
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

        rcvSearchProduct = findViewById(R.id.rcvSearchProduct)
        editTextSearch = findViewById(R.id.editTextSearch)
        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener { finish() }


        // Tải dữ liệu wishlist ngay trong onCreate
        val token = TokenManager.getToken(this)
        if (token != null) {
            viewModelWishlist.getWishlist(token)
            viewModelCart.getCartQuantity(token) // Đồng thời tải số lượng giỏ hàng
        }
        adapter = PProductAdapter(this, viewModelCart, viewModelWishlist) { product ->
            showToast("Clicked: ${product.name}")
        }
        rcvSearchProduct.layoutManager = GridLayoutManager(this, 2)
        rcvSearchProduct.setHasFixedSize(true)
        rcvSearchProduct.isNestedScrollingEnabled = false
        rcvSearchProduct.adapter = adapter

        val selectedItem: Suggestion? = intent.getParcelableExtra("SEARCH_QUERY") as? Suggestion

        val getKeyword = intent.getStringExtra("SEARCH_QUERY_BUTTON")
        searchKeyword = selectedItem?.keyword ?: getKeyword

        searchKeyword?.let {
            fetchProducts(it, currentPage)
        }



        // Thêm sự kiện ScrollListener để load thêm sản phẩm
        rcvSearchProduct.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                // Kiểm tra xem có đang tải dữ liệu không, và chỉ tải thêm khi gần cuối danh sách
                if (!isLoading && totalItemCount > 0 && firstVisibleItemPosition > 0 &&
                    totalItemCount - lastVisibleItemPosition <= 3) {
                    isLoading = true
                    currentPage++
                    searchKeyword?.let {
                        fetchProducts(it, currentPage)
                    }
                }
            }
        })


        //Quan sát kết quả add to cart
        viewModelCart.addToCartResult.observe(this) { result ->
            result.onSuccess { response ->
                Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
            }.onFailure { error ->
                Toast.makeText(this, "Failed: ${error.message}", Toast.LENGTH_LONG).show()
                Log.e("CartViewModel", "Failed to add to cart: ${error.message}")
            }
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

        // Quan sát dữ liệu từ ViewModel
        viewModel.searchProducts.observe(this, Observer { products ->
            if (currentPage == 1) {
                adapter.setData(products) // Gán dữ liệu sản phẩm trước

            } else {
                adapter.addData(products) // Các trang sau: thêm dữ liệu
            }
            isLoading = false
            viewModelWishlist.wishlistItems.value?.let { wishlistItems ->
                val favProductIds = wishlistItems.filter { it.status }.map { it._id }.toSet()
                adapter.updateWishlistStatus(favProductIds)
            }
        })
    }

    private fun fetchProducts(keyword: String, page: Int) {
        isLoading = true
        lifecycleScope.launch {
            try {
                viewModel.searchProducts(keyword, page, limit)
            } finally {
                isLoading = false // Đảm bảo isLoading luôn được reset
            }
        }
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}