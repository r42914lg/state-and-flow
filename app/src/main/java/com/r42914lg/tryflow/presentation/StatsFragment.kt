package com.r42914lg.tryflow.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.r42914lg.tryflow.R
import com.r42914lg.tryflow.utils.doOnError
import com.r42914lg.tryflow.utils.doOnSuccess
import com.r42914lg.tryflow.utils.log
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StatsFragment : Fragment() {

    private lateinit var tvOne: TextView
    private lateinit var tvTwo: TextView
    private lateinit var tvThree: TextView

    companion object {
        fun newInstance() = StatsFragment()
    }

    private val viewModel by viewModels<StatsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_stats, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow()
            }
        })

        initUi(view)
        setupObservers()
    }

    private fun initUi(view: View) {
        tvOne = view.findViewById(R.id.tvOne)
        tvTwo = view.findViewById(R.id.tvTwo)
        tvThree = view.findViewById(R.id.tvThree)

        tvOne.text = "cleared!!!"
        tvTwo.text = "cleared!!!"
        tvThree.text = "cleared!!!"
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.categorySharedFlow.collect() {
                log("Stats fragment consumed item: $it")
                it.doOnError { error ->
                    log(error.message ?: "Error item in flow")
                }.doOnSuccess { data ->
                    tvThree.text = tvTwo.text
                    tvTwo.text = tvOne.text
                    tvOne.text = data.title
                }
            }
        }
    }
}