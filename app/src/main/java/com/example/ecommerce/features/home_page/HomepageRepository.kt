package drawable.home_page

import android.util.Log
import com.example.ecommerce.utils.RetrofitInstance
import com.example.ecommerce.model.Product
import com.example.ecommerce.model.ImageItem
import com.example.ecommerce.model.Category
import com.example.ecommerce.model.Suggestion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class HomepageRepository {
    private val api = RetrofitInstance.homepageAPI

    fun getAds(): Flow<Result<List<ImageItem>>> = flow {
        emit(Result.success(api.getDataAds()))
    }.catch { e ->
        emit(Result.failure(e))
    }


    fun getCategories(): Flow<Result<List<Category>>> = flow {
        emit(Result.success(api.getDataCategories()))
    }.catch { e ->
        emit(Result.failure(e))
    }

    fun getPProducts(): Flow<Result<List<Product>>> = flow {
        emit(Result.success(api.getDataPProduct()))
    }.catch { e ->
        emit(Result.failure(e))
    }

    fun getFurniture(): Flow<Result<List<Product>>> = flow {
        emit(Result.success(api.getDataFurniture()))
    }.catch { e ->
        emit(Result.failure(e))
    }

    fun getNewShoes(): Flow<Result<List<Product>>> = flow {
        emit(Result.success(api.getDataNewShoes()))
    }.catch { e ->
        emit(Result.failure(e))
    }

    fun getTopSelling(): Flow<Result<List<Product>>> = flow {
        emit(Result.success(api.getDataTopSelling()))
    }.catch { e ->
        emit(Result.failure(e))
    }

    fun getAllProduct(): Flow<Result<List<Product>>> = flow {
        emit(Result.success(api.getDataAllProduct()))
    }.catch { e ->
        emit(Result.failure(e))
    }

    fun getProductsByCategory(categoryId: String, page: Int, limit: Int): Flow<Result<List<Product>>> = flow {
        emit(Result.success(api.getProductsByCategory(categoryId,page,limit)))
    }.catch { e ->
        emit(Result.failure(e))
    }

    fun searchSuggestions(keyword: String): Flow<Result<List<Suggestion>>> = flow {
        Log.d("HomepageRepository", "Calling API with keyword: $keyword")
        val response = api.searchSuggestion(keyword)
        if (response.isSuccessful) {
            val suggestion = response.body()?.suggestions ?: emptyList()
            Log.d("HomepageRepository", "API Success: Found ${suggestion.size} products")
            emit(Result.success(suggestion))
        } else {
            Log.e("HomepageRepository", "API Error: ${response.errorBody()?.string()}")
            emit(Result.failure(Exception("API Error")))
        }
    }.catch { e ->
        Log.e("HomepageRepository", "Exception: ${e.message}")
        emit(Result.failure(e))
    }

    fun searchProducts(keyword: String, page: Int, limit: Int): Flow<Result<List<Product>>> = flow {
        try {
            Log.d("HomepageRepository", "Calling API with keyword: $keyword, page: $page, limit: $limit")

            val response = api.searchProducts(keyword, page, limit)

            if (response.isSuccessful) { // Kiểm tra thành công
                val products = response.body() ?: emptyList()
                Log.d("HomepageRepository", "API Success: Found ${products.size} products")
                emit(Result.success(products))
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                Log.e("HomepageRepository", "API Error: ${response.code()} - $errorMsg")
                emit(Result.failure(Exception("API Error: ${response.code()} - $errorMsg")))
            }
        } catch (e: Exception) {
            Log.e("HomepageRepository", "Exception: ${e.message}")
            emit(Result.failure(e))
        }
    }.catch { e ->
        Log.e("HomepageRepository", "Flow Exception: ${e.message}")
        emit(Result.failure(e))
    }



}
