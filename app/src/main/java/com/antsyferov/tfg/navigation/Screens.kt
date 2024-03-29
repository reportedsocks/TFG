package com.antsyferov.tfg.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List

val screens = listOf(Screen.UsersList, Screen.UserView, Screen.PublicationsList, Screen.AddPublication, Screen.MyArticlesList, Screen.Profile, Screen.ArticlesList, Screen.AddArticle, Screen.EditProfile, Screen.ArticleView, Screen.ReviewsList, Screen.AddReview)

val homeScreens = listOf(Screen.PublicationsList, Screen.MyArticlesList, Screen.Profile, Screen.UsersList)

val sectionMap = mapOf(
    Screen.PublicationsList to listOf(Screen.PublicationsList, Screen.AddPublication, Screen.ArticlesList, Screen.AddArticle, Screen.ArticleView, Screen.ReviewsList, Screen.AddReview),
    Screen.MyArticlesList to listOf(Screen.MyArticlesList),
    Screen.Profile to listOf(Screen.Profile, Screen.EditProfile),
    Screen.UsersList to listOf(Screen.UsersList, Screen.UserView)
)

val iconMap = mapOf(
    Screen.PublicationsList to Icons.Filled.Home,
    Screen.MyArticlesList to Icons.Filled.Favorite,
    Screen.Profile to Icons.Filled.AccountBox,
    Screen.UsersList to Icons.Filled.List,
)

fun isNotHomeScreen(route: String) =
    homeScreens.find { it.route == route } == null

fun isSubSection(homeScreen: Screen, currentRoute: String) =
    sectionMap[homeScreen].orEmpty().find { it.route == currentRoute } != null
