package com.r42914lg.tryflow.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
    val sharedFlowCategoryData : SharedFlow<Result<CategoryDetailed, Throwable>>
    fun startDownload(): Flow<Int>
    suspend fun requestNext()
    suspend fun setAutoRefresh(isOn: Boolean)
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val interactor: CategoryInteractor
) : ViewModel() {

    private val _autoRefreshStatus = MutableLiveData(false)
    val autoRefreshStatus: LiveData<Boolean>
        get() = _autoRefreshStatus

    fun onAutoRefreshClicked() {
        _autoRefreshStatus.value = !_autoRefreshStatus.value!!
        viewModelScope.launch {
            interactor.setAutoRefresh(_autoRefreshStatus.value!!)
        }
    }

    private val _contentState: MutableStateFlow<ContentState> = MutableStateFlow(ContentState.Loading)
    val contentState: StateFlow<ContentState>
        get() = _contentState

    lateinit var downloadProgress: Flow<Int>

    init {
        viewModelScope.launch {
            downloadProgress = interactor.startDownload()

            interactor.sharedFlowCategoryData.collect {
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
}