package com.pawlowski.ekgmonitor.ui.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format
import kotlinx.datetime.format.char

internal fun LocalDate.formatDate() =
    format(
        LocalDate.Format {
            dayOfMonth()
            char('.')
            monthNumber()
            char('.')
            year()
        },
    )

internal fun LocalTime.formatTime() =
    format(
        LocalTime.Format {
            hour()
            char(':')
            minute()
        },
    )

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
