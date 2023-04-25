package com.r42914lg.tryflow.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    getCategoryFlowUseCase: GetCategoryFlowUseCase,
    private val autorefreshUseCase: SetAutorefreshUseCase,
) : ViewModel()
{
    val categorySharedFlow = getCategoryFlowUseCase.execute(viewModelScope)

    fun onFragmentPaused() {
        autorefreshUseCase.pauseAutoRefresh()
    }

    fun onFragmentResumed() {
        autorefreshUseCase.resumeAutorefresh()
    }
}