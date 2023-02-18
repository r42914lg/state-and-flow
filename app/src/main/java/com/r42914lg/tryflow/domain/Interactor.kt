package com.r42914lg.tryflow.domain

import com.r42914lg.tryflow.presentation.CategoryInteractor
import com.r42914lg.tryflow.presentation.StatsInteractor
import com.r42914lg.tryflow.utils.Result
import com.r42914lg.tryflow.utils.log
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

interface CategoryRepository {

    val progressFlow: Flow<Int>
    suspend fun requestNext()

    val sharedCategoryFlow: SharedFlow<Result<CategoryDetailed, Throwable>>
}

class CategoryInteractorImpl @Inject constructor(
    private val repository: CategoryRepository
) : CategoryInteractor {

    private lateinit var autoRefreshJob: Job

    override fun startDownload(): Flow<Int> = repository.progressFlow

    override suspend fun requestNext() {
        repository.requestNext()
    }

    override val sharedFlowCategoryData: SharedFlow<Result<CategoryDetailed, Throwable>>
        get() {
            log("CategoryInteractorImpl: reference to sharedFlow requested, returning: $repository.sharedCategoryFlow")
            return repository.sharedCategoryFlow
        }

    override suspend fun setAutoRefresh(isOn: Boolean) {
        if (isOn) {
            coroutineScope {
                autoRefreshJob = launch {
                    while (true) {
                        log("Auto refresh - requesting next...")
                        repository.requestNext()
                        delay(2000)
                    }
                }
            }
        } else {
            autoRefreshJob.cancel()
            log("Auto refresh job cancelled")
        }
    }
}

class StatsInteractorImpl @Inject constructor(
    private val repository: CategoryRepository
) : StatsInteractor {

    override val sharedFlowCategoryData: SharedFlow<Result<CategoryDetailed, Throwable>>
        get() {
            log("StatsInteractorImpl: reference to sharedFlow requested, returning: $repository.sharedCategoryFlow")
            return repository.sharedCategoryFlow
        }
}