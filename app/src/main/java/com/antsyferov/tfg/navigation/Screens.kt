package com.antsyferov.tfg.navigation

val screens = listOf(Screen.PublicationsList, Screen.MyArticlesList, Screen.Profile, Screen.ArticlesList, Screen.AddArticle, Screen.EditProfile)

val homeScreens = listOf(Screen.PublicationsList, Screen.MyArticlesList, Screen.Profile)

val sectionMap = mapOf(
    Screen.PublicationsList to listOf(Screen.PublicationsList, Screen.ArticlesList, Screen.AddArticle),
    Screen.MyArticlesList to listOf(Screen.MyArticlesList),
    Screen.Profile to listOf(Screen.Profile, Screen.EditProfile)
)

fun isNotHomeScreen(route: String) =
    homeScreens.find { it.route == route } == null

fun isSubSection(homeScreen: Screen, currentRoute: String) =
    sectionMap[homeScreen].orEmpty().find { it.route == currentRoute } != null
