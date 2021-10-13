package net.wojteksz128.worktimemeasureapp.window.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import net.wojteksz128.worktimemeasureapp.databinding.FragmentWorkDayDetailsBinding

class WorkDayDetailsFragment : Fragment() {
    private lateinit var binding: FragmentWorkDayDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWorkDayDetailsBinding.inflate(layoutInflater, container, false)

        return binding.root
    }
}