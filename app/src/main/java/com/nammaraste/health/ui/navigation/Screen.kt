package com.nammaraste.health.ui.navigation

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object RoadList : Screen("road_list")
    object RoadDetail : Screen("road_detail/{roadId}") {
        fun createRoute(roadId: Int) = "road_detail/$roadId"
    }
    object ReportDamage : Screen("report_damage?roadId={roadId}") {
        fun createRoute(roadId: Int = -1) = "report_damage?roadId=$roadId"
    }
    object MapView : Screen("map_view")
    object Leaderboard : Screen("leaderboard")
    object ContractorProfile : Screen("contractor_profile/{name}") {
        fun createRoute(name: String) = "contractor_profile/$name"
    }
}
