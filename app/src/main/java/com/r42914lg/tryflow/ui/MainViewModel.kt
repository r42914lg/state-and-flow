package com.r42914lg.tryflow.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

import com.r42914lg.tryflow.domain.CategoryInteractor
import com.r42914lg.tryflow.utils.doOnSuccess
import com.r42914lg.tryflow.utils.doOnError
import kotlinx.coroutines.flow.*

class MainViewModel(
    private val interactor: CategoryInteractor
) : ViewModel() {

    private val _contentState: MutableStateFlow<ContentState> = MutableStateFlow(ContentState.Loading)
    val contentState: StateFlow<ContentState>
        get() = _contentState

    lateinit var downloadProgress: Flow<Int>

    init {
        viewModelScope.launch {
            interactor.getCategoryData().collect {
                it.doOnError { error ->
                    _contentState.emit(ContentState.Error(error))
                }.doOnSuccess { data ->
                    _contentState.emit(ContentState.Content(data))
                }
            }

            downloadProgress = interactor.startDownload()
        }
    }

    fun requestNext() {
        interactor.requestNext()
    }

    fun onAutoRefreshClicked(isOn: Boolean) {
        viewModelScope.launch {
            interactor.setAutoRefresh(isOn)
        }
    }
}