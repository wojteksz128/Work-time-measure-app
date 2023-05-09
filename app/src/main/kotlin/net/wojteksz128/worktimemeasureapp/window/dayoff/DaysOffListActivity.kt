package net.wojteksz128.worktimemeasureapp.window.dayoff

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.model.DayOff
import net.wojteksz128.worktimemeasureapp.model.fieldType.DayOffSource
import net.wojteksz128.worktimemeasureapp.model.fieldType.DayOffType
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

@Composable
private fun DayOffTile(dayOff: DayOff) {
    Surface {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DayOffTileIcon(type = dayOff.type)
            DayOffTileTexts(dayOff.name, dayOff.startDate, dayOff.finishDate)
        }
    }
}

@Composable
private fun DayOffTileIcon(type: DayOffType) {
    Box(
        modifier = Modifier.size(64.dp),
        contentAlignment = Alignment.Center,
    ) {
        when (type) {
            DayOffType.Weekend ->
                Icon(
                    painter = painterResource(R.drawable.ic_baseline_weekend_24),
                    contentDescription = stringResource(R.string.daysOff_item_icon_weekend_desc),
                    tint = Color.DarkGray.copy(alpha = 0.5f)
                )
            DayOffType.PersonalHoliday ->
                Icon(
                    painter = painterResource(R.drawable.ic_baseline_surfing_24),
                    contentDescription = stringResource(R.string.daysOff_item_icon_personalHoliday_desc),
                    tint = Color.Blue.copy(alpha = 0.5f)
                )
            DayOffType.PublicHoliday ->
                Icon(
                    painter = painterResource(R.drawable.ic_baseline_public_24),
                    contentDescription = stringResource(R.string.daysOff_item_icon_publicHoliday_desc),
                    tint = Color(0x8800A000)
                )
            else -> {}
        }
    }
}

@Composable
private fun DayOffTileTexts(name: String, startDate: LocalDate, finishDate: LocalDate) {
    Column(
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge,
        )

        val isTheSameDay = startDate.isEqual(finishDate)

        Spacer(modifier = Modifier.size(2.dp))
        CompositionLocalProvider(
            LocalContentColor provides Color.Black.copy(alpha = 0.5f)
        ) {
            Text(
                text = if (isTheSameDay) "$startDate" else "$startDate - $finishDate",
                style = MaterialTheme.typography.bodySmall,
            )
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