package com.r42914lg.tryflow.domain

import com.r42914lg.tryflow.utils.log
import com.r42914lg.tryflow.utils.Result
import com.r42914lg.tryflow.utils.runOperationCatching
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Remote
 */
class CategoryRemoteDataSource(
    private val categoryService: CategoryService
) {
    interface CategoryService {

        @GET("categories") suspend fun getCategories(
            @Query("count") amountOfCluesToReturn: Int,
            @Query("offset") offset: Int,
        ): List<Category>

        @GET("category")
        suspend fun getDetailedCategory(@Query("id") categoryId: Int): CategoryDetailed
    }

    suspend fun getCategories() =
        runOperationCatching {
            log("starting getCategories - 2 sec to go...")
            delay(2000)
            log("finished getCategories")
            categoryService.getCategories(NUM_OF_ITEMS, OFFSET)
        }

    suspend fun getDetails(categoryId: Int) =
        runOperationCatching {
            log("starting getDetails - 2 sec to go...")
            delay(50)
            log("finished getDetails")
            categoryService.getDetailedCategory(categoryId)
        }

    companion object {
        const val OFFSET = 0
        const val NUM_OF_ITEMS = 50
    }
}

/**
 * Local (in-memory)
 */
class CategoryLocalDataSource {
    private val _detailsMap = mutableMapOf<Int, CategoryDetailed>()
    private val _keys = mutableSetOf<Int>()
    private var currIndex = 0

    suspend fun saveAll(detailsMap: MutableMap<Int, CategoryDetailed>) {
        log("starting saveData - 10 sec to go...")
        delay(10000)
        _detailsMap.putAll(detailsMap)
        _keys.addAll(_detailsMap.keys)
        log("finished saveData")
    }

    suspend fun getCategoryData(): Flow<Result<CategoryDetailed, Throwable>> = flow {
        runOperationCatching {
            log("starting getDataAsFlow - 2 sec to go...")
            delay(2000)
            log("finished getDataAsFlow")
            nextItem()
        }
    }

    private fun nextItem(): CategoryDetailed {
        val retVal = _detailsMap[_keys.elementAt(currIndex++)]!!
        if (currIndex == _keys.size)
            currIndex = 0

        return retVal
    }
}

