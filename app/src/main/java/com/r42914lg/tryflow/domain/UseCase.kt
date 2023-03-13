package com.r42914lg.tryflow.domain

import com.r42914lg.tryflow.presentation.GetCategoryFlowUseCase
import com.r42914lg.tryflow.presentation.GetProgressUseCase
import com.r42914lg.tryflow.presentation.RequestNextCategoryUseCase
import com.r42914lg.tryflow.presentation.SetAutorefreshUseCase
import com.r42914lg.tryflow.utils.Result
import kotlinx.coroutines.flow.*
import javax.inject.Inject

interface CategoryRepository {
    val progressFlow: Flow<Int>
    val sharedFlowCategoryData: SharedFlow<Result<CategoryDetailed, Throwable>>

    fun requestNext()
    suspend fun setAutoRefresh(isOn: Boolean)
    fun pauseAutoRefresh()
    fun resumeAutoRefresh()
}

class GetProgressUseCaseImpl @Inject constructor(
    private val repository: CategoryRepository
) : GetProgressUseCase {

    override fun execute(): Flow<Int> =
        repository.progressFlow
}

class GetCategoryFlowUseCaseImpl @Inject constructor(
    private val repository: CategoryRepository
) : GetCategoryFlowUseCase {

    override fun execute(): SharedFlow<Result<CategoryDetailed, Throwable>> =
        repository.sharedFlowCategoryData
}

class RequestNextCategoryUseCaseImpl @Inject constructor(
    private val repository: CategoryRepository
) : RequestNextCategoryUseCase {

    override suspend fun execute() {
        repository.requestNext()
    }
}

class SetAutorefreshUseCaseImpl @Inject constructor(
    private val repository: CategoryRepository
) : SetAutorefreshUseCase {

    override suspend fun execute(autoRefreshFlag: Boolean) {
        repository.setAutoRefresh(autoRefreshFlag)
    }

    override fun pauseAutoRefresh() {
        repository.pauseAutoRefresh()
    }

    override fun resumeAutorefresh() {
        repository.resumeAutoRefresh()
    }
}