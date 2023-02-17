package com.r42914lg.tryflow.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.r42914lg.tryflow.domain.CategoryDetailed
import com.r42914lg.tryflow.utils.Result
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

interface StatsInteractor {
    suspend fun getCategoryData() : SharedFlow<Result<CategoryDetailed, Throwable>>
}

class StatsViewModel(
    private val interactor: StatsInteractor
) : ViewModel() {

    lateinit var categorySharedFlow: SharedFlow<Result<CategoryDetailed, Throwable>>

    init {
        viewModelScope.launch {
            categorySharedFlow = interactor.getCategoryData()
        }
    }
}