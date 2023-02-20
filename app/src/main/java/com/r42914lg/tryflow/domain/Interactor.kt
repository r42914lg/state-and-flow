package com.r42914lg.tryflow.domain

import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.r42914lg.tryflow.presentation.GetCategoryDataInteractor
import com.r42914lg.tryflow.presentation.GetProgressUseCase
import com.r42914lg.tryflow.utils.Result
import com.r42914lg.tryflow.utils.doOnError
import com.r42914lg.tryflow.utils.doOnSuccess
import com.r42914lg.tryflow.utils.log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

interface CategoryRepository {

    val repoIsReady: Boolean
    suspend fun getCategories(): Result<List<Category>, Throwable>
    suspend fun getDetails(id: Int): Result<CategoryDetailed, Throwable>

    suspend fun saveAll(detailsMap: Map<Int, CategoryDetailed>)
    fun requestNext()

    val catDetailsColdFlow: Flow<Result<CategoryDetailed, Throwable>>
}

class GetProgressUseCaseImpl @Inject constructor(
    private val repository: CategoryRepository
) : GetProgressUseCase {

    private val _progressFlow = flow {
        if (repository.repoIsReady) {
            emit(100)
            return@flow
        }

        val catList = repository.getCategories()

        catList.doOnError {
            emit(-1)
        }.doOnSuccess { categoryList ->
            emit(1)

            val detailsMap = mutableMapOf<Int, CategoryDetailed>()
            var loadedCount = 0

            categoryList.forEach { category ->
                val detail = repository.getDetails(category.id)
                loadedCount++

                detail.doOnSuccess {
                    detailsMap[it.id] = it
                    emit(((loadedCount.toFloat() / categoryList.size) * 100).toInt() -1)
                }.doOnError {
                    log("error in CatDetails... just skip")
                }
            }

            repository.saveAll(detailsMap)
            repository.requestNext()
            emit(100)
        }
    }

    override val progressFlow: Flow<Int>
        get() = _progressFlow
}

class GetCategoryDataInteractorImpl @Inject constructor(
    private val repository: CategoryRepository
) : GetCategoryDataInteractor {

    private lateinit var autoRefreshJob: Job
    private var pausedFlag = false

    override fun requestNext() {
        repository.requestNext()
    }

    override suspend fun setAutoRefresh(isOn: Boolean) {
        if (isOn) {
            log("Auto-refresh is ON - start spinning")
            coroutineScope {
                autoRefreshJob = launch {
                    while (true) {

                        if (!pausedFlag) {
                            repository.requestNext()
                            log("Requested next item...")
                        } else {
                            log("Pause flag is ON - just waiting 2000 ms...")
                        }

                        delay(2000)
                    }
                }
            }
        } else {
            log("Auto-refresh is OFF - canceling job... ")
            autoRefreshJob.cancel()
        }
    }

    override fun pauseAutoRefresh(isPaused: Boolean) {
        log("Auto-refresh pause flag = $isPaused")
        pausedFlag = isPaused
    }

    private val _sharedFlowCategoryData = repository.catDetailsColdFlow
        .shareIn(
            ProcessLifecycleOwner.get().lifecycleScope,
            SharingStarted.WhileSubscribed(),
            3)

    override val sharedFlowCategoryData: SharedFlow<Result<CategoryDetailed, Throwable>>
        get() = _sharedFlowCategoryData
}