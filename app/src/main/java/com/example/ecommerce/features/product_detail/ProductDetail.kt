package com.example.ecommerce.features.product_detail

import PProductAdapter
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommerce.R
import com.example.ecommerce.adapter.AttributeTypeAdapter
import com.example.ecommerce.adapter.ProductDetailAdapter
import com.example.ecommerce.adapter.VariantAdapter
import com.example.ecommerce.features.cart.CartRepository
import com.example.ecommerce.features.cart.CartViewModel
import com.example.ecommerce.features.home_page.HomepageViewModel
import com.example.ecommerce.features.home_page.HomepageViewModelFactory
import com.example.ecommerce.features.wishlist.WishlistRepository
import com.example.ecommerce.model.Attribute
import com.example.ecommerce.model.Category
import com.example.ecommerce.model.Product
import com.example.ecommerce.model.ProductData
import com.example.ecommerce.model.ProductTest
import com.example.ecommerce.model.Variant
import com.example.ecommerce.utils.TokenManager
import com.example.ecommerce.viewModel.CartViewModelFactory
import com.example.ecommerce.viewModel.ProductDetailViewModel
import com.example.ecommerce.viewModel.ProductDetailViewModelFactory
import com.example.ecommerce.viewModel.WishlistViewModel
import com.example.ecommerce.viewModel.WishlistViewModelFactory
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import drawable.home_page.HomepageRepository

class ProductDetail : AppCompatActivity(), AppBarLayout.OnOffsetChangedListener {
    private lateinit var toolbar: Toolbar
    private lateinit var backButton: ImageButton
    private lateinit var cartButton: ImageButton
    private lateinit var wishListButton: ImageButton
    private lateinit var imgMainPD: ImageView
    private lateinit var searchField: EditText
    private lateinit var txtProductName: TextView
    private lateinit var txtsellingPriceProduct: TextView
    private lateinit var txtoriginalPriceProduct: TextView
    private lateinit var webViewDescription: WebView
    private lateinit var btnSeeMore: Button
    private lateinit var btnCollapse: Button
    private lateinit var imgViewShop: ImageView
    private lateinit var txtnameShop: TextView
    private lateinit var txtfollowShop: TextView
    private lateinit var txtratingShop: TextView
    private lateinit var txtproductShop: TextView
    private lateinit var rcvImagePattern: RecyclerView
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var viewModel: ProductDetailViewModel
    private lateinit var btnAddToCart: ImageButton
    private lateinit var btnBuyNow: Button
    private lateinit var viewModelHome: HomepageViewModel
    private lateinit var viewModelCart: CartViewModel
    private lateinit var viewModelWishlist: WishlistViewModel
    private lateinit var rcvSimilarItem: RecyclerView
    private lateinit var adapter2: PProductAdapter
    private var isLoading = false
    private var currentPage = 1
    private val limit = 6

    private val COLLAPSED_HEIGHT = 300 // Chiều cao ban đầu (pixel)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        // Khởi tạo các view
        toolbar = findViewById(R.id.toolbar)
        rcvImagePattern = findViewById(R.id.rcvImagePattern)
        imgMainPD = findViewById(R.id.imgMainPD)
        backButton = findViewById(R.id.backButton)
        txtProductName = findViewById(R.id.txtProductName)
        txtsellingPriceProduct = findViewById(R.id.txtsellingPriceProduct)
        txtoriginalPriceProduct = findViewById(R.id.txtoriginalPriceProduct)
        webViewDescription = findViewById(R.id.webViewDescription)
        btnSeeMore = findViewById(R.id.btnSeeMore)
        imgViewShop = findViewById(R.id.imgShop)
        txtnameShop = findViewById(R.id.txtnameShop)
        txtfollowShop = findViewById(R.id.txtfollowShop)
        txtratingShop = findViewById(R.id.txtratingShop)
        txtproductShop = findViewById(R.id.txtproductShop)
        btnCollapse = findViewById(R.id.btnCollapse)
        searchField = findViewById(R.id.searchFieldProduct)
        cartButton = findViewById(R.id.btnCart)
        wishListButton = findViewById(R.id.btnWishlist)
        appBarLayout = findViewById(R.id.appBarLayout)
        btnAddToCart = findViewById(R.id.btnAddToCart)
        btnBuyNow = findViewById(R.id.btnBuyNow)
        rcvSimilarItem = findViewById(R.id.rcvSimilarItem)

        // Khởi tạo ViewModel
        viewModel = ViewModelProvider(
            this,
            ProductDetailViewModelFactory(ProductDetailRepository())
        )[ProductDetailViewModel::class.java]

        viewModelHome = ViewModelProvider(
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



        // Thiết lập rcvImagePattern
        rcvImagePattern.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rcvImagePattern.setHasFixedSize(true)

        backButton.setOnClickListener { finish() }

        val productId = intent.getStringExtra("PRODUCT_ID")
        productId?.let { id ->
            viewModel.fetchProductDetail(id)
        } ?: Log.e("ProductDetail", "Product ID is null")

        // Thiết lập chiều cao ban đầu cho WebView
        webViewDescription.layoutParams.height = COLLAPSED_HEIGHT
        webViewDescription.requestLayout()
        btnCollapse.isVisible = false

        appBarLayout.addOnOffsetChangedListener(this)

        // Quan sát dữ liệu sản phẩm từ ProductDetailViewModel
        viewModel.product.observe(this) { productResponse ->
            productResponse?.let {
                val imageUrls = it.productData.images

                val adapter = ProductDetailAdapter(imageUrls) { selectedImageUrl ->
                    fetchImgView(selectedImageUrl, imgMainPD)
                }
                adapter.setData(imageUrls)
                rcvImagePattern.adapter = adapter

                rcvImagePattern.post {
                    if (imageUrls.isNotEmpty()) {
                        fetchImgView(imageUrls[0], imgMainPD)
                    }
                }

                imgMainPD.setOnClickListener {
                    val intent = Intent(this, ImageDetailActivity::class.java)
                    intent.putStringArrayListExtra("PRODUCT_ITEM_DATA", ArrayList(imageUrls))
                    startActivity(intent)
                }
                txtProductName.text = it.productData.name

                // Tải dữ liệu ban đầu cho rcvSimilarItem dựa trên categoryId
                val categoryId = it.productData.categoryId
                if (categoryId.isNotEmpty()) {
                    Log.d("ProductDetail", "Fetching products for categoryId: $categoryId, page: $currentPage")
                    fetchProducts(categoryId, currentPage)
                } else {
                    Log.e("ProductDetail", "Category ID is empty")
                }

                // Tải mô tả vào WebView
                val htmlDescription = it.productData.description
                val decodedHtml = htmlDescription
                    .replace("\\u003C", "<")
                    .replace("\\u003E", ">")
                    .replace("<h2>", "<h4>").replace("</h2>", "</h4>")
                    .replace("<h3>", "<h5>").replace("</h3>", "</h5>")

                webViewDescription.loadDataWithBaseURL(null, decodedHtml, "text/html", "UTF-8", null)


                // Tìm price và salePrice thấp nhất
                val variants = it.variants
                val minPrice = variants.minOf { it.price }
                val salePrices = variants.mapNotNull { it.salePrice }
                val minSalePrice = if (salePrices.isNotEmpty()) salePrices.min() else null

                if (minSalePrice != null) {
                    txtsellingPriceProduct.text = "$${String.format("%.2f", minSalePrice)}"
                    txtoriginalPriceProduct.text = "$${String.format("%.2f", minPrice)}"
                    txtoriginalPriceProduct.visibility = View.VISIBLE
                    btnBuyNow.text = "Buy now\n$${String.format("%.2f", minSalePrice)}"
                } else {
                    txtsellingPriceProduct.text = "$${String.format("%.2f", minPrice)}"
                    txtoriginalPriceProduct.visibility = View.INVISIBLE
                    btnBuyNow.text = "Buy now\n$${String.format("%.2f", minPrice)}"
                }
                txtoriginalPriceProduct.paintFlags = txtoriginalPriceProduct.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                val shopInfo = it.productData.shopId
                txtnameShop.text = shopInfo.name
                txtfollowShop.text = "${shopInfo.followers} followers"
                txtratingShop.text = "${shopInfo.rating}"
                fetchImgView(shopInfo.logo, imgViewShop)

                btnAddToCart.setOnClickListener {
                    showBottomSheetDialogWithVariants(variants)
                }

                btnSeeMore.setOnClickListener {
                    webViewDescription.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    webViewDescription.requestLayout()
                    btnSeeMore.isVisible = false
                    btnCollapse.isVisible = true
                }

                btnCollapse.setOnClickListener {
                    webViewDescription.layoutParams.height = COLLAPSED_HEIGHT
                    webViewDescription.requestLayout()
                    btnSeeMore.isVisible = true
                    btnCollapse.isVisible = false
                }

                webViewDescription.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
                    val contentHeight = webViewDescription.contentHeight
                    val viewHeight = webViewDescription.height
                    if (scrollY + viewHeight >= contentHeight - 50) {
                        btnCollapse.isVisible = true
                    }
                }

                // Thiết lập rcvSimilarItem
                adapter2 = PProductAdapter(this, viewModelCart, viewModelWishlist) { product ->
                    showToast("Clicked: ${product.name}")
                }
                rcvSimilarItem.adapter = adapter2
                rcvSimilarItem.layoutManager = GridLayoutManager(this, 2)
                rcvSimilarItem.setHasFixedSize(true) // Thêm để tối ưu hóa
                Log.d("ProductDetail", "Attaching adapter to rcvSimilarItem")
//                rcvSimilarItem.post { rcvSimilarItem.requestLayout() }


                viewModelHome.productListByCategory.observe(this, Observer { products ->
                    Log.d("ProductDetail", "Fetched products: ${products?.size ?: 0}")
                    products?.let {
                        if (it.isNotEmpty()) {
                            adapter2.setData(it)
                        } else {
                            Log.d("ProductDetail", "No similar products found")
                        }
                    }
                    isLoading = false
                })

                // Xử lý cuộn để tải thêm dữ liệu
                rcvSimilarItem.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        val layoutManager = recyclerView.layoutManager as GridLayoutManager
                        val totalItemCount = layoutManager.itemCount
                        val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                        if (!isLoading && lastVisibleItemPosition + 1 >= totalItemCount) {
                            isLoading = true
                            currentPage++
                            viewModel.product.value?.let { productResponse ->
                                val categoryId = productResponse.productData.categoryId
                                if (categoryId.isNotEmpty()) {
                                    fetchProducts(categoryId, currentPage)
                                } else {
                                    Log.e("ScrollDebug", "Category ID is empty")
                                }
                            } ?: Log.e("ScrollDebug", "No product data available")
                        }
                    }
                })


            }
        }

    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        val scrollRange = appBarLayout?.totalScrollRange ?: 0
        if (scrollRange == 0) return

        val scrollPercentage = Math.abs(verticalOffset) / scrollRange.toFloat()
        val alpha = (scrollPercentage * 255).toInt().coerceIn(0, 255)
        val color = Color.argb(alpha, 255, 255, 255)
        toolbar.setBackgroundColor(color)

        if (scrollPercentage > 0.1f) {
            searchField.visibility = View.VISIBLE
            searchField.alpha = scrollPercentage.coerceIn(0f, 1f)
        } else {
            searchField.visibility = View.INVISIBLE
            searchField.alpha = 0f
        }
    }

    private fun showBottomSheetDialogWithVariants(variants: List<Variant>) {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
        bottomSheetDialog.setContentView(view)

        val decreaseQty = view.findViewById<ImageButton>(R.id.decreaseQty)
        val increaseQty = view.findViewById<ImageButton>(R.id.increaseQty)
        val quantity = view.findViewById<TextView>(R.id.quantity)
        val addToCartButton = view.findViewById<Button>(R.id.addToCartButton)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rcv_attribute_types)
        val recyclerViewVariant = view.findViewById<RecyclerView>(R.id.rcv_variant)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerViewVariant.layoutManager = LinearLayoutManager(this)

        val attributesList = variants.flatMap { it.attributes }
        val attributeMap = attributesList.groupBy { it.type }
            .mapValues { entry -> entry.value.map { it.value }.distinct() }

        val selectedAttributes = mutableMapOf<String, String>()

        // Khởi tạo VariantAdapter với variant đầu tiên
        val variantAdapter = VariantAdapter(variants[0])
        recyclerViewVariant.adapter = variantAdapter

        val adapter = AttributeTypeAdapter(attributeMap, variants) { type, value ->
            selectedAttributes[type] = value
            // Tìm variant dựa trên selectedAttributes
            val selectedVariantId = findVariantIdByAttributes(variants, selectedAttributes)
            val selectedVariant = variants.find { it.id == selectedVariantId } ?: variants[0]
            variantAdapter.updateVariant(selectedVariant)
            recyclerView.adapter?.notifyDataSetChanged()

            // Kiểm tra stock của variant được chọn và cập nhật trạng thái nút Add to Cart
            addToCartButton.isEnabled = selectedVariant.stock > 0
            if (selectedVariant.stock == 0) {
                addToCartButton.text = "Out of Stock"
                addToCartButton.alpha = 0.5f // Làm mờ nút để biểu thị không khả dụng
            } else {
                addToCartButton.text = "Add to Cart"
                addToCartButton.alpha = 1.0f // Trả lại trạng thái bình thường
            }
        }
        recyclerView.adapter = adapter

        var qty = 1
        quantity.text = qty.toString()

        decreaseQty.setOnClickListener {
            if (qty > 1) qty--; quantity.text = qty.toString()
        }

        increaseQty.setOnClickListener {
            qty++; quantity.text = qty.toString()
        }

        addToCartButton.setOnClickListener {
            val token = TokenManager.getToken(this) ?: ""
            val selectedVariantId = findVariantIdByAttributes(variants, selectedAttributes)

            if (selectedVariantId != null && addToCartButton.isEnabled) { // Chỉ thực hiện nếu nút được bật
                val qty = quantity.text.toString().toInt()
                viewModelCart.addToCart(token, selectedVariantId, qty)
                Log.d("AddToCart", "Adding variant with ID: $selectedVariantId, Quantity: $qty")
                Toast.makeText(this, "Add to cart success, Quantity: $qty", Toast.LENGTH_LONG).show()
                bottomSheetDialog.dismiss()
            } else if (!addToCartButton.isEnabled) {
                Toast.makeText(this, "This variant is out of stock", Toast.LENGTH_SHORT).show()
            } else {
                Log.e("AddToCart", "No variant matches the selected attributes: $selectedAttributes")
            }
        }

        // Kiểm tra stock của variant mặc định ban đầu
        val initialVariant = variants[0]
        addToCartButton.isEnabled = initialVariant.stock > 0
        if (initialVariant.stock == 0) {
            addToCartButton.text = "Out of Stock"
            addToCartButton.alpha = 0.5f
        } else {
            addToCartButton.text = "Add to Cart"
            addToCartButton.alpha = 1.0f
        }

        bottomSheetDialog.show()
    }

    private fun findVariantIdByAttributes(variants: List<Variant>, selectedAttributes: Map<String, String>): String? {
        return variants.find { variant ->
            val variantAttributes = variant.attributes.associate { it.type to it.value }
            selectedAttributes.all { (type, value) -> variantAttributes[type] == value }
        }?.id
    }

    fun fetchImgView(imageUrl: String, imgPD: ImageView) {
        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.banner_placeholder)
            .dontTransform()
            .into(imgPD)
    }

    private fun fetchProducts(categoryId: String, page: Int) {
        if (!isLoading) {
            isLoading = true
            Log.d("ProductDetail", "Calling fetchProductsByCategory with categoryId: $categoryId, page: $page, limit: $limit")
            viewModelHome.fetchProductsByCategory(categoryId, page, limit)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

