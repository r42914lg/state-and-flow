package com.r42914lg.tryflow.ui

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.r42914lg.tryflow.R
import com.r42914lg.tryflow.domain.asString
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

class StatsFragment : Fragment() {

    private lateinit var tvOne: TextView
    private lateinit var tvTwo: TextView
    private lateinit var tvThree: TextView

    companion object {
        fun newInstance() = StatsFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_stats, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi(view)
        setupObservers()
    }

    private fun initUi(view: View) {
        tvOne = view.findViewById(R.id.tvOne)
        tvTwo = view.findViewById(R.id.tvTwo)
        tvThree = view.findViewById(R.id.tvThree)
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.categoryFlow.collect() {
                tvThree.text = tvTwo.text
                tvTwo.text = tvOne.text
                tvOne.text = it.title
            }
        }
    }
}