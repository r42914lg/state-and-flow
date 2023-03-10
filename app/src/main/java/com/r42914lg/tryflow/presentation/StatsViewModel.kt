package com.r42914lg.tryflow.presentation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    getCategoryFlowUseCase: GetCategoryFlowUseCase,
    private val autorefreshUseCase: SetAutorefreshUseCase
) : ViewModel()
{
    val categorySharedFlow = getCategoryFlowUseCase.execute()

    fun onFragmentPaused() {
        autorefreshUseCase.pauseAutoRefresh()
    }

    fun onFragmentResumed() {
        autorefreshUseCase.resumeAutorefresh()
    }
}