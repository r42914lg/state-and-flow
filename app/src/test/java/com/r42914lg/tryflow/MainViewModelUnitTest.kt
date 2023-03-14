package com.r42914lg.tryflow

import app.cash.turbine.test
import com.r42914lg.tryflow.presentation.*
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelUnitTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun check_StateIsLoading_1_To_100_Then_Content() = runTest {

        val vm = MainViewModel(
            GetProgressUseCaseTestImpl(listOf(50,100)),
            GetCategoryFlowUseCaseTestImpl(),
            SetAutorefreshUseCaseTestImpl(),
            RequestNextCategoryUseCaseTestImpl()
        )

        vm.state.test {
            assertEquals(MainScreenState.Loading(0), awaitItem())
            assertEquals(MainScreenState.Loading(50), awaitItem())
            assertEquals(MainScreenState.Loading(100), awaitItem())
            assertEquals(CONTENT, awaitItem())
            cancel()
        }
    }
}