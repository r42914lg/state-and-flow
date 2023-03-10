package com.r42914lg.tryflow.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.r42914lg.tryflow.R
import com.r42914lg.tryflow.domain.CategoryDetailed
import com.r42914lg.tryflow.domain.asString
import com.r42914lg.tryflow.utils.log
import com.r42914lg.tryflow.utils.observeIn
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment : Fragment() {

    private lateinit var progressBar: ProgressBar
    private lateinit var btnNextItem: Button
    private lateinit var btnAutoRefresh: Button
    private lateinit var btnStats: Button
    private lateinit var tvId: TextView
    private lateinit var tvTitle: TextView
    private lateinit var tvCount: TextView
    private lateinit var tvClues: TextView
    private lateinit var tvStatus: TextView

    companion object {
        fun newInstance() = MainFragment()
    }

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi(view)
        setupObservers()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onEvent(MainScreenEvent.FragmentPaused)
    }

    override fun onResume() {
        super.onResume()
        viewModel.onEvent(MainScreenEvent.FragmentResumed)
    }

    private fun initUi(view: View) {
        progressBar = view.findViewById(R.id.progress_horizontal)
        btnNextItem = view.findViewById(R.id.btn_next)
        btnAutoRefresh = view.findViewById(R.id.btn_auto_refresh)
        btnStats = view.findViewById(R.id.btn_stats)
        tvId = view.findViewById(R.id.detail_category_id)
        tvTitle = view.findViewById(R.id.detail_category_title)
        tvCount = view.findViewById(R.id.detail_clues_count)
        tvClues = view.findViewById(R.id.detail_clues)
        tvStatus = view.findViewById(R.id.status_bar)

        btnNextItem.setOnClickListener {
            viewModel.onEvent(MainScreenEvent.NextItemClick)
        }

        btnAutoRefresh.setOnClickListener {
            viewModel.onEvent(MainScreenEvent.AutoRefreshClick)
        }

        btnStats.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container, StatsFragment.newInstance())
                .addToBackStack("Stats")
                .commit()
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.state
                    .onEach {
                        log("Main fragment consumed item: $it")
                        when (it) {
                            is MainScreenState.Loading -> renderLoading(it)
                            is MainScreenState.Error -> renderError(it)
                            is MainScreenState.Content -> renderContent(it)
                            is MainScreenState.AutoRefreshStatus -> renderAutoRefresh(it)
                        }
                    }
                    .observeIn(this@MainFragment)
            }
        }
    }

    private fun renderLoading(state: MainScreenState.Loading) {
        if (state.progress < 100) {
            tvStatus.visibility = View.VISIBLE
            progressBar.progress = state.progress
        } else {
            tvStatus.visibility = View.INVISIBLE
            progressBar.visibility = View.INVISIBLE
        }

        btnAutoRefresh.isEnabled = false
        btnNextItem.isEnabled = false
        btnStats.isEnabled = false
    }

    private fun renderError(state: MainScreenState.Error) {
        Toast.makeText(
            requireContext(),
            "Error while loading: ${state.throwable.message}",
            Toast.LENGTH_LONG).show()

        tvStatus.visibility = View.INVISIBLE
        btnAutoRefresh.isEnabled = true
        btnStats.isEnabled = true
        btnNextItem.isEnabled = true
    }

    private fun renderContent(state: MainScreenState.Content) {
        val catDetail = state.categoryDetailed

        tvId.text = catDetail.id.toString()
        tvTitle.text = catDetail.title
        tvCount.text = catDetail.cluesCount.toString()
        tvClues.text = catDetail.clues.asString()

        btnAutoRefresh.isEnabled = true
        btnStats.isEnabled = true
        btnNextItem.isEnabled = true
        tvStatus.visibility = View.INVISIBLE
    }

    private fun renderAutoRefresh(status: MainScreenState.AutoRefreshStatus) {
        btnAutoRefresh.text = if (status.autoRefresh) "AUTO-REFRESH ON" else "AUTO-REFRESH OFF"
    }
}

sealed interface MainScreenState {
    data class Loading(val progress: Int): MainScreenState
    data class Error(val throwable: Throwable): MainScreenState
    data class Content(val categoryDetailed: CategoryDetailed): MainScreenState
    data class AutoRefreshStatus(val autoRefresh: Boolean): MainScreenState
}