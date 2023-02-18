package com.r42914lg.tryflow.presentation

import androidx.lifecycle.ViewModel
import com.r42914lg.tryflow.domain.CategoryDetailed
import com.r42914lg.tryflow.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

interface StatsInteractor {
    val sharedFlowCategoryData : SharedFlow<Result<CategoryDetailed, Throwable>>
}

@HiltViewModel
class StatsViewModel @Inject constructor(
    interactor: StatsInteractor
) : ViewModel()
{
    val categorySharedFlow = interactor.sharedFlowCategoryData
}