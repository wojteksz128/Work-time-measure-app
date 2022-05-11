package net.wojteksz128.worktimemeasureapp.window.util.recyclerView

import android.content.Context
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.model.ComeEvent
import net.wojteksz128.worktimemeasureapp.util.recyclerView.RecyclerLeftSwipeActionParam
import net.wojteksz128.worktimemeasureapp.util.recyclerView.RecyclerRightSwipeActionParam
import net.wojteksz128.worktimemeasureapp.window.dialog.comeevent.DeleteComeEventDialogFragment
import net.wojteksz128.worktimemeasureapp.window.dialog.comeevent.EditComeEventDialogFragment
import net.wojteksz128.worktimemeasureapp.window.history.ComeEventsAdapter

class ComeEventsRecyclerViewSwipeLogic(
    context: Context,
    selectedComeEvent: MutableLiveData<ComeEvent>,
    selectedComeEventPosition: MutableLiveData<Int>
) :
    RecyclerViewSwipeLogic<ComeEvent, EditComeEventDialogFragment, DeleteComeEventDialogFragment>(
        context,
        selectedComeEvent,
        selectedComeEventPosition,
        EditComeEventDialogFragment::class.java,
        DeleteComeEventDialogFragment::class.java
    ) {

    override fun generateSwipeLeftActionParameters(fragmentManager: FragmentManager): RecyclerLeftSwipeActionParam<ComeEvent> =
        RecyclerLeftSwipeActionParam(
            R.color.teal_200,
            R.drawable.ic_baseline_edit_24,
            context,
            prepareSwipeLeftAction(fragmentManager)
        )


    override fun generateSwipeRightActionParameters(fragmentManager: FragmentManager): RecyclerRightSwipeActionParam<ComeEvent> =
        RecyclerRightSwipeActionParam(
            R.color.colorAlert,
            R.drawable.ic_baseline_delete_24,
            context,
            prepareSwipeRightAction(fragmentManager)
        )

    override fun extractEntity(viewHolder: RecyclerView.ViewHolder): ComeEvent =
        (viewHolder as ComeEventsAdapter.ComeEventViewHolder).binding.comeEvent!!
}