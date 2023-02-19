package com.r42914lg.tryflow.data

import com.r42914lg.tryflow.domain.*
import com.r42914lg.tryflow.utils.Result
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val categoryLocalDataSource: CategoryLocalDataSource,
    private val categoryRemoteDataSource: CategoryRemoteDataSource
) : CategoryRepository {

    override val repoIsReady: Boolean
        get() = categoryLocalDataSource.isReady

    override suspend fun getCategories(): Result<List<Category>, Throwable> =
        categoryRemoteDataSource.getCategories()

    override suspend fun getDetails(id: Int): Result<CategoryDetailed, Throwable> =
        categoryRemoteDataSource.getDetails(id)

    override suspend fun saveAll(detailsMap: Map<Int, CategoryDetailed>) {
        categoryLocalDataSource.saveAll(detailsMap)
    }

    override suspend fun requestNext(): Result<CategoryDetailed, Throwable> =
        categoryLocalDataSource.getCategoryData()
}