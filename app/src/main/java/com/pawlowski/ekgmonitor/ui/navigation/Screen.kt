package com.pawlowski.ekgmonitor.ui.navigation

sealed interface Screen {
    val name: String
    val directions: List<Direction>

    data object Chart : Screen {
        override val name: String = "Chart"
        override val directions: List<Direction> = ChartDirection.entries

        enum class ChartDirection(
            override val destination: Screen,
            override val popUpTo: Screen? = null,
            override val popUpToInclusive: Boolean = false,
        ) : Direction {
            SERVER_SETTINGS(
                destination = Settings,
            ),
        }
    }

    data object Settings : Screen {
        override val name: String = "Chart"
        override val directions: List<Direction> = SettingsDirection.entries

        enum class SettingsDirection(
            override val destination: Screen,
            override val popUpTo: Screen? = null,
            override val popUpToInclusive: Boolean = false,
        ) : Direction {
            CHART(
                destination = Chart,
                popUpTo = Chart,
                popUpToInclusive = true,
            ),
        }
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
