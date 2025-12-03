package net.wojteksz128.worktimemeasureapp.util

import androidx.paging.PagingSource
import androidx.paging.PagingState
import net.wojteksz128.worktimemeasureapp.model.WorkDay

class TestPagingSource(private val data: List<WorkDay>) : PagingSource<Int, WorkDay>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, WorkDay> {
        return LoadResult.Page(data, null, null)
    }

    override fun getRefreshKey(state: PagingState<Int, WorkDay>): Int? {
        return null
    }
}