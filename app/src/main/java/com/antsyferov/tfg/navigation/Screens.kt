package com.antsyferov.tfg.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List

val screens = listOf(Screen.PublicationsList, Screen.MyArticlesList, Screen.Profile, Screen.ArticlesList, Screen.AddArticle, Screen.EditProfile, Screen.ArticleView)

val homeScreens = listOf(Screen.PublicationsList, Screen.MyArticlesList, Screen.Profile)

val sectionMap = mapOf(
    Screen.PublicationsList to listOf(Screen.PublicationsList, Screen.ArticlesList, Screen.AddArticle, Screen.ArticleView),
    Screen.MyArticlesList to listOf(Screen.MyArticlesList),
    Screen.Profile to listOf(Screen.Profile, Screen.EditProfile)
)

val iconMap = mapOf(
    Screen.PublicationsList to Icons.Filled.List,
    Screen.MyArticlesList to Icons.Filled.Favorite,
    Screen.Profile to Icons.Filled.AccountBox
)

fun isNotHomeScreen(route: String) =
    homeScreens.find { it.route == route } == null

fun isSubSection(homeScreen: Screen, currentRoute: String) =
    sectionMap[homeScreen].orEmpty().find { it.route == currentRoute } != null
