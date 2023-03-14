package com.r42914lg.tryflow

import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.r42914lg.tryflow.domain.CategoryDetailed
import com.r42914lg.tryflow.domain.Clue
import com.r42914lg.tryflow.presentation.GetCategoryFlowUseCase
import com.r42914lg.tryflow.presentation.GetProgressUseCase
import com.r42914lg.tryflow.presentation.RequestNextCategoryUseCase
import com.r42914lg.tryflow.presentation.SetAutorefreshUseCase
import com.r42914lg.tryflow.utils.Result
import kotlinx.coroutines.flow.*

/**
 * Mocked uses cases for testing purposes
 */

class GetProgressUseCaseTestImpl(private val values: List<Int>): GetProgressUseCase {
    override fun execute(): Flow<Int> = values.asFlow()
}

private val mutableSharedFlowCategoryData = MutableSharedFlow<Result<CategoryDetailed, Throwable>>()

class GetCategoryFlowUseCaseTestImpl: GetCategoryFlowUseCase {
    override fun execute(): SharedFlow<Result<CategoryDetailed, Throwable>> {
        return mutableSharedFlowCategoryData.shareIn(
            ProcessLifecycleOwner.get().lifecycleScope,
            SharingStarted.WhileSubscribed(),
            3)
    }
}

class SetAutorefreshUseCaseTestImpl: SetAutorefreshUseCase {
    override suspend fun execute(autoRefreshFlag: Boolean) {}
    override fun pauseAutoRefresh() {}
    override fun resumeAutorefresh() {}

}

class RequestNextCategoryUseCaseTestImpl : RequestNextCategoryUseCase {
    override suspend fun execute() {
        mutableSharedFlowCategoryData.emit(Result.Success(CONTENT))
    }
}

val CONTENT = CategoryDetailed(
    1,
    "title",
    1,
    listOf(Clue(101, "answer-1", "question-1"))
)


