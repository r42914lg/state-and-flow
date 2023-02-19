package com.r42914lg.tryflow.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.r42914lg.tryflow.utils.doOnSuccess
import com.r42914lg.tryflow.utils.doOnError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val progressUseCase: GetProgressUseCase,
    private val getCategoryDataInteractor: GetCategoryDataInteractor,
) : ViewModel() {

    private val _autoRefreshStatus = MutableLiveData(false)
    val autoRefreshStatus: LiveData<Boolean>
        get() = _autoRefreshStatus

    fun onAutoRefreshClicked() {
        _autoRefreshStatus.value = !_autoRefreshStatus.value!!
        getCategoryDataInteractor.setAutoRefresh(_autoRefreshStatus.value!!)
    }

    private val _contentState: MutableStateFlow<ContentState> = MutableStateFlow(ContentState.Loading)
    val contentState: StateFlow<ContentState>
        get() = _contentState

    lateinit var downloadProgress: Flow<Int>

    init {
        viewModelScope.launch {
            downloadProgress = progressUseCase.progressFlow

            getCategoryDataInteractor.sharedFlowCategoryData.collect {
                it.doOnError { error ->
                    _contentState.emit(ContentState.Error(error))
                }.doOnSuccess { data ->
                    _contentState.emit(ContentState.Content(data))
                }
            }
        }
    }

    fun requestNext() {
        getCategoryDataInteractor.requestNext()
    }
}