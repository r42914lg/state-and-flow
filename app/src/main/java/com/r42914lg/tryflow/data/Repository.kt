package com.r42914lg.tryflow.data

import com.r42914lg.tryflow.domain.*
import com.r42914lg.tryflow.utils.Result
import com.r42914lg.tryflow.utils.doOnError
import com.r42914lg.tryflow.utils.doOnSuccess
import com.r42914lg.tryflow.utils.log
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val categoryLocalDataSource: CategoryLocalDataSource,
    private val categoryRemoteDataSource: CategoryRemoteDataSource,
) : CategoryRepository {

    private val _progressFlow = flow {
        if (categoryLocalDataSource.isReady) {
            emit(100)
            return@flow
        }

        val catList = categoryRemoteDataSource.getCategories()

        catList.doOnError {
            emit(-1)
        }.doOnSuccess { categoryList ->
            emit(1)

            val detailsMap = mutableMapOf<Int, CategoryDetailed>()
            var loadedCount = 0

            categoryList.forEach { category ->
                val detail = categoryRemoteDataSource.getDetails(category.id)
                loadedCount++

                detail.doOnSuccess {
                    detailsMap[it.id] = it
                    emit(((loadedCount.toFloat() / categoryList.size) * 100).toInt())
                }.doOnError {
                    log("error in CatDetails... just skip")
                }
            }

            categoryLocalDataSource.saveAll(detailsMap)
            emit(100)

            requestNext()
        }
    }

    override val progressFlow: Flow<Int>
        get() = _progressFlow

    private val _sharedCategoryFlow = MutableSharedFlow<Result<CategoryDetailed, Throwable>>(replay = 3)
    override val sharedCategoryFlow: SharedFlow<Result<CategoryDetailed, Throwable>>
        get() = _sharedCategoryFlow

    override suspend fun requestNext() {
        _sharedCategoryFlow.emit(categoryLocalDataSource.getCategoryData())
    }
}