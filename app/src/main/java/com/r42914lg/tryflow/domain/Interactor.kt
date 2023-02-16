package com.r42914lg.tryflow.domain

import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class CategoryInteractor (
    private val repository: CategoryRepository
) {

    private lateinit var autoRefreshJob: Job

    fun startDownload(): Flow<Int> =
        repository.init()

    suspend fun getCategoryData() =
        repository.getCategoryData()

    suspend fun setAutoRefresh(isOn: Boolean) {
        if (isOn) {
            coroutineScope {
                autoRefreshJob = launch {
                    while (true) {
                        repository.requestNext()
                        delay(5000)
                    }
                }
            }
        } else
            autoRefreshJob.cancel()
    }
}