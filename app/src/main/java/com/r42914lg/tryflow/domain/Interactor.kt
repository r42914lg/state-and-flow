package com.r42914lg.tryflow.domain

import com.r42914lg.tryflow.presentation.CategoryInteractor
import com.r42914lg.tryflow.utils.Result
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

interface CategoryRepository {

    fun initAndGetProgressFlow(): Flow<Int>
    suspend fun requestNext()

    val sharedCategoryFlow: SharedFlow<Result<CategoryDetailed, Throwable>>
}

class CategoryInteractorImpl(
    private val repository: CategoryRepository
) : CategoryInteractor {

    private lateinit var autoRefreshJob: Job

    override fun startDownload(): Flow<Int> =
        repository.initAndGetProgressFlow()

    override suspend fun requestNext() {
        repository.requestNext()
    }

    override suspend fun getCategoryData() =
        repository.sharedCategoryFlow

    override suspend fun setAutoRefresh(isOn: Boolean) {
        if (isOn) {
            coroutineScope {
                autoRefreshJob = launch {
                    while (true) {
                        repository.requestNext()
                        delay(5000)
                    }
                }
            }
        } else
            autoRefreshJob.cancel()
    }
}