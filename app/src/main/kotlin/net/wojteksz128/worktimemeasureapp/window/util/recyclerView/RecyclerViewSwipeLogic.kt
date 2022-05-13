package net.wojteksz128.worktimemeasureapp.window.util.recyclerView

import android.content.Context
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import net.wojteksz128.worktimemeasureapp.model.DomainModel
import net.wojteksz128.worktimemeasureapp.util.recyclerView.RecyclerLeftSwipeActionParam
import net.wojteksz128.worktimemeasureapp.util.recyclerView.RecyclerRightSwipeActionParam
import net.wojteksz128.worktimemeasureapp.util.recyclerView.RecyclerSwipeHelper
import net.wojteksz128.worktimemeasureapp.util.recyclerView.ViewHolderInformation

abstract class RecyclerViewSwipeLogic<Model, VH, EntityLeftDialogFragment, EntityRightDialogFragment>(
    protected val context: Context,
    private val selectionUpdater: (Model, ViewHolderInformation<VH>) -> Unit,
    private val swipeLeftDialogFragmentClass: Class<EntityLeftDialogFragment>,
    private val swipeRightDialogFragmentClass: Class<EntityRightDialogFragment>
) where Model : DomainModel,
        VH : ViewHolder,
        EntityLeftDialogFragment : DialogFragment,
        EntityRightDialogFragment : DialogFragment {

    open fun attach(recyclerView: RecyclerView, fragmentManager: FragmentManager) {
        val recyclerSwipeHelper =
            RecyclerSwipeHelper(
                generateSwipeLeftActionParameters(fragmentManager),
                generateSwipeRightActionParameters(fragmentManager),
                this::extractEntity
            )
        val itemTouchHelper = ItemTouchHelper(recyclerSwipeHelper)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    protected fun prepareSwipeLeftAction(fragmentManager: FragmentManager): (Model, ViewHolderInformation<VH>) -> Unit =
        { entity: Model, viewHolderInformation: ViewHolderInformation<VH> ->
            selectionUpdater.invoke(entity, viewHolderInformation)
            val newInstance = swipeLeftDialogFragmentClass.newInstance()
            newInstance.show(fragmentManager, swipeLeftDialogFragmentClass.toString())
        }

    protected fun prepareSwipeRightAction(fragmentManager: FragmentManager): (Model, ViewHolderInformation<VH>) -> Unit =
        { entity: Model, viewHolderInformation ->
            selectionUpdater.invoke(entity, viewHolderInformation)
            val newInstance = swipeRightDialogFragmentClass.newInstance()
            newInstance.show(fragmentManager, swipeRightDialogFragmentClass.toString())
        }

    protected abstract fun extractEntity(viewHolder: VH): Model
    protected abstract fun generateSwipeLeftActionParameters(fragmentManager: FragmentManager): RecyclerLeftSwipeActionParam<Model, VH>
    protected abstract fun generateSwipeRightActionParameters(fragmentManager: FragmentManager): RecyclerRightSwipeActionParam<Model, VH>
}
