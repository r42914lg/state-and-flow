package com.r42914lg.tryflow.presentation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val getCategoryDataInteractor: GetCategoryDataInteractor,
) : ViewModel()
{
    val categorySharedFlow = getCategoryDataInteractor.sharedFlowCategoryData

    fun onFragmentPaused() {
        getCategoryDataInteractor.pauseAutoRefresh(true)
    }

    fun onFragmentResumed() {
        getCategoryDataInteractor.pauseAutoRefresh(false)
    }
}