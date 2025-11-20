package drawable.home_page

import PProductAdapter
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.coding.imagesliderwithdotindicatorviewpager2.adapters.BannerAdapter
import com.coding.imagesliderwithdotindicatorviewpager2.adapters.TopSellingAdapter
import com.example.ecommerce.R
import com.example.ecommerce.adapter.CategoriesAdapter
import com.example.ecommerce.features.auth.LoginActivity
import com.example.ecommerce.features.cart.CartActivity
import com.example.ecommerce.features.cart.CartRepository
import com.example.ecommerce.features.cart.CartViewModel
import com.example.ecommerce.features.home_page.HomepageViewModel
import com.example.ecommerce.features.home_page.HomepageViewModelFactory
import com.example.ecommerce.features.wishlist.WishlistActivity
import com.example.ecommerce.features.wishlist.WishlistRepository
import com.example.ecommerce.utils.TokenManager
import com.example.ecommerce.viewModel.CartViewModelFactory
import com.example.ecommerce.viewModel.WishlistViewModel
import com.example.ecommerce.viewModel.WishlistViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class HomepageFragment : Fragment() {
    private lateinit var viewModel: HomepageViewModel
    private lateinit var viewModelCart: CartViewModel
    private lateinit var viewModelWishlist: WishlistViewModel
    private lateinit var viewPager2: ViewPager2
    private lateinit var rcvCategory: RecyclerView
    private lateinit var rcvPProduct: RecyclerView
    private lateinit var rcvFurniture: RecyclerView
    private lateinit var rcvNewShoes: RecyclerView
    private lateinit var rcvTopSelling: RecyclerView
    private lateinit var rcvAllProduct: RecyclerView
    private lateinit var topProductImage: ImageView
    private lateinit var topProductName: TextView
    private lateinit var txtBrandname: TextView
    private lateinit var txtRating: TextView
    private lateinit var txtQuantitySold: TextView
    private lateinit var topProducOriginalPrice: TextView
    private lateinit var topProductSellingPrice: TextView
    private lateinit var txtCartBadge: TextView
    private lateinit var txtWishlistBadge: TextView
    private lateinit var topProductCard: CardView
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var imgWishlist: ImageView
    private lateinit var imgCartList: ImageView

    private val handler = Handler(Looper.getMainLooper())
    private var currentPage = 0
    private val delay: Long = 5000
    private var isBottomNavVisible = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Khởi tạo ViewModel
        viewModel = ViewModelProvider(
            this,
            HomepageViewModelFactory(HomepageRepository())
        )[HomepageViewModel::class.java]

        viewModelCart = ViewModelProvider(
            this,
            CartViewModelFactory(CartRepository())
        )[CartViewModel::class.java]

        viewModelWishlist = ViewModelProvider(
            this,
            WishlistViewModelFactory(WishlistRepository())
        )[WishlistViewModel::class.java]

        // Tải dữ liệu wishlist ngay trong onCreate
        val token = TokenManager.getToken(requireContext())
        if (token != null) {
            viewModelWishlist.getWishlist(token)
            viewModelCart.getCartQuantity(token) // Đồng thời tải số lượng giỏ hàng
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_homepage, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ánh xạ các view
        bottomNav = requireActivity().findViewById(R.id.bottomNavigationView)
        val scrollView = view.findViewById<NestedScrollView>(R.id.nestedScrollView)
        val editTextSearch = view.findViewById<EditText>(R.id.editTextSearch)

        viewPager2 = view.findViewById(R.id.viewpager2)
        rcvCategory = view.findViewById(R.id.rcvCategories)
        rcvPProduct = view.findViewById(R.id.rcvPProduct)
        rcvFurniture = view.findViewById(R.id.rcvFurniture)
        rcvNewShoes = view.findViewById(R.id.rcvNewShoes)
        rcvTopSelling = view.findViewById(R.id.rcvTopSelling)
        rcvAllProduct = view.findViewById(R.id.rcvAllProduct)
        topProductImage = view.findViewById(R.id.topProductImage)
        topProductName = view.findViewById(R.id.txtTopProductName)
        txtBrandname = view.findViewById(R.id.txtBrandName)
        txtRating = view.findViewById(R.id.txtRating)
        txtQuantitySold = view.findViewById(R.id.txtQuantitySold)
        topProducOriginalPrice = view.findViewById(R.id.txtoriginalPrice)
        topProductSellingPrice = view.findViewById(R.id.txtsellingPrice)
        topProductCard = view.findViewById(R.id.topProductCard)
        txtCartBadge = view.findViewById(R.id.txtcartBadge)
        txtWishlistBadge = view.findViewById(R.id.txtwishListBadge)
        imgWishlist = view.findViewById(R.id.imgWishlist)
        imgCartList = view.findViewById(R.id.cartIcon)

        // Xử lý sự kiện click vào thanh tìm kiếm
        editTextSearch.setOnClickListener {
            startActivity(Intent(requireContext(), SearchFilterActivity::class.java))
        }

        val token = TokenManager.getToken(requireContext())

        imgWishlist.setOnClickListener {
            if (token != null) {
                startActivity(Intent(requireContext(), WishlistActivity::class.java))
            }else {
                startActivity(Intent(requireContext(), LoginActivity::class.java))
            }
        }
        imgCartList.setOnClickListener {
            if (token != null) {
            startActivity(Intent(requireContext(), CartActivity::class.java))
            }else {
                startActivity(Intent(requireContext(), LoginActivity::class.java))
            }
        }


        // Thiết lập các adapter, observer và dữ liệu
        setupAdapters()
        observeViewModel()
        fetchData() // Tải các dữ liệu khác ngoài wishlist
        setupRecyclerViews()
        hideBottomNavWhenScroll(scrollView)

        // Tự động chuyển slide cho ViewPager
        handler.postDelayed(autoSlideRunnable, delay)
    }

    private fun setupAdapters() {
        rcvCategory.adapter = CategoriesAdapter { showToast("Clicked: ${it.name}") }

        setupRecyclerViewAdapter(
            rcvPProduct,
            PProductAdapter(requireContext(), viewModelCart, viewModelWishlist) { product ->
                showToast("Clicked: ${product.name}")
            })

        setupRecyclerViewAdapter(
            rcvFurniture,
            PProductAdapter(requireContext(), viewModelCart, viewModelWishlist) { product ->
                showToast("Clicked: ${product.name}")
            })

        setupRecyclerViewAdapter(
            rcvNewShoes,
            PProductAdapter(requireContext(), viewModelCart, viewModelWishlist) { product ->
                showToast("Clicked: ${product.name}")
            })

        setupRecyclerViewAdapter(
            rcvAllProduct,
            PProductAdapter(requireContext(), viewModelCart, viewModelWishlist) { product ->
                showToast("Clicked: ${product.name}")
            })
        rcvTopSelling.adapter = TopSellingAdapter(emptyList()) { product ->
            showToast("Clicked: ${product.name}")
        }


        viewPager2.adapter = BannerAdapter { showToast("Clicked: ${it.title}") }
    }

    private fun setupRecyclerViewAdapter(
        recyclerView: RecyclerView,
        adapter: RecyclerView.Adapter<*>
    ) {
        recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        // Quan sát danh sách quảng cáo
        viewModel.adsList.observe(viewLifecycleOwner) { ads ->
            // Quan sát danh sách quảng cáo
            viewModel.adsList.observe(viewLifecycleOwner) { ads ->
                if (ads.isNullOrEmpty()) {
                    viewPager2.visibility = View.GONE
                } else {
                    viewPager2.visibility = View.VISIBLE
                    (viewPager2.adapter as BannerAdapter).setData(ads)
                    setupDotsIndicator(ads.size)
                }
            }

        }

        // Quan sát danh sách danh mục
        viewModel.categoriesList.observe(viewLifecycleOwner) {
            (rcvCategory.adapter as CategoriesAdapter).setData(it)
        }

        // Quan sát danh sách sản phẩm
        viewModel.productsList.observe(viewLifecycleOwner) {
            (rcvPProduct.adapter as PProductAdapter).setData(it)
        }

        // Quan sát danh sách đồ nội thất
        viewModel.furnitureList.observe(viewLifecycleOwner) {
            (rcvFurniture.adapter as PProductAdapter).setData(it)
        }

        // Quan sát danh sách giày mới
        viewModel.newShoesList.observe(viewLifecycleOwner) {
            (rcvNewShoes.adapter as PProductAdapter).setData(it)
        }

        // Quan sát danh sách sản phẩm bán chạy
        viewModel.topSellingList.observe(viewLifecycleOwner) { products ->
            if (products.isNotEmpty()) {
                val topProduct = products[0]
                topProductName.text = topProduct.name
                txtBrandname.text = topProduct.brandName
                txtRating.text = topProduct.rating.toString()
                txtQuantitySold.text = topProduct.quantitySold.toString()
                topProducOriginalPrice.text = "$${topProduct.originalPrice}"
                topProductSellingPrice.text = "$${topProduct.sellingPrice}"
                topProducOriginalPrice.paintFlags =
                    topProducOriginalPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                Glide.with(this).load(topProduct.thumbnail).into(topProductImage)
                topProductCard.visibility = View.VISIBLE
            } else {
                topProductCard.visibility = View.GONE
            }
            val remainingProducts = products.drop(1)
            (rcvTopSelling.adapter as? TopSellingAdapter)?.setData(remainingProducts)
        }

        // Quan sát tất cả sản phẩm
        viewModel.allProductList.observe(viewLifecycleOwner) {
            (rcvAllProduct.adapter as PProductAdapter).setData(it)
        }

        // Quan sát kết quả thêm vào giỏ hàng
        viewModelCart.addToCartResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess { response ->
                Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
                val token = TokenManager.getToken(requireContext())
                if (token != null) {
                    viewModelCart.getCartQuantity(token)
                }
            }.onFailure { error ->
                Toast.makeText(context, "Failed: ${error.message}", Toast.LENGTH_LONG).show()
                Log.e("CartViewModel", "Failed to add to cart: ${error.message}")
            }
        }

        // Quan sát số lượng giỏ hàng
        viewModelCart.cartQuantity.observe(viewLifecycleOwner) { result ->
            result.onSuccess { quantity ->
                Log.d("Home", "Cart Quantity Updated: $quantity")
                txtCartBadge.text = quantity.toString()
            }.onFailure { error ->
                Log.e("Home", "Failed to fetch cart quantity: ${error.message}")
            }
        }

        // Quan sát danh sách yêu thích
        viewModelWishlist.wishlistItems.observe(viewLifecycleOwner) { wishlistItems ->
            val favProductIds = wishlistItems.filter { it.status }.map { it._id }.toSet()
            (rcvPProduct.adapter as? PProductAdapter)?.updateWishlistStatus(favProductIds)
            (rcvFurniture.adapter as? PProductAdapter)?.updateWishlistStatus(favProductIds)
            (rcvNewShoes.adapter as? PProductAdapter)?.updateWishlistStatus(favProductIds)
            (rcvAllProduct.adapter as? PProductAdapter)?.updateWishlistStatus(favProductIds)
        }

        // Quan sát kết quả thêm vào wishlist
        viewModelWishlist.addToWishlistResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess { message ->
                Toast.makeText(context, message ?: "Added to wishlist", Toast.LENGTH_SHORT).show()
                val token = TokenManager.getToken(requireContext())
                if (token != null) {
                    viewModelWishlist.getWishlist(token)
                }
            }.onFailure { error ->
                Toast.makeText(context, "Failed: ${error.message}", Toast.LENGTH_LONG).show()
                Log.e("WishlistViewModel", "Failed to add to wishlist: ${error.message}")
            }
        }

        // Quan sát số lượng wishlist
        viewModelWishlist.wishlistQuantity.observe(viewLifecycleOwner) { quantity ->
            txtWishlistBadge.text = quantity.toString()
        }
    }

    private fun fetchData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.fetchAds()
            viewModel.fetchCategories()
            viewModel.fetchProducts()
            viewModel.fetchFurniture()
            viewModel.fetchNewShoes()
            viewModel.fetchTopSelling()
            viewModel.fetchAllProduct()
            // Lưu ý: Không gọi getWishlist ở đây nữa vì đã gọi trong onCreate
        }
    }

    private fun setupRecyclerViews() {
        listOf(rcvCategory, rcvPProduct, rcvFurniture, rcvNewShoes).forEach {
            it.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            it.setHasFixedSize(true)
            it.isNestedScrollingEnabled = false
        }
        val layoutManager2 =
            object : LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false) {
                override fun canScrollHorizontally(): Boolean = false
            }
        rcvTopSelling.layoutManager = layoutManager2
        rcvTopSelling.isNestedScrollingEnabled = false

        rcvAllProduct.layoutManager = GridLayoutManager(requireContext(), 2)
        rcvAllProduct.setHasFixedSize(true)
        rcvAllProduct.isNestedScrollingEnabled = false
    }

    private fun hideBottomNavWhenScroll(scrollView: NestedScrollView) {
        scrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            val shouldHide = scrollY > oldScrollY && isBottomNavVisible
            val shouldShow = scrollY < oldScrollY && !isBottomNavVisible

            if (shouldHide) {
                bottomNav.animate().translationY(bottomNav.height.toFloat()).setDuration(200)
                isBottomNavVisible = false
            } else if (shouldShow) {
                bottomNav.animate().translationY(0f).setDuration(200)
                isBottomNavVisible = true
            }
        })
    }

    private fun setupDotsIndicator(size: Int) {
        val slideDotLL = view?.findViewById<LinearLayout>(R.id.slideDotLL)
        slideDotLL?.removeAllViews()

        val maxDots = 4
        val displaySize = minOf(size, maxDots)
        val dotsImage = Array(displaySize) { ImageView(requireContext()) }
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

    private val autoSlideRunnable = object : Runnable {
        override fun run() {
            val itemCount = viewPager2.adapter?.itemCount ?: 0
            if (itemCount > 0) {
                currentPage = (currentPage + 1) % itemCount
                viewPager2.setCurrentItem(currentPage, true)
                handler.postDelayed(this, delay)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val token = TokenManager.getToken(requireContext())
        if (token != null) {
            viewModelWishlist.getWishlist(token)
            viewModelCart.getCartQuantity(token) // Nếu muốn cập nhật cả giỏ hàng
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(autoSlideRunnable)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}