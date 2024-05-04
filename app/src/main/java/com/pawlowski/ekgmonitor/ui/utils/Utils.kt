package com.pawlowski.ekgmonitor.ui.utils

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import kotlinx.datetime.format.char

internal fun LocalDateTime.formatDateTime() =
    format(
        LocalDateTime.Format {
            dayOfMonth()
            char('.')
            monthNumber()
            char('.')
            year()
            char(' ')
            hour()
            char(':')
            minute()
            char(':')
            second()
        },
    )
