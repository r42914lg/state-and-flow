package com.r42914lg.tryflow.ui

import com.r42914lg.tryflow.domain.CategoryDetailed

sealed class ContentState {
    object Loading: ContentState()
    data class Error(val throwable: Throwable): ContentState()
    data class Content(val categoryDetailed: CategoryDetailed): ContentState()
}