package com.antsyferov.tfg.util

import com.antsyferov.tfg.navigation.Screen

val screens = listOf(Screen.PublicationsList, Screen.MyArticlesList, Screen.Profile, Screen.ArticlesList, Screen.AddArticle)

val homeScreens = listOf(Screen.PublicationsList, Screen.MyArticlesList, Screen.Profile)

val sectionMap = mapOf(
    Screen.PublicationsList to listOf(Screen.PublicationsList, Screen.ArticlesList, Screen.AddArticle),
    Screen.MyArticlesList to listOf(Screen.MyArticlesList),
    Screen.Profile to listOf(Screen.Profile)
)

fun isNotHomeScreen(route: String) =
    homeScreens.find { it.route == route } == null

fun isSubSection(homeScreen: Screen, currentRoute: String) =
    sectionMap[homeScreen].orEmpty().find { it.route == currentRoute } != null
