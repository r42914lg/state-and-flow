package com.r42914lg.tryflow.presentation

import com.r42914lg.tryflow.domain.CategoryDetailed
import com.r42914lg.tryflow.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface GetProgressUseCase {
    fun execute(): Flow<Int>
}

interface GetCategoryFlowUseCase {
    fun execute() : SharedFlow<Result<CategoryDetailed, Throwable>>
}

interface SetAutorefreshUseCase {
    suspend fun execute(autoRefreshFlag: Boolean)
    fun pauseAutoRefresh()
    fun resumeAutorefresh()
}

interface RequestNextCategoryUseCase {
    suspend fun execute()
}