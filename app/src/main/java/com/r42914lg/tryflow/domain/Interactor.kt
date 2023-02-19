package com.r42914lg.tryflow.domain

import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.r42914lg.tryflow.presentation.GetCategoryDataInteractor
import com.r42914lg.tryflow.presentation.GetProgressUseCase
import com.r42914lg.tryflow.utils.Result
import com.r42914lg.tryflow.utils.doOnError
import com.r42914lg.tryflow.utils.doOnSuccess
import com.r42914lg.tryflow.utils.log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import javax.inject.Inject

interface CategoryRepository {

    val repoIsReady: Boolean
    suspend fun getCategories(): Result<List<Category>, Throwable>
    suspend fun getDetails(id: Int): Result<CategoryDetailed, Throwable>
    suspend fun saveAll(detailsMap: Map<Int, CategoryDetailed>)
    suspend fun requestNext(): Result<CategoryDetailed, Throwable>
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
            emit(100)
        }
    }

    override val progressFlow: Flow<Int>
        get() = _progressFlow
}

class GetCategoryDataInteractorImpl @Inject constructor(
    private val repository: CategoryRepository
) : GetCategoryDataInteractor {

    private var autoRefreshIsOn = false
    private var nextItemRequested = false

    private val _dataFlow = flow {
        while (true) {
            if (autoRefreshIsOn || nextItemRequested) {
                nextItemRequested = false

                emit(repository.requestNext())
                log("Next item received from repository and emitted")
            }

            delay(2000)
        }
    }

    override fun requestNext() {
        nextItemRequested = true
    }

    override fun setAutoRefresh(isOn: Boolean) {
        autoRefreshIsOn = isOn
    }

    private val _sharedFlowCategoryData =
        _dataFlow.shareIn(
            ProcessLifecycleOwner.get().lifecycleScope,
            SharingStarted.WhileSubscribed(),
            3)

    override val sharedFlowCategoryData: SharedFlow<Result<CategoryDetailed, Throwable>>
        get() = _sharedFlowCategoryData
}