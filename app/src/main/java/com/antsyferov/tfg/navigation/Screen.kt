package com.antsyferov.tfg.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import com.antsyferov.tfg.R

sealed class Screen(val route: String, @StringRes val resourceId: Int, val icon: ImageVector) {
    object Profile : Screen("profile", R.string.profile_title, Icons.Filled.AccountBox)
    object PublicationsList : Screen("publicationsList", R.string.publications_list_title, Icons.Filled.List)
    object MyArticlesList : Screen("myArticlesList", R.string.my_articles_list_title, Icons.Filled.Favorite)
}
