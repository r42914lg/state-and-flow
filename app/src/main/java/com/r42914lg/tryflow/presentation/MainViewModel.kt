package com.r42914lg.tryflow.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.r42914lg.tryflow.domain.CategoryDetailed
import com.r42914lg.tryflow.domain.Clue
import com.r42914lg.tryflow.utils.doOnSuccess
import com.r42914lg.tryflow.utils.doOnError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getProgressUseCase: GetProgressUseCase,
    private val getCategoryFlowUseCase: GetCategoryFlowUseCase,
    private val setAutorefreshUseCase: SetAutorefreshUseCase,
    private val requestNextCategoryUseCase: RequestNextCategoryUseCase,
) : ViewModel() {

    private var autoRefresh = false

    private val _state: MutableStateFlow<MainScreenState> =
        MutableStateFlow(MainScreenState.Loading(0))

    val state: StateFlow<MainScreenState>
        get() = _state

    init {
        viewModelScope.launch {
            getProgressUseCase.execute().collect {
                _state.emit(MainScreenState.Loading(it))
            }

            launch {
                delay(100)
                requestNextCategoryUseCase.execute()
            }

            getCategoryFlowUseCase.execute(viewModelScope).collect {
                it.doOnError { error ->
                    _state.emit(MainScreenState.Error(error))
                }.doOnSuccess { data ->
                    _state.emit(MainScreenState.Content(data))
                }
            }
        }
    }

    fun onEvent(event: MainScreenEvent) {
        when (event) {
            MainScreenEvent.FragmentPaused -> setAutorefreshUseCase.pauseAutoRefresh()
            MainScreenEvent.FragmentResumed -> setAutorefreshUseCase.resumeAutorefresh()
            MainScreenEvent.NextItemClick -> viewModelScope.launch { requestNextCategoryUseCase.execute() }
            MainScreenEvent.AutoRefreshClick -> onAutoRefreshClicked()
        }
    }

    private fun onAutoRefreshClicked() {
        autoRefresh = !autoRefresh
        viewModelScope.launch {
            _state.emit(MainScreenState.AutoRefreshStatus(autoRefresh))
            setAutorefreshUseCase.execute(autoRefresh)
        }
    }
}

sealed interface MainScreenEvent {
    object NextItemClick: MainScreenEvent
    object AutoRefreshClick: MainScreenEvent
    object FragmentPaused: MainScreenEvent
    object FragmentResumed: MainScreenEvent
}