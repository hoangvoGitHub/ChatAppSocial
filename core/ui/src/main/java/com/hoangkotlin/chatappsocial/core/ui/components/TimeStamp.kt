package com.hoangkotlin.chatappsocial.core.ui.components


import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import com.hoangkotlin.chatappsocial.core.common.utils.date_time.DateFormatType
import com.hoangkotlin.chatappsocial.core.common.utils.date_time.DateFormatType.DATE
import com.hoangkotlin.chatappsocial.core.common.utils.date_time.DateFormatType.TIME
import com.hoangkotlin.chatappsocial.core.common.utils.date_time.DateFormatter
import com.hoangkotlin.chatappsocial.core.common.utils.date_time.DefaultDateFormatter
import com.hoangkotlin.chatappsocial.core.common.utils.date_time.formatDate
import com.hoangkotlin.chatappsocial.core.common.utils.date_time.formatTime
import java.util.Date

/**
 * Represents a timestamp in the app, that's used primarily for channels and messages.
 *
 * @param date The date to show in the timestamp.
 * @param modifier Modifier for styling.
 * @param formatter The formatter that's used to format the date to a String.
 * @param formatType The type of formatting to provide - either a timestamp or a full date. We format the information
 * using [DATE] by default, as it's the most common behavior.
 */
@Composable
fun Timestamp(
    date: Date?,
    modifier: Modifier = Modifier,
    formatter: DateFormatter = DefaultDateFormatter(LocalContext.current),
    formatType: DateFormatType = DATE,
    fontWeight: FontWeight? = null,
) {

    val timestamp = when (formatType) {
        TIME -> formatter.formatTime(date)
        DATE -> formatter.formatDate(date)
    }

    Text(
        modifier = modifier,
        text = timestamp,
        style = MaterialTheme.typography.labelMedium.merge(fontWeight = fontWeight),
    )
}