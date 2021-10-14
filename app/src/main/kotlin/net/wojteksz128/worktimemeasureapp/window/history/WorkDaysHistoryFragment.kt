package net.wojteksz128.worktimemeasureapp.window.history

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.liveData
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.databinding.FragmentWorkDaysHistoryBinding
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeUtils
import javax.inject.Inject

@AndroidEntryPoint
class WorkDaysHistoryFragment : Fragment(), ClassTagAware {
    private val viewModel: WorkDaysHistoryViewModel by viewModels()

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
            val workDayAdapter = WorkDayAdapter(requireContext(), dateTimeUtils) { workDay ->
                val workDayId = workDay.id
                {
                    if (workDayId != null) {
                        val bundle = Bundle()
                        bundle.putLong("workDayId", workDayId)
                        findNavController().navigate(R.id.viewWorkDayDetails, bundle)
                    } else {
                        Snackbar.make(
                            binding.root,
                            "Id cannot be null.",// TODO: 13.10.2021 Zamień na poprawny ciąg z zasobu
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }
            adapter = workDayAdapter.also { this@WorkDaysHistoryFragment.workDayAdapter = it }
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }

        Log.v(classTag, "onCreateView: Fill days list")
        viewModel.workDaysPager.liveData.observe(viewLifecycleOwner, {
            workDayAdapter.submitData(this.lifecycle, it)
        })

        return binding.root
    }
}