package com.r42914lg.tryflow.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

import com.r42914lg.tryflow.utils.Result
import com.r42914lg.tryflow.utils.doOnError
import com.r42914lg.tryflow.utils.doOnSuccess
import com.r42914lg.tryflow.utils.getOrThrow

class CategoryRepository(
    private val categoryLocalDataSource: CategoryLocalDataSource,
    private val categoryRemoteDataSource: CategoryRemoteDataSource
) {

    fun init(): Flow<Int> = flow {
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

    suspend fun getCategoryData(): Flow<Result<CategoryDetailed, Throwable>> =
        categoryLocalDataSource.getCategoryData()
}