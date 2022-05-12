package net.wojteksz128.worktimemeasureapp.window.history

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.liveData
import androidx.recyclerview.widget.SimpleItemAnimator
import dagger.hilt.android.AndroidEntryPoint
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.databinding.FragmentWorkDaysHistoryBinding
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeUtils
import javax.inject.Inject

@AndroidEntryPoint
class WorkDaysHistoryFragment : Fragment(), ClassTagAware {
    private val viewModel: WorkDaysHistoryViewModel by viewModels()
    private val selectedWorkDayViewModel: SelectedWorkDayViewModel by activityViewModels()

    @Inject
    lateinit var dateTimeUtils: DateTimeUtils

    private lateinit var binding: FragmentWorkDaysHistoryBinding
    private lateinit var workDayAdapter: WorkDayAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWorkDaysHistoryBinding.inflate(layoutInflater, container, false)

        binding.workDaysHistoryRv.apply {
            val workDayAdapter =
                WorkDayAdapter(requireContext(), dateTimeUtils, viewLifecycleOwner) { workDay ->
                    return@WorkDayAdapter {
                        selectedWorkDayViewModel.select(workDay)
                        findNavController().navigate(R.id.viewWorkDayDetails, bundleOf())
                    }
                }
            adapter = workDayAdapter.also { this@WorkDaysHistoryFragment.workDayAdapter = it }
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }

        Log.v(classTag, "onCreateView: Fill days list")
        viewModel.workDaysPager.liveData.observe(viewLifecycleOwner) {
            workDayAdapter.submitData(this.lifecycle, it)
        }

        return binding.root
    }
}