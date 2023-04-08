package com.antsyferov.tfg.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import com.antsyferov.tfg.R

sealed class Screen(
    val cleanRoute: String,
    @StringRes val title: Int,
    val icon: ImageVector? = null,
    val param: String? = null
    ) {

    val route get() = "$cleanRoute{$param}"

    object Profile : Screen("profile", R.string.profile_title, Icons.Filled.AccountBox)
    object PublicationsList : Screen("publicationsList", R.string.publications_list_title, Icons.Filled.List)
    object MyArticlesList : Screen("myArticles", R.string.my_articles_list_title, Icons.Filled.Favorite)
    object ArticlesList : Screen(
        "articles/",
        R.string.articles_list_title,
        param = "publication_id"
    )

    object AddArticle: Screen(
        "addArticle/",
        R.string.articles_list_title,
        param = "publication_id"
    )
}
