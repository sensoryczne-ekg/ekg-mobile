package com.pawlowski.ekgmonitor.ui.navigation

sealed interface Screen {
    val name: String
    val nameForNavigation: String
        get() = name

    val directions: List<Direction>

    data object Chart : Screen {
        override val name: String = "Chart"
        override val directions: List<Direction> = ChartDirection.entries

        enum class ChartDirection(
            override val destination: Screen,
            override val popUpTo: Screen? = null,
            override val popUpToInclusive: Boolean = false,
        ) : Direction {
            CHART_WITH_REFRESH(
                destination = Chart,
                popUpTo = Chart,
                popUpToInclusive = true,
            ),
            CHOOSE_PERIOD(
                destination = ChoosePeriod,
            ),
        }
    }

    data object ChoosePeriod : Screen {
        override val name: String = "ChoosePeriod"
        override val directions: List<Direction> = ChoosePeriodDirection.entries

        enum class ChoosePeriodDirection(
            override val destination: Screen,
            override val popUpTo: Screen? = null,
            override val popUpToInclusive: Boolean = false,
        ) : Direction
    }

    data class History(val from: Long, val to: Long) : Screen {
        companion object {
            const val NAME = "History/{from}/{to}"
        }

        override val name: String = NAME
        override val nameForNavigation: String get() = "History/$from/$to"
        override val directions: List<Direction> = HistoryDirection.entries

        enum class HistoryDirection(
            override val destination: Screen,
            override val popUpTo: Screen? = null,
            override val popUpToInclusive: Boolean = false,
        ) : Direction
    }

    // Needed for navigating back
    data object Back : Screen {
        override val name: String = "Back"
        override val directions: List<Direction> = emptyList()
    }
}

interface Direction {
    val destination: Screen
    val popUpTo: Screen?
        get() = null
    val popUpToInclusive: Boolean
        get() = false
}

object Back : Direction {
    override val destination: Screen = Screen.Back
}
