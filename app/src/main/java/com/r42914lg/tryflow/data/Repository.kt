package com.r42914lg.tryflow.data

import com.r42914lg.tryflow.domain.*
import com.r42914lg.tryflow.utils.Result
import com.r42914lg.tryflow.utils.doOnError
import com.r42914lg.tryflow.utils.doOnSuccess
import kotlinx.coroutines.flow.*

class CategoryRepositoryImpl(
    private val categoryLocalDataSource: CategoryLocalDataSource,
    private val categoryRemoteDataSource: CategoryRemoteDataSource,
) : CategoryRepository {

    override fun initAndGetProgressFlow(): Flow<Int> = flow {
            val catList = categoryRemoteDataSource.getCategories()

            catList.doOnError {
                emit(-1)
            }.doOnSuccess { categoryList ->
                emit(25)

                val detailsMap = mutableMapOf<Int, CategoryDetailed>()
                var step = 1

                categoryList.forEach { category ->
                    val detail = categoryRemoteDataSource.getDetails(category.id)

                    detail.doOnSuccess {
                        detailsMap[it.id] = it
                        emit(25 + (75 / categoryList.size) * step++)
                    }.doOnError {
                        step++
                    }
                }

                categoryLocalDataSource.saveAll(detailsMap)
                emit(100)
            }
        }

    private val _sharedCategoryFlow = MutableSharedFlow<Result<CategoryDetailed, Throwable>>()
    override val sharedCategoryFlow: SharedFlow<Result<CategoryDetailed, Throwable>>
        get() = _sharedCategoryFlow

    override suspend fun requestNext() {
        _sharedCategoryFlow.emit(categoryLocalDataSource.getCategoryData())
    }
}