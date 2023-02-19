package com.r42914lg.tryflow.data

import com.r42914lg.tryflow.domain.CategoryDetailed
import com.r42914lg.tryflow.utils.log
import com.r42914lg.tryflow.utils.Result
import com.r42914lg.tryflow.utils.runOperationCatching
import kotlinx.coroutines.delay
import javax.inject.Inject

/**
 * Remote
 */
class CategoryRemoteDataSource @Inject constructor(
    private val categoryService: CategoryService
) {
    suspend fun getCategories() =
        runOperationCatching {
            delay(1000)
            val retVal = categoryService.getCategories(NUM_OF_ITEMS, OFFSET)
            log("finished getCategories - count loaded: ${retVal.size}")
            retVal
        }

    suspend fun getDetails(categoryId: Int) =
        runOperationCatching {
            delay(100)
            log("finished getDetails for id: $categoryId")
            categoryService.getDetailedCategory(categoryId)
        }

    companion object {
        const val OFFSET = 0
        const val NUM_OF_ITEMS = 20
    }
}

/**
 * Local (in-memory)
 */
class CategoryLocalDataSource @Inject constructor() {
    private val _detailsMap = mutableMapOf<Int, CategoryDetailed>()
    private val _keys = mutableSetOf<Int>()
    private var currIndex = 0

    val isReady: Boolean
        get() = _keys.isNotEmpty()

    suspend fun saveAll(detailsMap: Map<Int, CategoryDetailed>) {
        delay(3000)
        _detailsMap.putAll(detailsMap)
        _keys.addAll(_detailsMap.keys)
        log("finished saveData")
    }

    suspend fun getCategoryData(): Result<CategoryDetailed, Throwable> =
        runOperationCatching {
            delay(1000)
            log("getCategoryData - about to retrieve next")
            nextItem()
        }

    private fun nextItem(): CategoryDetailed {
        val retVal = _detailsMap[_keys.elementAt(currIndex++)]!!
        if (currIndex == _keys.size)
            currIndex = 0

        return retVal
    }
}

