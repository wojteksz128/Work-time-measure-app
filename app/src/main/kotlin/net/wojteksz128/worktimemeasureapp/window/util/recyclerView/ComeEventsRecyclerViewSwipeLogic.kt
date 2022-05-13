package net.wojteksz128.worktimemeasureapp.window.util.recyclerView

import android.content.Context
import androidx.fragment.app.FragmentManager
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.model.ComeEvent
import net.wojteksz128.worktimemeasureapp.util.recyclerView.RecyclerLeftSwipeActionParam
import net.wojteksz128.worktimemeasureapp.util.recyclerView.RecyclerRightSwipeActionParam
import net.wojteksz128.worktimemeasureapp.util.recyclerView.ViewHolderInformation
import net.wojteksz128.worktimemeasureapp.window.dialog.comeevent.DeleteComeEventDialogFragment
import net.wojteksz128.worktimemeasureapp.window.dialog.comeevent.EditComeEventDialogFragment
import net.wojteksz128.worktimemeasureapp.window.history.ComeEventsAdapter.ComeEventViewHolder

class ComeEventsRecyclerViewSwipeLogic(
    context: Context,
    selectionUpdater: (ComeEvent, ViewHolderInformation<ComeEventViewHolder>) -> Unit
) :
    RecyclerViewSwipeLogic<ComeEvent, ComeEventViewHolder, EditComeEventDialogFragment, DeleteComeEventDialogFragment>(
        context,
        selectionUpdater,
        EditComeEventDialogFragment::class.java,
        DeleteComeEventDialogFragment::class.java
    ) {

    override fun generateSwipeLeftActionParameters(fragmentManager: FragmentManager): RecyclerLeftSwipeActionParam<ComeEvent, ComeEventViewHolder> =
        RecyclerLeftSwipeActionParam(
            R.color.teal_200,
            R.drawable.ic_baseline_edit_24,
            context,
            prepareSwipeLeftAction(fragmentManager)
        )


    override fun generateSwipeRightActionParameters(fragmentManager: FragmentManager): RecyclerRightSwipeActionParam<ComeEvent, ComeEventViewHolder> =
        RecyclerRightSwipeActionParam(
            R.color.colorAlert,
            R.drawable.ic_baseline_delete_24,
            context,
            prepareSwipeRightAction(fragmentManager)
        )

    override fun extractEntity(viewHolder: ComeEventViewHolder): ComeEvent =
        viewHolder.binding.comeEvent!!
}