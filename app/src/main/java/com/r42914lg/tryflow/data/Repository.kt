package com.r42914lg.tryflow.data

import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.r42914lg.tryflow.domain.*
import com.r42914lg.tryflow.utils.Result
import com.r42914lg.tryflow.utils.doOnError
import com.r42914lg.tryflow.utils.doOnSuccess
import com.r42914lg.tryflow.utils.log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val dispatcher: CoroutineDispatcher,
    private val categoryLocalDataSource: CategoryLocalDataSource,
    private val categoryRemoteDataSource: CategoryRemoteDataSource
) : CategoryRepository {

    private var repoIsReady = categoryLocalDataSource.isReady

    private val _progressFlow = flow {
        if (repoIsReady) {
            emit(100)
            return@flow
        }

        val catList: Result<List<Category>, Throwable>
        withContext(dispatcher) {
            catList = categoryRemoteDataSource.getCategories()
        }

        catList.doOnError {
            emit(-1)
        }.doOnSuccess { categoryList ->
            emit(1)

            val detailsMap = mutableMapOf<Int, CategoryDetailed>()
            var loadedCount = 0

            categoryList.forEach { category ->
                val detail: Result<CategoryDetailed, Throwable>
                withContext(dispatcher) {
                    detail = categoryRemoteDataSource.getDetails(category.id)
                }

                loadedCount++

                detail.doOnSuccess {
                    detailsMap[it.id] = it
                    emit(((loadedCount.toFloat() / categoryList.size) * 100).toInt() -1)
                }.doOnError {
                    log("error in CatDetails... just skip")
                }
            }

            categoryLocalDataSource.saveAll(detailsMap)

            emit(100)
        }
    }

    override val progressFlow: Flow<Int>
        get() = _progressFlow


    private val mutableSharedFlowCategoryData = MutableSharedFlow<Result<CategoryDetailed, Throwable>>()
    private val _sharedFlowCategoryData = mutableSharedFlowCategoryData
        .shareIn(
            ProcessLifecycleOwner.get().lifecycleScope,
            SharingStarted.WhileSubscribed(),
            3)

    override val sharedFlowCategoryData: SharedFlow<Result<CategoryDetailed, Throwable>>
        get() = _sharedFlowCategoryData

    override fun requestNext() {
        ProcessLifecycleOwner.get().lifecycleScope.launch {
            val detail: Result<CategoryDetailed, Throwable>
            withContext(dispatcher) {
                detail = categoryLocalDataSource.getNextItem()
            }
            mutableSharedFlowCategoryData.emit(detail)
        }
    }

    private lateinit var autoRefreshJob: Job
    private var pausedFlag = false

    override suspend fun setAutoRefresh(isOn: Boolean) = withContext(dispatcher) {
        if (isOn) {
            log("Auto-refresh is ON - start spinning")
            coroutineScope {
                autoRefreshJob = launch {
                    while (true) {

                        if (!pausedFlag) {
                            requestNext()
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

    override fun pauseAutoRefresh() {
        log("Auto-refresh PAUSING...")
        pausedFlag = true
    }

    override fun resumeAutoRefresh() {
        log("Auto-refresh RESUMING...")
        pausedFlag = false
    }
}