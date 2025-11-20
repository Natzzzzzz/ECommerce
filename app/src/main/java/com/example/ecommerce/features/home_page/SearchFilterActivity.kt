package drawable.home_page

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerce.R
import com.example.ecommerce.adapter.FilterSearchAdapter
import com.example.ecommerce.features.home_page.HomepageViewModel
import com.example.ecommerce.features.home_page.HomepageViewModelFactory

class SearchFilterActivity : AppCompatActivity() {
    private lateinit var rcvFilterSearch: RecyclerView
    private lateinit var editTextSearch: EditText
    private lateinit var searchButton: ImageView
    private lateinit var viewModel: HomepageViewModel

    private val handler = Handler(Looper.getMainLooper()) // Handler để debounce
    private var searchRunnable: Runnable? = null // Runnable để trì hoãn API call

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_filter)

        viewModel = ViewModelProvider(
            this,
            HomepageViewModelFactory(HomepageRepository())
        )[HomepageViewModel::class.java]

        editTextSearch = findViewById(R.id.searchField)
        searchButton = findViewById(R.id.searchButton)
        val parentLayout = findViewById<View>(R.id.customToolbar)
        val backButton = findViewById<ImageView>(R.id.backButton)
        rcvFilterSearch = findViewById(R.id.rcvFilterSearch)

        backButton.setOnClickListener { finish() }

        rcvFilterSearch.layoutManager = LinearLayoutManager(this)
        rcvFilterSearch.setHasFixedSize(true)
        rcvFilterSearch.isNestedScrollingEnabled = false

        searchButton.setOnClickListener {
            val intent = Intent(this, SearchProductActivity::class.java)
            intent.putExtra("SEARCH_QUERY_BUTTON", editTextSearch.text.toString().trim())
            startActivity(intent)
        }

        val adapter = FilterSearchAdapter(
            onItemClick = { suggestion ->
                val intent = Intent(this, SearchProductActivity::class.java)
                intent.putExtra("SEARCH_QUERY", suggestion)
                startActivity(intent)
                Toast.makeText(this, "Clicked: ${suggestion.keyword}", Toast.LENGTH_SHORT).show()
            },
        )

        rcvFilterSearch.adapter = adapter

        viewModel.searchSuggestion.observe(this) { products ->
            adapter.setData(products, editTextSearch.text.toString())
        }

        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchRunnable?.let { handler.removeCallbacks(it) } // Hủy task cũ nếu có
                searchRunnable = Runnable {
                    val query = s.toString().trim()
                    if (query.isNotEmpty()) {
                        viewModel.searchSuggestion(query)
                        Log.d("SearchActivity", "API Called with query: $query")
                    } else {
                        adapter.setData(emptyList(), "") // Xóa dữ liệu khi search rỗng
                    }
                }
                handler.postDelayed(searchRunnable!!, 300) // Chờ 300ms trước khi gọi API
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        parentLayout.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                editTextSearch.clearFocus()
                hideKeypad()
                view.performClick()
            }
            true
        }
    }

    private fun hideKeypad() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editTextSearch.windowToken, 0)
    }
}
