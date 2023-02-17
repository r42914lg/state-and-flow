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
import androidx.lifecycle.lifecycleScope
import com.r42914lg.tryflow.R
import com.r42914lg.tryflow.domain.asString
import dagger.hilt.android.AndroidEntryPoint

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
            viewModel.requestNext()
        }

        btnAutoRefresh.setOnClickListener {
            btnAutoRefresh.text =
                if (btnAutoRefresh.text == "AUTO REFRESH ON")
                    "AUTO REFRESH OFF"
                else
                    "AUTO REFRESH ON"

            viewModel.onAutoRefreshClicked(btnAutoRefresh.text == "AUTO REFRESH ON")
        }

        btnStats.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container, StatsFragment.newInstance())
                .commitNow()
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.downloadProgress.collect() {
                progressBar.progress = it
                if (it == 100)
                    viewModel.requestNext()
            }

            viewModel.contentState.collect {
                when (it) {
                    is ContentState.Loading -> {
                        tvStatus.visibility = View.VISIBLE
                    }
                    is ContentState.Error -> {
                        tvStatus.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), "Error while loading", Toast.LENGTH_LONG)
                            .show()
                    }
                    is ContentState.Content -> {
                        tvStatus.visibility = View.INVISIBLE
                        val catDetail = it.categoryDetailed
                        tvId.text = catDetail.id.toString()
                        tvTitle.text = catDetail.title
                        tvCount.text = catDetail.cluesCount.toString()
                        tvClues.text = catDetail.clues.asString()
                    }
                }
            }
        }
    }
}