package net.wojteksz128.worktimemeasureapp.window.util.recyclerView

import android.content.Context
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import net.wojteksz128.worktimemeasureapp.model.DomainModel
import net.wojteksz128.worktimemeasureapp.util.recyclerView.RecyclerLeftSwipeActionParam
import net.wojteksz128.worktimemeasureapp.util.recyclerView.RecyclerRightSwipeActionParam
import net.wojteksz128.worktimemeasureapp.util.recyclerView.RecyclerSwipeHelper

abstract class RecyclerViewSwipeLogic<Model, EntityLeftDialogFragment, EntityRightDialogFragment>(
    protected val context: Context,
    private val selectedModel: MutableLiveData<Model>,
    private val selectedModelPosition: MutableLiveData<Int>?,
    private val swipeLeftDialogFragmentClass: Class<EntityLeftDialogFragment>,
    private val swipeRightDialogFragmentClass: Class<EntityRightDialogFragment>
) where Model : DomainModel, EntityLeftDialogFragment : DialogFragment, EntityRightDialogFragment : DialogFragment {

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

    protected fun prepareSwipeLeftAction(fragmentManager: FragmentManager): (Model, Int) -> Unit =
        { entity: Model, position: Int ->
            fillSelectionProperties(entity, position)
            val newInstance = swipeLeftDialogFragmentClass.newInstance()
            newInstance.show(fragmentManager, swipeLeftDialogFragmentClass.toString())
        }

    protected fun prepareSwipeRightAction(fragmentManager: FragmentManager): (Model, Int) -> Unit =
        { entity: Model, position: Int ->
            fillSelectionProperties(entity, position)
            val newInstance = swipeRightDialogFragmentClass.newInstance()
            newInstance.show(fragmentManager, swipeRightDialogFragmentClass.toString())
        }

    private fun fillSelectionProperties(entity: Model, position: Int) {
        selectedModel.value = entity
        selectedModelPosition?.value = position
    }

    protected abstract fun extractEntity(viewHolder: RecyclerView.ViewHolder): Model
    protected abstract fun generateSwipeLeftActionParameters(fragmentManager: FragmentManager): RecyclerLeftSwipeActionParam<Model>
    protected abstract fun generateSwipeRightActionParameters(fragmentManager: FragmentManager): RecyclerRightSwipeActionParam<Model>
}
