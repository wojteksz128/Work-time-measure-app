package net.wojteksz128.worktimemeasureapp.window.dayoff

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.model.DayOff
import net.wojteksz128.worktimemeasureapp.model.fieldType.DayOffSource
import net.wojteksz128.worktimemeasureapp.model.fieldType.DayOffType
import net.wojteksz128.worktimemeasureapp.ui.dayoff.DayOffTile
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import org.threeten.bp.LocalDate
import org.threeten.bp.Month
import java.util.UUID

@AndroidEntryPoint
class DaysOffListActivity : AppCompatActivity(), ClassTagAware {
    private val viewModel: DaysOffListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val daysOff by viewModel.daysOff.observeAsState(listOf())
            Content(daysOff)
        }
    }
}

@Composable
private fun Content(daysOff: List<DayOff>) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorResource(R.color.background)
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(
                space = 2.dp,
                alignment = Alignment.Top,
            )
        ) {
            itemsIndexed(
                items = daysOff
            ) { _, dayOff ->
                DayOffTile(dayOff)
            }
        }
    }
}

@Preview
@Composable
private fun PreviewContent() {
    val sample = listOf(
        DayOff(
            1,
            UUID.randomUUID().toString(),
            DayOffType.PublicHoliday,
            "Test",
            LocalDate.of(2020, Month.JANUARY, 1),
            LocalDate.of(2020, Month.JANUARY, 1),
            DayOffSource.ExternalAPI
        ),
        DayOff(
            2,
            UUID.randomUUID().toString(),
            DayOffType.PersonalHoliday,
            "Test",
            LocalDate.of(2020, Month.JANUARY, 6),
            LocalDate.of(2020, Month.JANUARY, 8),
            DayOffSource.ExternalAPI
        ),
        DayOff(
            1,
            UUID.randomUUID().toString(),
            DayOffType.Weekend,
            "Test",
            LocalDate.of(2020, Month.JANUARY, 1),
            LocalDate.of(2020, Month.JANUARY, 1),
            DayOffSource.ExternalAPI
        ),
        DayOff(
            2,
            UUID.randomUUID().toString(),
            DayOffType.Other,
            "Test",
            LocalDate.of(2020, Month.JANUARY, 6),
            LocalDate.of(2020, Month.JANUARY, 8),
            DayOffSource.ExternalAPI
        ),
    )
    Content(sample)
}