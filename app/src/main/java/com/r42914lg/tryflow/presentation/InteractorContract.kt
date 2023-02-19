package com.r42914lg.tryflow.presentation

import com.r42914lg.tryflow.domain.CategoryDetailed
import com.r42914lg.tryflow.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface GetProgressUseCase {
    val progressFlow: Flow<Int>
}

interface GetCategoryDataInteractor {
    fun requestNext()
    fun setAutoRefresh(isOn: Boolean)
    val sharedFlowCategoryData : SharedFlow<Result<CategoryDetailed, Throwable>>
}