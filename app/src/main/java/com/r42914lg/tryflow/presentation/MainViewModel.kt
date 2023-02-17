package com.r42914lg.tryflow.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.r42914lg.tryflow.domain.CategoryDetailed
import kotlinx.coroutines.launch
import com.r42914lg.tryflow.utils.Result
import com.r42914lg.tryflow.utils.doOnSuccess
import com.r42914lg.tryflow.utils.doOnError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

interface CategoryInteractor {
    fun startDownload(): Flow<Int>
    suspend fun requestNext()
    suspend fun getCategoryData() : SharedFlow<Result<CategoryDetailed, Throwable>>
    suspend fun setAutoRefresh(isOn: Boolean)
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val interactor: CategoryInteractor
) : ViewModel() {

    private val _contentState: MutableStateFlow<ContentState> = MutableStateFlow(ContentState.Loading)
    val contentState: StateFlow<ContentState>
        get() = _contentState

    lateinit var downloadProgress: Flow<Int>

    init {
        viewModelScope.launch {
            downloadProgress = interactor.startDownload()

            interactor.getCategoryData().collect {
                it.doOnError { error ->
                    _contentState.emit(ContentState.Error(error))
                }.doOnSuccess { data ->
                    _contentState.emit(ContentState.Content(data))
                }
            }
        }
    }

    fun requestNext() {
        viewModelScope.launch {
            interactor.requestNext()
        }
    }

    fun onAutoRefreshClicked(isOn: Boolean) {
        viewModelScope.launch {
            interactor.setAutoRefresh(isOn)
        }
    }
}