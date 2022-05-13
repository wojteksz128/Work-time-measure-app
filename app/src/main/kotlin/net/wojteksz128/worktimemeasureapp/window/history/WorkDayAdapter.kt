package net.wojteksz128.worktimemeasureapp.window.history

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.*
import net.wojteksz128.worktimemeasureapp.databinding.ListItemHistoryWorkDayBinding
import net.wojteksz128.worktimemeasureapp.model.ComeEvent
import net.wojteksz128.worktimemeasureapp.model.WorkDay
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeUtils
import net.wojteksz128.worktimemeasureapp.util.livedata.RecyclerViewPeriodicUpdater
import net.wojteksz128.worktimemeasureapp.util.recyclerView.RecyclerViewItemClick
import net.wojteksz128.worktimemeasureapp.util.recyclerView.ViewHolderInformation
import net.wojteksz128.worktimemeasureapp.window.history.ComeEventsAdapter.ComeEventViewHolder
import net.wojteksz128.worktimemeasureapp.window.util.button.ExpandViewModel
import net.wojteksz128.worktimemeasureapp.window.util.recyclerView.ComeEventsRecyclerViewSwipeLogic

class WorkDayAdapter(
    private val context: Context,
    private val dateTimeUtils: DateTimeUtils,
    private val fragmentManager: FragmentManager,
    private val lifecycleOwner: LifecycleOwner,
    private val workDayItemListener: WorkDayItemListener
) : PagingDataAdapter<WorkDay, WorkDayAdapter.WorkDayViewHolder>(WorkDayEventsDiffCallback),
    RecyclerViewItemClick<WorkDay> {
    private val periodicUpdater = RecyclerViewPeriodicUpdater(this)

    @Suppress("UNUSED_PARAMETER")
    override var onItemClickListenerProvider: (WorkDay) -> (View) -> Unit
        get() = workDayItemListener::onWorkDayClicked
        set(value) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkDayViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = ListItemHistoryWorkDayBinding.inflate(inflater, parent, false)
        return WorkDayViewHolder(
            binding,
            context,
            fragmentManager,
            lifecycleOwner,
            dateTimeUtils,
            workDayItemListener::onWorkDayEventSelected
        )
    }

    override fun onBindViewHolder(holder: WorkDayViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setOnClickListener(onItemClickListenerProvider(it))
            holder.bind(it)
        }
    }

    override fun onViewAttachedToWindow(holder: WorkDayViewHolder) {
        super.onViewAttachedToWindow(holder)
        if (holder.binding.workDay?.isAllEventsEnded() != true) {
            periodicUpdater.addItem(holder.absoluteAdapterPosition)
            holder.syncUpdaterWith(periodicUpdater)
        }
    }

    override fun onViewDetachedFromWindow(holder: WorkDayViewHolder) {
        periodicUpdater.removeItem(holder.absoluteAdapterPosition)
        super.onViewDetachedFromWindow(holder)
    }


    class WorkDayViewHolder(
        val binding: ListItemHistoryWorkDayBinding,
        context: Context,
        private val fragmentManager: FragmentManager,
        private val lifecycleOwner: LifecycleOwner,
        private val dateTimeUtils: DateTimeUtils,
        private val selectionUpdater: (ComeEvent, ViewHolderInformation<ComeEventViewHolder>) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root), ClassTagAware {
        private val expandViewModel = ExpandViewModel()

        private val comeEventsAdapter = ComeEventsAdapter(dateTimeUtils)

        init {
            binding.apply {
                lifecycleOwner = this@WorkDayViewHolder.lifecycleOwner
                dateTimeUtils = this@WorkDayViewHolder.dateTimeUtils
                dayEventsList.apply {
                    adapter = comeEventsAdapter
                    layoutManager = object : LinearLayoutManager(context) {
                        override fun canScrollVertically() = false
                    }
                    (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
                    addItemDecoration(
                        DividerItemDecoration(
                            context,
                            DividerItemDecoration.VERTICAL
                        )
                    )
                }
                ComeEventsRecyclerViewSwipeLogic(context, selectionUpdater)
                    .attach(dayEventsList, fragmentManager)
                expandViewModel = this@WorkDayViewHolder.expandViewModel
            }
        }

        fun bind(workDay: WorkDay) {
            binding.workDay = workDay

            comeEventsAdapter.submitList(workDay.events)
        }

        fun syncUpdaterWith(anotherUpdater: RecyclerViewPeriodicUpdater) {
            comeEventsAdapter.syncUpdaterWith(anotherUpdater)
        }

        fun setOnClickListener(onItemClickListener: (View) -> Unit) {
            binding.dayLabelContainer.setOnClickListener(onItemClickListener)
        }
    }


    object WorkDayEventsDiffCallback : DiffUtil.ItemCallback<WorkDay>() {

        override fun areItemsTheSame(oldItem: WorkDay, newItem: WorkDay): Boolean {
            return oldItem.id!! == newItem.id!!
        }

        override fun areContentsTheSame(oldItem: WorkDay, newItem: WorkDay) =
            oldItem == newItem
    }

    interface WorkDayItemListener {
        fun onWorkDayEventSelected(
            comeEvent: ComeEvent,
            viewHolderInformation: ViewHolderInformation<ComeEventViewHolder>
        )

        fun onWorkDayClicked(workDay: WorkDay): (View) -> Unit = {}
    }
}

