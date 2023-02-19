package com.r42914lg.tryflow.presentation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    getCategoryDataInteractor: GetCategoryDataInteractor,
) : ViewModel()
{
    val categorySharedFlow = getCategoryDataInteractor.sharedFlowCategoryData
}