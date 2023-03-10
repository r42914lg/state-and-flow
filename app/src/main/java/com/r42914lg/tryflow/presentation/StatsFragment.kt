package com.r42914lg.tryflow.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import com.r42914lg.tryflow.R
import com.r42914lg.tryflow.utils.doOnError
import com.r42914lg.tryflow.utils.doOnSuccess
import com.r42914lg.tryflow.utils.log
import com.r42914lg.tryflow.utils.observeIn
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onEach

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
                requireActivity().supportFragmentManager.popBackStack()
            }
        })

        initUi(view)
        setupObservers()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onFragmentPaused()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onFragmentResumed()
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
        viewModel.categorySharedFlow
            .onEach {
                log("Stats fragment consumed item: $it")
                it.doOnError { error ->
                    log(error.message ?: "Error item in flow")
                }.doOnSuccess { data ->
                    tvThree.text = tvTwo.text
                    tvTwo.text = tvOne.text
                    tvOne.text = data.title
                }
            }.observeIn(this@StatsFragment)
    }
}